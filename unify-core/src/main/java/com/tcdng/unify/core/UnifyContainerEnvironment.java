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
package com.tcdng.unify.core;

import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TypeRepository;

/**
 * Represents the environment in which a unify container runs.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyContainerEnvironment {

    private TypeRepository typeRepository;

    private String workingPath;

    public UnifyContainerEnvironment(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public UnifyContainerEnvironment(TypeRepository typeRepository, String workingPath) {
        this.typeRepository = typeRepository;
        this.workingPath = workingPath;
    }

    public TypeRepository getTypeRepository() {
        return typeRepository;
    }

    public String getWorkingPath() {
        return workingPath;
    }

    public String getEnvironmentFilename(String relativeFilename) throws UnifyException {
        if (!StringUtils.isBlank(workingPath)) {
            return IOUtils.buildFilename(workingPath, relativeFilename);
        }

        return relativeFilename;
    }
}
