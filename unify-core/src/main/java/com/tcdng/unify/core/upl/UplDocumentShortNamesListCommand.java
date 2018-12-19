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
package com.tcdng.unify.core.upl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.list.AbstractListCommand;

/**
 * UPL document short name list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("upldocumentshortnameslist")
public class UplDocumentShortNamesListCommand
        extends AbstractListCommand<UplDocumentShortNamesListCommand.UplDocumentShortNamesParams> {

    @Configurable
    private UplCompiler uplCompiler;

    private FactoryMap<String, List<ListData>> listMap;

    public UplDocumentShortNamesListCommand() {
        super(UplDocumentShortNamesParams.class);
        this.listMap = new FactoryMap<String, List<ListData>>() {

            @Override
            protected List<ListData> create(String documentName, Object... params) throws Exception {
                List<ListData> list = new ArrayList<ListData>();
                UplDocumentAttributes uda = uplCompiler.compileComponentDocuments(getApplicationLocale(), documentName);
                for (String shortName : uda.getShortNames()) {
                    list.add(new ListData(shortName, shortName));
                }

                return list;
            }

        };
    }

    @Override
    public List<? extends Listable> execute(Locale locale, UplDocumentShortNamesParams params) throws UnifyException {
        if (params.getDocumentName() != null) {
            return this.listMap.get(params.getDocumentName());
        }

        return Collections.emptyList();
    }

    public static class UplDocumentShortNamesParams {

        private String documentName;

        public UplDocumentShortNamesParams(String documentName) {
            this.documentName = documentName;
        }

        public String getDocumentName() {
            return documentName;
        }

    }
}
