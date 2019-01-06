/*
 * Copyright 2018-2019 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.system.entities.UserSessionTracking;
import com.tcdng.unify.core.system.entities.UserSessionTrackingQuery;

/**
 * Manages user sessions.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UserSessionManager extends BusinessService {

    /**
     * Finds user sessions that match supplied criteria.
     * 
     * @param userSessionQuery
     *            the search criteria
     * @return list of user sessions
     * @throws UnifyException
     *             if an error occurs
     */
    List<UserSessionTracking> findUserSessions(UserSessionTrackingQuery userSessionQuery) throws UnifyException;

    /**
     * Finds user session data by ID
     * 
     * @param sessionId
     *            the session ID
     * @return the user session data
     * @throws UnifyException
     *             if an error occurs
     */
    UserSessionTracking findUserSession(String sessionId) throws UnifyException;

    /**
     * Counts user sessions that match supplied criteria.
     * 
     * @param userSessionQuery
     *            the search criteria
     * @return session count
     * @throws UnifyException
     *             if an error occurs
     */
    int countUserSessions(UserSessionTrackingQuery userSessionQuery) throws UnifyException;

    /**
     * Gets a user session.
     * 
     * @param sessionId
     *            the session ID
     * @return the user session if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    UserSession getUserSession(String sessionId) throws UnifyException;

    /**
     * Adds a new user session.
     * 
     * @param userSession
     *            the user session
     * @throws UnifyException
     *             if an error occurs
     */
    void addUserSession(UserSession userSession) throws UnifyException;

    /**
     * Removes a user session.
     * 
     * @param userSession
     *            the user session object
     * @throws UnifyException
     *             if an error occurs
     */
    void removeUserSession(UserSession userSession) throws UnifyException;

    /**
     * Updates current session last access time.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void updateCurrentSessionLastAccessTime() throws UnifyException;

    /**
     * Broadcasts attribute value to particular session.
     * 
     * @param sessionId
     *            the session ID
     * @param attribute
     *            the attribute
     * @param value
     *            the value to broadcast
     * @throws UnifyException
     *             if an error occurs
     */
    void broadcast(String sessionId, String attribute, Object value) throws UnifyException;

    /**
     * Broadcasts attribute value to all sessions.
     * 
     * @param attribute
     *            the attribute
     * @param value
     *            the value to broadcast
     * @throws UnifyException
     *             if an error occurs
     */
    void broadcast(String attribute, Object value) throws UnifyException;

    /**
     * Log in user into the current session context.
     * 
     * @param userToken
     *            the user token
     * @throws UnifyException
     *             if multiple login is not allowed and user is already logged-in
     */
    void logIn(UserToken userToken) throws UnifyException;

    /**
     * Log out current user from current session context.
     * 
     * @param clearCompleteSession
     *            indicates if session should be completed cleared, otherwise only
     *            user token is cleared from session context
     * @throws UnifyException
     *             if an error occurs
     */
    void logOut(boolean clearCompleteSession) throws UnifyException;

    /**
     * Log out user session out.
     * 
     * @param sessionId
     *            the user session ID
     * @throws UnifyException
     *             if an error occurs
     */
    void logOut(String sessionId) throws UnifyException;

    /**
     * Force logout sessions.
     * 
     * @param sessionIds
     *            Ids for sessions to log out
     * @throws UnifyException
     */
    void forceLogOut(String... sessionIds) throws UnifyException;
}
