/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.upl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Represents a compiled UPL document.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UplDocument extends UplElement implements UplDocumentAttributes {

    private Map<String, UplElement> uplElementByLongNameMap;

    public UplDocument() {
        uplElementByLongNameMap = new HashMap<String, UplElement>();
    }

    @Override
    public boolean isElementWithLongName(String longName) {
        return uplElementByLongNameMap.containsKey(longName);
    }

    @Override
    public Set<String> getLongNames() {
        return uplElementByLongNameMap.keySet();
    }

    @Override
    public Set<String> getShortNames() {
        Set<String> shortNames = new HashSet<String>();
        for (UplElement uplElement : uplElementByLongNameMap.values()) {
            shortNames.add(uplElement.getShortName());
        }
        return shortNames;
    }

    @Override
    public UplElement getChildElementByLongName(String longName) throws UnifyException {
        UplElement uplElement = uplElementByLongNameMap.get(longName);
        if (uplElement == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PAGEUTIL_MISSING_ELEMENT, longName);
        }
        return uplElement;
    }

    public void generateLongNames(String documentName) throws UnifyException {
        setShortName(documentName);
        setLongName(documentName);
        generateLongNames(this);
    }

    private void generateLongNames(UplElement parentUplElement) throws UnifyException {
        for (String id : parentUplElement.getChildIds()) {
            String longName = StringUtils.dotify(parentUplElement.getLongName(), id);
            UplElement childUplElement = parentUplElement.getChildElement(id);
            childUplElement.setShortName(id);
            childUplElement.setLongName(longName);
            uplElementByLongNameMap.put(longName, childUplElement);
            generateLongNames(childUplElement);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(super.toString());
        sb.append("longNames: ").append("\n");
        for (String longName : uplElementByLongNameMap.keySet()) {
            sb.append(longName).append("\n");
        }
        return sb.toString();
    }
}
