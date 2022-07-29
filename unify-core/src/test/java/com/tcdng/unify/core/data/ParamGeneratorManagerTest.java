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

package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Parameter generator manager tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ParamGeneratorManagerTest extends AbstractUnifyComponentTest {

    @Test
    public void testGenerationNoGen() throws Exception {
        ParamGeneratorManager manager = getComponent(ParamGeneratorManager.class,
                ApplicationComponents.APPLICATION_PARAMGENERATORMANAGER);
        ValueStore paramValueStore = new BeanValueStore(new Candidate("John", 25));
        ValueStore generatorValueStore = new BeanValueStore(new Address("38 Warehouse Road", "Apapa Lagos"));
        List<StringToken> tokenList = StringUtils
                .breakdownParameterizedString("Hello {{name}}. You are {{age}} years old.");
        ParameterizedStringGenerator generator = manager.getParameterizedStringGenerator(paramValueStore,
                generatorValueStore, tokenList);
        assertNotNull(generator);

        String txt = generator.generate();
        assertEquals("Hello John. You are 25 years old.", txt);
    }

    @Test
    public void testGenerationNoGenList() throws Exception {
        ParamGeneratorManager manager = getComponent(ParamGeneratorManager.class,
                ApplicationComponents.APPLICATION_PARAMGENERATORMANAGER);
        ValueStore paramValueStore = new BeanValueListStore(
                Arrays.asList(new Candidate("John", 25), new Candidate("Bashir", 45)));
        ValueStore generatorValueStore = new BeanValueStore(new Address("38 Warehouse Road", "Apapa Lagos"));
        List<StringToken> tokenList = StringUtils
                .breakdownParameterizedString("Hello {{name}}. You are {{age}} years old.");
        ParameterizedStringGenerator generator = manager.getParameterizedStringGenerator(paramValueStore,
                generatorValueStore, tokenList);
        assertNotNull(generator);

        String txt = generator.generate();
        assertEquals("Hello John. You are 25 years old.", txt);

        generator.setDataIndex(1);
        txt = generator.generate();
        assertEquals("Hello Bashir. You are 45 years old.", txt);
    }

    @Test
    public void testGenerationGenOnly() throws Exception {
        ParamGeneratorManager manager = getComponent(ParamGeneratorManager.class,
                ApplicationComponents.APPLICATION_PARAMGENERATORMANAGER);
        ValueStore paramValueStore = new BeanValueStore(new Candidate("John", 25));
        ValueStore generatorValueStore = new BeanValueStore(new Address("38 Warehouse Road", "Apapa Lagos"));
        List<StringToken> tokenList = StringUtils
                .breakdownParameterizedString("My address: {{g:test-gen-b}} {{g:test-gen-a}}.");
        ParameterizedStringGenerator generator = manager.getParameterizedStringGenerator(paramValueStore,
                generatorValueStore, tokenList);
        assertNotNull(generator);

        String txt = generator.generate();
        assertEquals("My address: Apapa Lagos - John 38 Warehouse Road - 25.", txt);
    }

    @Test
    public void testGenerationMixed() throws Exception {
        ParamGeneratorManager manager = getComponent(ParamGeneratorManager.class,
                ApplicationComponents.APPLICATION_PARAMGENERATORMANAGER);
        ValueStore paramValueStore = new BeanValueStore(new Candidate("Sam", 25));
        ValueStore generatorValueStore = new BeanValueStore(new Address("38 Warehouse Road", "Apapa Lagos"));
        List<StringToken> tokenList = StringUtils
                .breakdownParameterizedString("{{g:test-gen-b}} Hello {{name}} {{g:test-gen-a}}.");
        ParameterizedStringGenerator generator = manager.getParameterizedStringGenerator(paramValueStore,
                generatorValueStore, tokenList);
        assertNotNull(generator);

        String txt = generator.generate();
        assertEquals("Apapa Lagos - Sam Hello Sam 38 Warehouse Road - 25.", txt);
    }

    @Test
    public void testGenerationMixedList() throws Exception {
        ParamGeneratorManager manager = getComponent(ParamGeneratorManager.class,
                ApplicationComponents.APPLICATION_PARAMGENERATORMANAGER);
        ValueStore paramValueStore = new BeanValueListStore(
                Arrays.asList(new Candidate("Samuel", 25), new Candidate("James", 45)));
        ValueStore generatorValueStore = new BeanValueStore(new Address("38 Warehouse Road", "Apapa Lagos"));
        List<StringToken> tokenList = StringUtils
                .breakdownParameterizedString("{{g:test-gen-b}} Hello {{name}} {{g:test-gen-a}}.");
        ParameterizedStringGenerator generator = manager.getParameterizedStringGenerator(paramValueStore,
                generatorValueStore, tokenList);
        assertNotNull(generator);

        String txt = generator.generate();
        assertEquals("Apapa Lagos - Samuel Hello Samuel 38 Warehouse Road - 25.", txt);

        generator.setDataIndex(1);
        txt = generator.generate();
        assertEquals("Apapa Lagos - James Hello James 38 Warehouse Road - 45.", txt);
    }

    @Test(expected = UnifyException.class)
    public void testGenerationUnknownGenerator() throws Exception {
        ParamGeneratorManager manager = getComponent(ParamGeneratorManager.class,
                ApplicationComponents.APPLICATION_PARAMGENERATORMANAGER);
        ValueStore paramValueStore = new BeanValueStore(new Candidate("Sam", 25));
        ValueStore generatorValueStore = new BeanValueStore(new Address("38 Warehouse Road", "Apapa Lagos"));
        List<StringToken> tokenList = StringUtils
                .breakdownParameterizedString("{{g:test-gen-c}} Hello {{name}} {{g:test-gen-a}}.");
        manager.getParameterizedStringGenerator(paramValueStore,
                generatorValueStore, tokenList);
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    public class Candidate {

        private String name;

        private int age;

        public Candidate(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Candidate() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

    }

    public class Address {

        private String address1;

        private String address2;

        public Address(String address1, String address2) {
            this.address1 = address1;
            this.address2 = address2;
        }

        public Address() {

        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

    }
}
