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

package com.tcdng.unify.core.input;

import com.tcdng.unify.core.data.Input;

/**
 * Boolean input.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class BooleanInput extends Input<Boolean> {

    public BooleanInput(String name, String description, String editor, boolean mandatory) {
        super(Boolean.class, name, description, editor, mandatory);
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

}
