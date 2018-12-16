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
package com.tcdng.unify.core.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;

/**
 * Inputs data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Inputs {

    private List<Input> inputList;

    private Map<String, Input> inputByName;

    public Inputs(List<Input> inputList) {
        this.inputList = Collections.unmodifiableList(inputList);
        this.inputByName = new HashMap<String, Input>();
        for (Input input : inputList) {
            inputByName.put(input.getName(), input);
        }
    }

    public Input getInput(String name) {
        return this.inputByName.get(name);
    }

    public String getInputValue(String name) {
        Input input = this.inputByName.get(name);
        if (input != null) {
            return input.getValue();
        }
        return null;
    }

    public boolean setInputValue(String name, String value) {
        Input input = this.inputByName.get(name);
        if (input != null) {
            input.setValue(value);
            return true;
        }
        return false;
    }

    public List<Input> getInputList() {
        return inputList;
    }

    public Map<String, Object> getInputValues() throws UnifyException {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Input input : this.inputList) {
            map.put(input.getName(), input.getTypeValue());
        }

        return map;
    }

    public int size() {
        return inputList.size();
    }
}
