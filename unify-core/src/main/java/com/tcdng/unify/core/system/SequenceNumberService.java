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

import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;

/**
 * Used to generate sequence numbers for records. Expected to work in a
 * clustered environment.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SequenceNumberService extends BusinessService {

    /**
     * Returns the next available sequence number for sequence with supplied name.
     * Creates a new sequence if one does not exist. All sequences start from 1L.
     * 
     * @param sequenceName
     *            the sequence name
     * @return the next sequence number
     * @throws UnifyException
     *             if an error occurs
     */
    Long getNextSequenceNumber(String sequenceName) throws UnifyException;

    /**
     * Returns the next number in sequence specified by supplied sequence name and
     * date. Creates a new sequence if no sequence exists for supplied parameters.
     * All sequences start from 1L.
     * 
     * @param sequenceName
     *            the sequence name
     * @param date
     *            the date
     * @throws UnifyException
     *             if an error occurs
     */
    Long getNextSequenceNumber(String sequenceName, Date date) throws UnifyException;

    /**
     * Returns a unique long ID that represents supplied string. A new ID is created
     * the first time the string is supplied. Subsequent calls with the same string
     * returns the same ID.
     * 
     * @param uniqueString
     *            the supplied string
     * @return the unique ID
     * @throws UnifyException
     *             if supplied string is null. If an error occurs.
     */
    Long getUniqueStringId(String uniqueString) throws UnifyException;

    /**
     * Resets sequence number service.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void reset() throws UnifyException;
}
