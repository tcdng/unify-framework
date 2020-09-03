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

package com.tcdng.unify.web.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Write work object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class WriteWork {

    private Map<String, Object> values;
    
    public WriteWork() {
        values = new HashMap<String, Object>();
    }
    
    public void set(String name, Object val) {
        values.put(name, val);
    }
    
    public Object get(String name) {
        return values.get(name);
    }
}
