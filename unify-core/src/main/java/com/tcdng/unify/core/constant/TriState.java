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

package com.tcdng.unify.core.constant;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Tri-state type constants.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
@StaticList(name = "tristatelist", description="$m{staticlist.tristatelist}")
public enum TriState implements EnumConst {

    TRUE("T"), FALSE("F"), CONFORMING("C");

    private final String code;

    private TriState(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return CONFORMING.code;
    }

    public static TriState fromCode(String code) {
        return EnumUtils.fromCode(TriState.class, code);
    }

    public static TriState fromName(String name) {
        return EnumUtils.fromName(TriState.class, name);
    }

    public static TriState getTriState(boolean bool) {
        if (bool) {
            return TRUE;
        }

        return FALSE;
    }

    public boolean isTrue() {
        return TRUE.equals(this);
    }

    public boolean isFalse() {
        return FALSE.equals(this);
    }

    public boolean isConforming() {
        return CONFORMING.equals(this);
    }
}
