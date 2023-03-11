/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.constant.OrderType;

/**
 * Used to order the results of a criteria.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Order {

    public enum Policy {
        ADD_LEADING,
        ADD_TRAILING
    };

    private List<Part> parts;

    private final Policy policy;

    public Order() {
        this(Policy.ADD_TRAILING);
    }

    public Order(Policy policy) {
        this.policy = policy;
        parts = new ArrayList<Part>();
    }

    public Order add(String field) {
        return doAdd(field, OrderType.ASCENDING);
    }

    public Order add(String field, OrderType type) {
        return doAdd(field, type);
    }

    public List<Part> getParts() {
        return parts;
    }

    public void clear() {
        parts.clear();
    }

    public boolean isParts() {
        return !parts.isEmpty();
    }

    @Override
    public String toString() {
        return "Order [parts=" + parts + ", policy=" + policy + "]";
    }

    private Order doAdd(String field, OrderType type) {
        final int len = parts.size();
        for (int i = 0; i < len; i++) {
            if (parts.get(i).getField().equals(field)) {
                parts.remove(i);
                break;
            }
        }

        if (Policy.ADD_LEADING.equals(policy)) {
            parts.add(0, new Part(field, type));
        } else {
            parts.add(new Part(field, type));
        }

        return this;
    }

    public class Part {

        private String field;

        private OrderType type;

        private Part(String field, OrderType type) {
            this.field = field;
            this.type = type;
        }

        public String getField() {
            return field;
        }

        public OrderType getType() {
            return type;
        }

        public boolean isAscending() {
            return OrderType.ASCENDING.equals(type);
        }

        @Override
        public String toString() {
            return "Part [field=" + field + ", type=" + type + "]";
        }
    }
}
