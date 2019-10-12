/*
 * Copyright 2018-2019 The Code Department.
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
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Inputs data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Inputs {

    private List<Input<?>> inputList;

    private Map<String, Input<?>> inputByName;

    public Inputs(List<Input<?>> inputList) {
        this.inputList = Collections.unmodifiableList(inputList);
        this.inputByName = new HashMap<String, Input<?>>();
        for (Input<?> input : inputList) {
            inputByName.put(input.getName(), input);
        }
    }
    
    public String getInputValue(String name) throws UnifyException {
        Input<?> input = inputByName.get(name);
        if (input != null) {
            return input.getStringValue();
        }
        
        return null;
    }

    public boolean setInputValue(String name, String value) throws UnifyException {
        Input<?> input = inputByName.get(name);
        if (input != null) {
            input.setStringValue(value);
            return true;
        }
        
        return false;
    }

    public Set<String> getInputNames() {
        return inputByName.keySet();
    }
    
    public Input<?> getInput(String name) {
        return inputByName.get(name);
    }

    public List<Input<?>> getInputList() {
        return inputList;
    }

    public int size() {
        return inputList.size();
    }
    
    public Map<String, Object> getTypeValuesByName() {
        return Inputs.getTypeValuesByName(inputList);
    }  
    
    public static Map<String, Object> getTypeValuesByName(List<Input<?>> inputList) {
        Map<String, Object> map = new HashMap<String, Object>();
        Inputs.getTypeValuesByNameIntoMap(inputList, map);
        return map;
    }
    
    public static void getTypeValuesByNameIntoMap(List<Input<?>> inputList, Map<String, Object> map) {
        if(DataUtils.isNotBlank(inputList)) {
            for(Input<?> input: inputList) {
                map.put(input.getName(), input.getTypeValue());
            }
        }
    }
}
