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
package com.tcdng.unify.core.criterion;

/**
 * Restriction type enumeration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public enum RestrictionType {
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    BETWEEN,
    NOT_BETWEEN,
    AMONGST,
    NOT_AMONGST,
    LIKE,
    NOT_LIKE,
    LIKE_BEGIN,
    NOT_LIKE_BEGIN,
    LIKE_END,
    NOT_LIKE_END,
    IS_NULL,
    IS_NOT_NULL,
    AND,
    OR;
}
