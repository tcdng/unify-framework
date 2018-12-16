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

import java.util.Locale;

/**
 * An abstract generic locale map of generic factory maps.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class LocaleFactoryMaps<T, U> extends FactoryMaps<Locale, T, U> {

    public LocaleFactoryMaps() {
        this(false);
    }

    public LocaleFactoryMaps(boolean checkStale) {
        super(checkStale);
    }
}