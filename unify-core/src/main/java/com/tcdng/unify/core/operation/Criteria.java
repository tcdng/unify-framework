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

import java.util.Set;

/**
 * Criteria object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Criteria implements Cloneable {

    private Operator operator;

    private Object preOp;

    private Object postOp;

    public Criteria(Operator operator, Object preOp, Object postOp) {
        this.operator = operator;
        this.preOp = preOp;
        this.postOp = postOp;
    }

    /**
     * @return the operator
     */
    public final Operator getOperator() {
        return operator;
    }

    /**
     * @return the preOp
     */
    public final Object getPreOp() {
        return preOp;
    }

    /**
     * @return the postOp
     */
    public final Object getPostOp() {
        return postOp;
    }

    public final void getFields(Set<String> fields) {
        if (preOp instanceof String) {
            fields.add((String) preOp);
        } else {
            ((Criteria) preOp).getFields(fields);
        }

        if (postOp instanceof Criteria) {
            ((Criteria) postOp).getFields(fields);
        }
    }

    public Criteria copy() {
        try {
            return (Criteria) clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Criteria clone = (Criteria) super.clone();
        if (preOp != null && preOp instanceof Criteria) {
            clone.preOp = ((Criteria) preOp).clone();
        }
        if (postOp != null && postOp instanceof Criteria) {
            clone.postOp = ((Criteria) postOp).clone();
        }
        return clone;
    }
}
