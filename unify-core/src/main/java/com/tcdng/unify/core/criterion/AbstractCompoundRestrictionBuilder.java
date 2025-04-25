/*
 * Copyright 2018-2025 The Code Department.
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

/**
 * Convenient abstract base class for compound restriction builders.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractCompoundRestrictionBuilder {

    private CompoundRestriction restriction;

    public AbstractCompoundRestrictionBuilder amongst(String propertyName, Collection<Object> values) {
        getCompoundRestriction().add(new Amongst(propertyName, values));
        return this;
    }

    public AbstractCompoundRestrictionBuilder between(String propertyName, Object lowerValue, Object upperValue) {
        getCompoundRestriction().add(new Between(propertyName, lowerValue, upperValue));
        return this;
    }

    public AbstractCompoundRestrictionBuilder equals(String propertyName, Object value) {
        getCompoundRestriction().add(new Equals(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder greater(String propertyName, Object value) {
        getCompoundRestriction().add(new Greater(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder greaterEqual(String propertyName, Object value) {
        getCompoundRestriction().add(new GreaterOrEqual(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder isNotNull(String propertyName) {
        getCompoundRestriction().add(new IsNotNull(propertyName));
        return this;
    }

    public AbstractCompoundRestrictionBuilder isNull(String propertyName) {
        getCompoundRestriction().add(new IsNull(propertyName));
        return this;
    }

    public AbstractCompoundRestrictionBuilder less(String propertyName, Object value) {
        getCompoundRestriction().add(new Less(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder lessEqual(String propertyName, Object value) {
        getCompoundRestriction().add(new LessOrEqual(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder like(String propertyName, String value) {
        getCompoundRestriction().add(new Like(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder likeBegin(String propertyName, String value) {
        getCompoundRestriction().add(new BeginsWith(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder likeEnd(String propertyName, String value) {
        getCompoundRestriction().add(new EndsWith(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder notAmongst(String propertyName, Collection<Object> values) {
        getCompoundRestriction().add(new NotAmongst(propertyName, values));
        return this;
    }

    public AbstractCompoundRestrictionBuilder notBetween(String propertyName, Object lowerValue, Object upperValue) {
        getCompoundRestriction().add(new NotBetween(propertyName, lowerValue, upperValue));
        return this;
    }

    public AbstractCompoundRestrictionBuilder notEqual(String propertyName, Object value) {
        getCompoundRestriction().add(new NotEquals(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder notLike(String propertyName, String value) {
        getCompoundRestriction().add(new NotLike(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder notLikeBegin(String propertyName, String value) {
        getCompoundRestriction().add(new NotBeginWith(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder notLikeEnd(String propertyName, String value) {
        getCompoundRestriction().add(new NotEndWith(propertyName, value));
        return this;
    }

    public AbstractCompoundRestrictionBuilder addCompound(CompoundRestriction compoundRestriction) {
        getCompoundRestriction().add(compoundRestriction);
        return this;
    }
    
    public CompoundRestriction build() {
        CompoundRestriction result = getCompoundRestriction();
        restriction = null;
        return result;
    }
    
    protected abstract CompoundRestriction newInstance();
    
    private CompoundRestriction getCompoundRestriction() {
        if (restriction == null) {
            restriction = newInstance();
        }
        
        return restriction;
    }
}
