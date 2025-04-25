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

package com.tcdng.unify.web.ui.widget.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tcdng.unify.web.ui.widget.data.Hint.MODE;

/**
 * Hints.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Hints {

    private Map<String, Hint> hints;
    
    public Hints() {
        this.hints = new LinkedHashMap<String, Hint>();
    }
    
    public void add(MODE mode, String message) {
        add(new Hint(mode, message));
    }
    
    public void add(Hint hint) {
        String key = hint.key();
        if (!hints.containsKey(key)) {
            hints.put(key, hint);
        }
    }
    
    public Collection<Hint> getHints() {
        return hints.values();
    }
    
    public int size() {
        return hints.size();
    }
    
    public boolean isPresent() {
        return !hints.isEmpty();
    }
    
    public boolean isEmpty() {
        return hints.isEmpty();
    }
}
