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
package com.tcdng.unify.core.format;

/**
 * A pattern data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Pattern {

    private Base base;

    private String target;

    public Pattern(String pattern, boolean filler) {
        this(pattern, filler, false);
    }

    public Pattern(String pattern, boolean filler, boolean quoted) {
        this(new Base(pattern, filler, quoted));
    }

    public Pattern(Base base) {
        this.base = base;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (base.filler) {
            sb.append('f');
            if (base.quoted) {
                sb.append('q');
            }
        } else {
            sb.append('p');
        }
        sb.append('[').append(base.pattern).append(']');
        return sb.toString();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPattern() {
        return base.pattern;
    }

    public boolean isFiller() {
        return base.filler;
    }

    public boolean isQuoted() {
        return base.quoted;
    }
    
    public static class Base {

        private String pattern;

        private boolean filler;

        private boolean quoted;

        public Base(String pattern, boolean filler) {
            this(pattern, filler, false);
        }

        public Base(String pattern, boolean filler, boolean quoted) {
            this.pattern = pattern;
            this.filler = filler;
            this.quoted = quoted;
        }

        public String getPattern() {
            return pattern;
        }

        public boolean isFiller() {
            return filler;
        }

        public boolean isQuoted() {
            return quoted;
        }
    }
}
