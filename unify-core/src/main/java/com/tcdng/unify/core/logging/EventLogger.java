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
package com.tcdng.unify.core.logging;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.Entity;

/**
 * A component for logging events.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface EventLogger extends UnifyComponent {

    /**
     * Logs a user event using event code with optional details.
     * 
     * @param eventCode
     *            the event code
     * @param details
     *            the event details
     * @return true if event was successfully logged
     * @throws UnifyException
     *             if event code is unknown. if an error occurs.
     */
    boolean logUserEvent(String eventCode, String... details) throws UnifyException;

    /**
     * Logs a user event using event code with details.
     * 
     * @param eventCode
     *            the event code
     * @param details
     *            the event details
     * @return true if event was successfully logged
     * @throws UnifyException
     *             if event code is unknown. if an error occurs.
     */
    boolean logUserEvent(String eventCode, List<String> details) throws UnifyException;

    /**
     * Logs a user event using supplied event and record type.
     * 
     * @param eventType
     *            the event type
     * @param entityClass
     *            the record type
     * @return true if event was successfully logged
     * @throws UnifyException
     *             If an error occurs.
     */
    boolean logUserEvent(EventType eventType, Class<? extends Entity> entityClass) throws UnifyException;

    /**
     * Logs a user event with associated record.
     * 
     * @param eventType
     *            the event type
     * @param record
     *            the record object
     * @param isNewRecord
     *            indicates supplied record is new
     * @return true if event was successfully logged
     * @throws UnifyException
     *             if an error occurs.
     */
    boolean logUserEvent(EventType eventType, Entity record, boolean isNewRecord) throws UnifyException;

    /**
     * Logs a user event with associated old and new record.
     * 
     * @param eventType
     *            the event type
     * @param oldRecord
     *            the old record
     * @param newRecord
     *            the new record. Can be null
     * @return true if event was successfully logged
     * @throws UnifyException
     *             if audit type with supplied action is unknown. If an error
     *             occurs.
     */
    <T extends Entity> boolean logUserEvent(EventType eventType, T oldRecord, T newRecord) throws UnifyException;
}
