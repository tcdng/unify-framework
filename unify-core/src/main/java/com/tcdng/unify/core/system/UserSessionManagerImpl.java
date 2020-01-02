/*
 * Copyright 2018-2020 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.core.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreSessionAttributeConstants;
import com.tcdng.unify.core.SessionAttributeValueConstants;
import com.tcdng.unify.core.SessionContext;
import com.tcdng.unify.core.UnifyContainer;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Broadcast;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.system.entities.UserSessionTracking;
import com.tcdng.unify.core.system.entities.UserSessionTrackingQuery;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Default implementation of application user session manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_USERSESSIONMANAGER)
public class UserSessionManagerImpl extends AbstractBusinessService implements UserSessionManager {

    private Map<String, UserSession> userSessions;

    public UserSessionManagerImpl() {
        userSessions = new ConcurrentHashMap<String, UserSession>();
    }

    @Override
    public List<UserSessionTracking> findUserSessions(UserSessionTrackingQuery userSessionQuery) throws UnifyException {
        return db().listAll(userSessionQuery);
    }

    @Override
    public UserSessionTracking findUserSession(String sessionId) throws UnifyException {
        return db().list(UserSessionTracking.class, sessionId);
    }

    @Override
    public int countUserSessions(UserSessionTrackingQuery userSessionQuery) throws UnifyException {
        return db().countAll(userSessionQuery);
    }

    @Override
    public UserSession getUserSession(String sessionId) throws UnifyException {
        if (sessionId != null) {
            return userSessions.get(sessionId);
        }
        return null;
    }

    @Override
    public void addUserSession(UserSession userSession) throws UnifyException {
        SessionContext sessionContext = userSession.getSessionContext();
        UserSessionTracking userSessionTracking = new UserSessionTracking();
        userSessionTracking.setSessionId(sessionContext.getId());
        userSessionTracking.setRemoteHost(sessionContext.getRemoteHost());
        userSessionTracking.setRemoteAddress(sessionContext.getRemoteAddress());
        userSessionTracking.setRemoteUser(sessionContext.getRemoteUser());
        userSessionTracking.setNode(getNodeId());
        db().create(userSessionTracking);
        userSessions.put(sessionContext.getId(), userSession);
    }

    @Override
    public void removeUserSession(UserSession userSession) throws UnifyException {
        SessionContext sessionContext = userSession.getSessionContext();
        db().delete(UserSessionTracking.class, sessionContext.getId());
        userSessions.remove(sessionContext.getId());
    }

    @Override
    public void updateCurrentSessionLastAccessTime() throws UnifyException {
        getSessionContext().setLastAccessTime(db().getNow());
    }

    @Override
    public void broadcast(String sessionId, String attribute, Object value) throws UnifyException {
        broadcast(userSessions.get(sessionId), attribute, value);
    }

    @Override
    public void broadcast(String attribute, Object value) throws UnifyException {
        for (UserSession session : userSessions.values()) {
            broadcast(session, attribute, value);
        }
    }

    @Override
    public void login(UserToken userToken) throws UnifyException {
        SessionContext sessionContext = getRequestContext().getSessionContext();

        // Add user session if not existing
        if (getUserSession(sessionContext.getId()) == null) {
            addUserSession(new LocalUserSession(sessionContext));
        }

        // Update user session database record
        String userLoginId = userToken.getUserLoginId();
        String sessionId = sessionContext.getId();
        db().updateAll(new UserSessionTrackingQuery().id(sessionId), new Update().add("userLoginId", userLoginId)
                .add("userLoginId", userToken.getUserLoginId()).add("userName", userToken.getUserName()));

        if (!userToken.isAllowMultipleLogin()) {
            List<String> sessionIdList = db().valueList(String.class, "sessionId",
                    new UserSessionTrackingQuery().userLoginId(userLoginId).idNot(sessionId));

            if (!sessionIdList.isEmpty()) {
                // Log other user sessions out
                db().updateAll(new UserSessionTrackingQuery().idAmongst(sessionIdList),
                        new Update().add("userLoginId", null).add("userLoginId", null).add("userName", null));

                forceLogout(sessionIdList.toArray(new String[sessionIdList.size()]));
            }
        }

        // Update session context
        sessionContext.setUserToken(userToken);
    }

    @Override
    public void logout(boolean clearCompleteSession) throws UnifyException {
        if (clearCompleteSession) {
            logOut(userSessions.get(getRequestContext().getSessionContext().getId()));
        } else {
            getRequestContext().getSessionContext().setUserToken(null);
        }
    }

    @Override
    public void logout(String sessionId) throws UnifyException {
        logOut(userSessions.get(sessionId));
    }

    @Broadcast
    @Override
    public void forceLogout(String... sessionIds) throws UnifyException {
        // Force logout specific sessions in this node
        for (String otherSessionId : sessionIds) {
            logout(otherSessionId);
            UserSession userSession = userSessions.get(otherSessionId);
            if (userSession != null) {
                userSession.getSessionContext().setAttribute(UnifyCoreSessionAttributeConstants.FORCE_LOGOUT,
                        SessionAttributeValueConstants.FORCE_LOGOUT_NO_MULTIPLE_LOGIN);
            }
        }
    }

    @Periodic(PeriodicType.SLOWEST)
    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public void performUserSessionHouseKeeping(TaskMonitor taskMonitor) throws UnifyException {
        // Update active session records and remove inactive ones
        Date now = db().getNow();
        List<String> activeSessionList = new ArrayList<String>();
        int expirationInSeconds = getContainerSetting(int.class, UnifyCorePropertyConstants.APPLICATION_SESSION_TIMEOUT,
                UnifyContainer.DEFAULT_APPLICATION_SESSION_TIMEOUT);
        expirationInSeconds = expirationInSeconds + expirationInSeconds / 5;
        Date expiryTime = CalendarUtils.getDateWithOffset(now, -(expirationInSeconds * 1000));
        for (UserSession userSession : userSessions.values()) {
            SessionContext sessionContext = userSession.getSessionContext();
            if (sessionContext.getLastAccessTime() == null || expiryTime.before(sessionContext.getLastAccessTime())) {
                activeSessionList.add(sessionContext.getId());
            } else {
                userSessions.remove(sessionContext.getId());
            }
        }

        if (!activeSessionList.isEmpty()) {
            db().updateAll(new UserSessionTrackingQuery().idAmongst(activeSessionList),
                    new Update().add("node", getNodeId()).add("lastAccessTime", now));
        }

        if (grabClusterMasterLock()) {
            // Delete inactive session
            db().deleteAll(new UserSessionTrackingQuery().expired(expiryTime));
        }
    }

    private void broadcast(UserSession userSession, String attribute, Object value) throws UnifyException {
        if (userSession != null) {
            if (UnifyCoreSessionAttributeConstants.FORCE_LOGOUT.equals(attribute)) {
                logOut(userSession);
            }
            userSession.getSessionContext().setAttribute(attribute, value);
        }
    }

    private void logOut(UserSession userSession) throws UnifyException {
        if (userSession != null) {
            SessionContext sessionContext = userSession.getSessionContext();
            db().updateAll(new UserSessionTrackingQuery().id(sessionContext.getId()),
                    new Update().add("userLoginId", null).add("userName", null));
            sessionContext.setUserToken(null);
            sessionContext.removeAllAttributes();
        }
    }

    private class LocalUserSession implements UserSession {

        private SessionContext sessionContext;

        public LocalUserSession(SessionContext sessionContext) {
            this.sessionContext = sessionContext;
        }

        @Override
        public String getRemoteAddress() {
            return sessionContext.getRemoteAddress();
        }

        @Override
        public String getRemoteHost() {
            return sessionContext.getRemoteHost();
        }

        @Override
        public String getRemoteUser() {
            return sessionContext.getRemoteUser();
        }

        @Override
        public SessionContext getSessionContext() {
            return sessionContext;
        }
    }
}
