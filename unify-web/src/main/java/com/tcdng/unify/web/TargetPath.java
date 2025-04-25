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

package com.tcdng.unify.web;

import com.tcdng.unify.core.util.StringUtils;

/**
 * Target path.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TargetPath {

    private String path;

    private String target;

    public TargetPath(String path, String target) {
        this.path = path;
        this.target = target;
    }

    public TargetPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getTarget() {
        return target;
    }

    public boolean isWithTarget() {
        return !StringUtils.isBlank(target);
    }
}
