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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyComponentSettings;
import com.tcdng.unify.core.UnifyContainerConfig;
import com.tcdng.unify.core.database.TestSqlDataSource;

/**
 * UnifyConfigUtils tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UnifyConfigUtilsTest {

    @Test
    public void testResolveConfigFileToEnvironment() throws Exception {
    	String resolvedFileName1 = UnifyConfigUtils.resolveConfigFileToEnvironment("conf/unify.xml", "banking-prod");
    	assertNotNull(resolvedFileName1);
    	assertEquals("conf/unify-banking-prod.xml", resolvedFileName1);
    }
    
    @Test
    public void testScanTypeRepository() throws Exception {
        UnifyContainerConfig.Builder uccb = UnifyContainerConfig.newBuilder();
        TypeRepository typeRepository = TypeUtils.getTypeRepositoryFromClasspath();
        UnifyConfigUtils.readConfigFromTypeRepository(uccb, typeRepository, "com.tcdng.unify.core");

        UnifyContainerConfig ucc = uccb.build();
        UnifyComponentConfig unifyComponentConfig = ucc.getComponentConfig("test-sqldatasource");
        assertNotNull(unifyComponentConfig);

        // Check configurable properties
        UnifyComponentSettings settings = unifyComponentConfig.getSettings();
        assertTrue(settings.isProperty("dialect"));
        assertTrue(settings.isProperty("driver"));
        assertTrue(settings.isProperty("connectionUrl"));
        assertTrue(settings.isProperty("passwordAuthentication"));
        assertTrue(settings.isProperty("username"));
        assertTrue(settings.isProperty("password"));
        assertTrue(settings.isProperty("getConnectionTimeout"));
        assertTrue(settings.isProperty("maxConnections"));
        assertTrue(settings.isProperty("minConnections"));

        // Check default configuration values
        assertEquals("hsqldb-dialect", settings.getSettingValue("dialect"));
        assertEquals("org.hsqldb.jdbcDriver", settings.getSettingValue("driver"));
        assertEquals("jdbc:hsqldb:mem:test", settings.getSettingValue("connectionUrl"));
        assertEquals("1000", settings.getSettingValue("getConnectionTimeout"));
        assertEquals("64", settings.getSettingValue("maxConnections"));
        assertEquals("1", settings.getSettingValue("minConnections"));
    }

    @Test
    public void testReadXMLConfiguration() throws Exception {
        String configXml =
        		"<unify version=\"1\" nodeId=\"node-001\" production=\"false\" cluster=\"false\">"
                + "<properties>"
        		+ "		<property name=\"application.name\" value = \"TestApplication\"/>"
                + "</properties>"
        		+ "<components>"
                + " <component name=\"test-sqldatasource\" class=\"com.tcdng.unify.core.database.TestSqlDataSource\">"
                + "     <properties>"
                + "         <property name=\"dialect\" value=\"hsqldb-dialect\"/>"
                + "         <property name=\"maxConnections\" value=\"20\"/>"
                + "         <property name=\"minConnections\" value=\"4\"/>"
                + "     </properties>"
                + " </component>"
                + "</components>"
                + "</unify>";
        UnifyContainerConfig.Builder uccb = UnifyContainerConfig.newBuilder();
        UnifyConfigUtils.readConfigFromXml(uccb, configXml, null);

        UnifyContainerConfig ucc = uccb.build();
        assertEquals("TestApplication", ucc.getProperty("application.name"));
        UnifyComponentConfig unifyComponentConfig = ucc.getComponentConfig("test-sqldatasource");
        assertNotNull(unifyComponentConfig);
        assertEquals(TestSqlDataSource.class, unifyComponentConfig.getType());

        // Check configurable properties
        UnifyComponentSettings settings = unifyComponentConfig.getSettings();
        assertTrue(settings.isProperty("dialect"));
        assertTrue(settings.isProperty("driver"));
        assertTrue(settings.isProperty("connectionUrl"));
        assertTrue(settings.isProperty("passwordAuthentication"));
        assertTrue(settings.isProperty("username"));
        assertTrue(settings.isProperty("password"));
        assertTrue(settings.isProperty("getConnectionTimeout"));
        assertTrue(settings.isProperty("maxConnections"));
        assertTrue(settings.isProperty("minConnections"));

        // Check default configuration values
        assertEquals("hsqldb-dialect", settings.getSettingValue("dialect"));
        assertEquals("org.hsqldb.jdbcDriver", settings.getSettingValue("driver"));
        assertEquals("jdbc:hsqldb:mem:test", settings.getSettingValue("connectionUrl"));
        assertEquals("1000", settings.getSettingValue("getConnectionTimeout"));
        assertEquals("20", settings.getSettingValue("maxConnections"));
        assertEquals("4", settings.getSettingValue("minConnections"));
    }

    @Test
    public void testReadXMLConfigurationWithMultiValues() throws Exception {
        String configXml = "<unify version=\"1\" nodeId=\"node-001\" production=\"false\" cluster=\"false\">"
                + "<properties>" + "		<property name=\"application.name\" value = \"TestApplication 2\"/>"
                + "</properties>" + "<components>"
                + " <component name=\"test-dummy\" class=\"com.tcdng.unify.core.TestDummyComponent\">" + "     <properties>"
                + "         <property name=\"valueList\">"
                + "             <valueItem>com.tcdng.unify.core.resources.webtestmessages</valueItem>"
                + "             <valueItem>com.tcdng.unify.core.resources.messages</valueItem>"
                + "             <valueItem>com.tcdng.unify.core.resources.messages</valueItem>" + "         </property>"
                + "     </properties>" + " </component>" + "</components>" + "</unify>";
        UnifyContainerConfig.Builder uccb = UnifyContainerConfig.newBuilder();
        UnifyConfigUtils.readConfigFromXml(uccb, configXml, null);

        UnifyContainerConfig ucc = uccb.build();
        assertEquals("TestApplication 2", ucc.getProperty("application.name"));
        UnifyComponentConfig unifyComponentConfig = ucc.getComponentConfig("test-dummy");
        assertNotNull(unifyComponentConfig);
        String[] values = (String[]) unifyComponentConfig.getSettings().getSettingValue("valueList");
        assertNotNull(values);
        assertEquals(3, values.length);
        assertEquals("com.tcdng.unify.core.resources.webtestmessages", values[0]);
        assertEquals("com.tcdng.unify.core.resources.messages", values[1]);
        assertEquals("com.tcdng.unify.core.resources.messages", values[2]);
    }

    @Test
    public void testResolveConfigurationOverrides() throws Exception {
        List<String> overrideSuffixList = Arrays.asList("red", "blue");
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("a", Integer.valueOf(32));
        map.put("b", Integer.valueOf(16));
        map.put("c", Integer.valueOf(16));
        map.put("d", "Green");
        map.put("e", Long.valueOf(20L));
        map.put("b_red", Integer.valueOf(8));
        map.put("c_blue", "Blue");
        map.put("d_red", "Red");
        map.put("e_purple", Long.valueOf(80L));

        Map<String, String> resolutionMap = UnifyConfigUtils.resolveConfigurationOverrides(map, overrideSuffixList);
        assertNotNull(resolutionMap);
        assertEquals(3, resolutionMap.size());
        assertEquals("b", resolutionMap.get("b_red"));
        assertEquals("c", resolutionMap.get("c_blue"));
        assertEquals("d", resolutionMap.get("d_red"));

        assertEquals(6, map.size());
        assertFalse(map.containsKey("b_red"));
        assertFalse(map.containsKey("c_blue"));
        assertFalse(map.containsKey("d_red"));
        assertEquals(Integer.valueOf(32), map.get("a"));
        assertEquals(Integer.valueOf(8), map.get("b"));
        assertEquals("Blue", map.get("c"));
        assertEquals("Red", map.get("d"));
        assertEquals(Long.valueOf(20L), map.get("e"));
        assertEquals(Long.valueOf(80L), map.get("e_purple"));
    }
}
