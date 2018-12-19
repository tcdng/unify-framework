/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.operation;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.constant.OrderType;

/**
 * Used to order the results of a criteria.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Order {

    private List<Part> parts;

    public Order() {
        parts = new ArrayList<Part>();
    }

    public Order add(String field) {
        parts.add(new Part(field));
        return this;
    }

    public Order add(String field, OrderType type) {
        parts.add(new Part(field, type));
        return this;
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
    
    public class Part {

        private String field;

        private OrderType type;

        public Part(String field) {
            this.field = field;
            type = OrderType.ASCENDING;
        }

        public Part(String field, OrderType type) {
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
    }
}
