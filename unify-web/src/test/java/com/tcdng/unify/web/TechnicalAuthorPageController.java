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

package com.tcdng.unify.web;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.annotation.Action;

/**
 * Technical author controller for testing action handler annotation
 * inheritance.
 * 
 * The first of the overridden handlers is not annotated with the {@link Action}
 * annotation. It should still be detected as an action handler since the super
 * method is already annotated.
 * 
 * The second overridden method has an explicit {@link Action} annotation, which is also
 * acceptable.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/testtechnicalauthor")
public class TechnicalAuthorPageController extends AuthorPageController {

    @Override
    public String viewAuthor() throws UnifyException {
        return super.viewAuthor();
    }

    @Action
    @Override
    public String newAuthor() throws UnifyException {
        return super.newAuthor();
    }

    @Action
    public String printTechnicalSpec() throws UnifyException {
        return "showtechnicalspec";
    }

    public String drawTechnicalSpec() throws UnifyException {
        return "showtechnicalspec";
    }
}
