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
package com.tcdng.unify.core;

/**
 * Common unify exception class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyException extends Exception {

    private static final long serialVersionUID = 6283713822275326177L;

    private UnifyError unifyError;

    public UnifyException(Throwable cause, String errorCode, Object... errorParams) {
        super(UnifyException.buildMessage(errorCode, errorParams), cause);
        this.unifyError = new UnifyError(errorCode, errorParams);
    }

    public UnifyException(String errorCode, Object... errorParams) {
        super(UnifyException.buildMessage(errorCode, errorParams));
        this.unifyError = new UnifyError(errorCode, errorParams);
    }

    public UnifyException(UnifyError unifyError) {
        super(UnifyException.buildMessage(unifyError.getErrorCode(), unifyError.getErrorParams()));
        this.unifyError = unifyError;
    }

    public UnifyError getUnifyError() {
        return unifyError;
    }

    public String getErrorCode() {
        return this.unifyError.getErrorCode();
    }

    public Object[] getErrorParams() {
        return this.unifyError.getErrorParams();
    }

    private static String buildMessage(String errorCode, Object... errorParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error Code: ").append(errorCode);
        sb.append(", Parameter(s): [");
        if (errorParams != null && errorParams.length > 0) {
            boolean appendSym = false;
            for (Object errorParam : errorParams) {
                if (appendSym)
                    sb.append(',');
                else
                    appendSym = true;
                sb.append(errorParam);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
