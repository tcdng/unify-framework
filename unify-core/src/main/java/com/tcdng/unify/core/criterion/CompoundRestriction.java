/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Collection;
import java.util.List;

/**
 * Compound restriction object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface CompoundRestriction extends Restriction {

    /**
     * Adds restriction to this compound restriction.
     * 
     * @param restriction
     *            the restriction to add
     * @return expected to return this restriction
     */
    CompoundRestriction add(Restriction restriction);

    /**
     * Gets restriction list.
     * 
     * @return the restriction list
     */
    List<Restriction> getRestrictionList();

    /**
     * Indicates if this compound restriction is empty.
     * 
     * @return a true value if contains restrictions otherwise false
     */
    boolean isEmpty();

    /**
     * Gets the number of restrictions in this compound restriction.
     * 
     * @return the number of restrictions
     */
    int size();

    /**
     * Clears all restrictions contained by this compound restriction.
     */
    void clear();

    /**
     * Replace value for all single value restrictions on property.
     * 
     * @param propertyName
     *            the property name
     * @param val
     *            the value to set
     * @return a true value if any replacement was made
     */
    boolean replaceAll(String propertyName, Object val);

    /**
     * Replace value for all double value restrictions on property.
     * 
     * @param propertyName
     *            the property name
     * @param val1
     *            the first value to set
     * @param val2
     *            the second value to set
     * @return a true value if any replacement was made
     */
    boolean replaceAll(String propertyName, Object val1, Object val2);

    /**
     * Replace value for all multiple value restrictions on property.
     * 
     * @param propertyName
     *            the property name
     * @param val
     *            the multiple values to set
     * @return a true value if any replacement was made
     */
    boolean replaceAll(String propertyName, Collection<Object> val);
}
