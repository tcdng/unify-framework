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

package com.tcdng.unify.core;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Unify error.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyError {

    private String errorCode;

    private Object[] errorParams;

    public UnifyError(String errorCode) {
        this(errorCode, DataUtils.ZEROLEN_OBJECT_ARRAY);
    }

    public UnifyError(String errorCode, Object... errorParams) {
        this.errorCode = errorCode;
        this.errorParams = errorParams;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getErrorParams() {
        return errorParams;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Code = ").append(errorCode).append(", params = [");
        boolean appendSym = false;
        for (Object obj : errorParams) {
            if (appendSym) {
                sb.append(',');
            } else {
                appendSym = true;
            }

            sb.append(obj);
        }
        sb.append("]");
        return sb.toString();
    }
}
