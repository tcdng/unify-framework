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
package com.tcdng.unify.core.system.entities;

import java.util.Collection;

import com.tcdng.unify.core.database.Query;

/**
 * Convenient query class for sequenced entities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SequencedEntityQuery<T extends SequencedEntity> extends Query<T> {

    public SequencedEntityQuery(Class<T> entityClass) {
        super(entityClass);
    }

    public SequencedEntityQuery(Class<T> entityClass, boolean applyAppQueryLimit) {
        super(entityClass, applyAppQueryLimit);
    }

    public SequencedEntityQuery<T> id(Long id) {
        return (SequencedEntityQuery<T>) addEquals("id", id);
    }

    public SequencedEntityQuery<T> idNot(Long id) {
        return (SequencedEntityQuery<T>) addNotEqual("id", id);
    }

    public SequencedEntityQuery<T> idIn(Collection<Long> ids) {
        return (SequencedEntityQuery<T>) addAmongst("id", ids);
    }

    public SequencedEntityQuery<T> idNotIn(Collection<Long> ids) {
        return (SequencedEntityQuery<T>) addNotAmongst("id", ids);
    }

    public SequencedEntityQuery<T> reserved() {
        return (SequencedEntityQuery<T>) addLessThan("id", 0L);
    }

    public SequencedEntityQuery<T> notReserved() {
        return (SequencedEntityQuery<T>) addGreaterThan("id", 0L);
    }

    public SequencedEntityQuery<T> orderById() {
        return (SequencedEntityQuery<T>) addOrder("id");
    }

    public SequencedEntityQuery<T> selectId() {
        return (SequencedEntityQuery<T>) addSelect("id");
    }
}
