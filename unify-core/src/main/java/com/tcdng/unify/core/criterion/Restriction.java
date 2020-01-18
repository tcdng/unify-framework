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

package com.tcdng.unify.core.criterion;

import java.util.Set;

/**
 * Restriction object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Restriction {

    /**
     * Gets the restriction type.
     * 
     * @return the restriction type.
     */
    RestrictionType getType();

    /**
     * Writes restricted fields into supplied bucket.
     * 
     * @param restrictedFields
     *            the bucket to write to
     */
    void writeRestrictedFields(Set<String> restrictedFields);

    /**
     * Checks if field is part of this restriction.
     * 
     * @param fieldname
     *            the field name
     * @return true is property is part of this restriction otherwise false
     */
    boolean isRestrictedField(String fieldname);

    /**
     * Checks if restriction is empty.
     * 
     * @return a true if empty otherwise false
     */
    boolean isEmpty();

    /**
     * Checks if restriction is simple restriction.
     * 
     * @return a true if simple otherwise false
     */
    boolean isSimple();
}
