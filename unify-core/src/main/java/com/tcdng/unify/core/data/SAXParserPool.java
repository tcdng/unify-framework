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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * SAX parser pool.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SAXParserPool extends AbstractPool<SAXParser> {

    private SAXParserFactory saxParserFctry;

    public SAXParserPool() {
        this(8 * 1000, 4, 32);
    }

    public SAXParserPool(long getTimeout, int minSize, int maxSize) {
        super(getTimeout, minSize, maxSize, true);
        this.saxParserFctry = SAXParserFactory.newInstance();
    }

    @Override
    protected SAXParser createObject(Object... params) throws Exception {
        return this.saxParserFctry.newSAXParser();
    }

    @Override
    protected void onGetObject(SAXParser saxParser, Object... params) throws Exception {
        saxParser.reset();
    }

    @Override
    protected void destroyObject(SAXParser saxParser) {

    }

}
