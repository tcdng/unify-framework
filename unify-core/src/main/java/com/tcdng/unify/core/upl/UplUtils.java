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
package com.tcdng.unify.core.upl;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;

/**
 * UPL utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class UplUtils {

    private static final String GENERATOR_INFIX = ">g>";

    private static final String CLONE_INFIX = ">c>";

    private UplUtils() {

    }

    public static UplGeneratorTarget getUplGeneratorTarget(String name) throws UnifyException {
        if (!UplUtils.isUplGeneratorTargetName(name)) {
            throw new UnifyOperationException("UplUtils",
                    "Invalid generator target name [" + name + "]");
        }

        String[] names = name.split(GENERATOR_INFIX);
        String target = names[1];
        if (UplUtils.isUplCloneName(target)) {
            target = UplUtils.getUplComponentClone(target).getComponentName();
        }
        return new UplGeneratorTarget(names[0], target);
    }

    public static String generateUplGeneratorTargetName(String generatorName, String target) {
        return generatorName + GENERATOR_INFIX + target;
    }

    public static boolean isUplGeneratorTargetName(String name) {
        return name.indexOf(GENERATOR_INFIX) > 0;
    }

    public static UplComponentClone getUplComponentClone(String name) throws UnifyException {
        if (!UplUtils.isUplCloneName(name)) {
            throw new UnifyOperationException("UplUtils",
                    "Invalid UPL component clone name [" + name + "]");
        }

        String[] names = name.split(CLONE_INFIX);
        return new UplComponentClone(names[0], names[1]);
    }

    public static String generateUplComponentCloneName(String componentName, String cloneId) {
        return componentName + CLONE_INFIX + cloneId;
    }

    public static boolean isUplCloneName(String name) {
        return name.indexOf(CLONE_INFIX) > 0;
    }

    public static String generateUplAttributesKey(int uplType, String componentName, String longName,
            String descriptor) {
        StringBuilder sb = new StringBuilder();
        sb.append(uplType);
        UplUtils.appendKeyField(sb, 0, componentName);
        UplUtils.appendKeyField(sb, 1, longName);
        UplUtils.appendKeyField(sb, 2, descriptor);
        return sb.toString();
    }

    public static UplAttributesKeyFields extractUplAtributesKeyFields(String uplAttributesKey) {
        int uplType = Integer.valueOf(uplAttributesKey.substring(0, uplAttributesKey.indexOf("[0]>")));
        String document = UplUtils.extractField(uplAttributesKey, "[0]>", "[1]>");
        String longName = UplUtils.extractField(uplAttributesKey, "[1]>", "[2]>");
        String descriptor = UplUtils.extractField(uplAttributesKey, "[2]>", null);
        return new UplAttributesKeyFields(uplType, document, longName, descriptor);
    }

    private static void appendKeyField(StringBuilder sb, int index, String field) {
        sb.append('[').append(index).append("]>");
        if (field != null && !field.isEmpty()) {
            sb.append(field);
        }
    }

    private static String extractField(String key, String startMark, String stopMark) {
        String field = null;
        if (stopMark != null) {
            field = key.substring(key.indexOf(startMark) + startMark.length(), key.indexOf(stopMark));
        } else {
            field = key.substring(key.indexOf(startMark) + startMark.length());
        }

        if (field != null && field.isEmpty()) {
            return null;
        }

        return field;
    }

    public static class UplGeneratorTarget {

        private String generatorName;

        private String target;

        public UplGeneratorTarget(String generatorName, String target) {
            this.generatorName = generatorName;
            this.target = target;
        }

        public String getGeneratorName() {
            return generatorName;
        }

        public String getTarget() {
            return target;
        }

    }

    public static class UplComponentClone {

        private String componentName;

        private String cloneId;

        public UplComponentClone(String componentName, String cloneId) {
            this.componentName = componentName;
            this.cloneId = cloneId;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getCloneId() {
            return cloneId;
        }

    }
}
