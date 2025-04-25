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
package com.tcdng.unify.core.upl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.format.DateFormatter;
import com.tcdng.unify.core.upl.artifacts.TestElementA;

/**
 * Unify UPL compiler tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UplCompilerTest extends AbstractUnifyComponentTest {

    private UplCompiler uplCompiler;

    @Test
    public void testSameAttributesForSameDescriptorAndLocale() throws Exception {
        UplElementAttributes a = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa");
        UplElementAttributes b = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa");
        UplElementAttributes c = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa");
        assertSame(a, b);
        assertSame(b, c);
        assertSame(c, a);
    }

    @Test
    public void testDifferentAttributesForSameDescriptorDiferrentLocale() throws Exception {
        UplElementAttributes a = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa");
        UplElementAttributes b = uplCompiler.compileDescriptor(Locale.GERMANY, "!test-uplelementa");
        UplElementAttributes c = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa");
        UplElementAttributes d = uplCompiler.compileDescriptor(Locale.GERMANY, "!test-uplelementa");
        assertNotSame(a, b);
        assertNotSame(c, d);
        assertSame(a, c);
        assertSame(b, d);
    }

    @Test
    public void testDifferentAttributesForDiferrentDescriptorSameLocale() throws Exception {
        UplElementAttributes a = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa name:$s{Funmi}");
        UplElementAttributes b = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa name:$s{Tayo}");
        UplElementAttributes c = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementa name:$s{Funmi} age:25");
        assertNotSame(a, b);
        assertNotSame(b, c);
        assertNotSame(c, a);
    }

    @Test
    public void testCompileDescriptorWithDefaultAttributes() throws Exception {
        UplElementAttributes uplElementAttributes =
                uplCompiler.compileDescriptor(Locale.getDefault(), "!test-uplelementa");
        assertNull(uplElementAttributes.getAttributeValue(String.class, "name"));
        assertEquals("Application User", uplElementAttributes.getAttributeValue(String.class, "description"));
        assertNull(uplElementAttributes.getAttributeValue(String[].class, "friendList"));
        assertEquals(Integer.valueOf(20), uplElementAttributes.getAttributeValue(int.class, "age"));
    }

    @Test
    public void testCompileDescriptorWithSetAttributes() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementa name:$s{Smith Jones} description:$s{Astrologist}"
                        + " age:12 friendList:$s{Tom} rateList:25.60");
        assertEquals("Smith Jones", uplElementAttributes.getAttributeValue(String.class, "name"));
        assertEquals("Astrologist", uplElementAttributes.getAttributeValue(String.class, "description"));
        String[] friendList = uplElementAttributes.getAttributeValue(String[].class, "friendList");
        assertFalse(friendList == null);
        assertEquals(1, friendList.length);
        assertEquals("Tom", friendList[0]);
        Double[] rateList = uplElementAttributes.getAttributeValue(Double[].class, "rateList");
        assertFalse(rateList == null);
        assertEquals(1, rateList.length);
        assertEquals(Double.valueOf(25.60), rateList[0]);
        assertEquals(Integer.valueOf(12), uplElementAttributes.getAttributeValue(int.class, "age"));
    }

    @Test
    public void testCompileDescriptorWithComplexArrays() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementa name:$s{Krane} friendList:$s{Tom} friendList:$s{Jack}"
                        + " friendList:$l{Mink Pink Link}");
        assertEquals("Krane", uplElementAttributes.getAttributeValue(String.class, "name"));
        String[] friendList = uplElementAttributes.getAttributeValue(String[].class, "friendList");
        assertFalse(friendList == null);
        assertEquals(5, friendList.length);
        assertEquals("Tom", friendList[0]);
        assertEquals("Jack", friendList[1]);
        assertEquals("Mink", friendList[2]);
        assertEquals("Pink", friendList[3]);
        assertEquals("Link", friendList[4]);
    }

    @Test
    public void testCompileDescriptorWithComplexConvertedArrays() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementa name:$s{Peter Parker} rateList:35.83" + " rateList:20 rateList:$l{10.2 47.50}");
        assertEquals("Peter Parker", uplElementAttributes.getAttributeValue(String.class, "name"));
        Double[] rateList = uplElementAttributes.getAttributeValue(Double[].class, "rateList");
        assertFalse(rateList == null);
        assertEquals(4, rateList.length);
        assertEquals(Double.valueOf(35.83), rateList[0]);
        assertEquals(Double.valueOf(20.00), rateList[1]);
        assertEquals(Double.valueOf(10.20), rateList[2]);
        assertEquals(Double.valueOf(47.50), rateList[3]);
    }

    @Test
    public void testCompileDescriptorWithMessages() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementa name:$m{uplcompiler.test.tiger} friendList:$m{uplcompiler.test.joe}"
                        + " friendList:$m{uplcompiler.test.sanders} friendList:$l{Mink Pink Link}");
        assertEquals("Tiger", uplElementAttributes.getAttributeValue(String.class, "name"));
        String[] friendList = uplElementAttributes.getAttributeValue(String[].class, "friendList");
        assertFalse(friendList == null);
        assertEquals(5, friendList.length);
        assertEquals("Joe", friendList[0]);
        assertEquals("Sanders", friendList[1]);
        assertEquals("Mink", friendList[2]);
        assertEquals("Pink", friendList[3]);
        assertEquals("Link", friendList[4]);
    }

    @Test
    public void testCompileDescriptorWithUnifyProperties() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementa name:$u{uplcompiler.test.tiger} friendList:$m{uplcompiler.test.joe}"
                        + " friendList:$u{uplcompiler.test.sanders} friendList:$l{Mink Pink Link}");
        assertEquals("Unify Tiger", uplElementAttributes.getAttributeValue(String.class, "name"));
        String[] friendList = uplElementAttributes.getAttributeValue(String[].class, "friendList");
        assertFalse(friendList == null);
        assertEquals(5, friendList.length);
        assertEquals("Joe", friendList[0]);
        assertEquals("Unify Sanders", friendList[1]);
        assertEquals("Mink", friendList[2]);
        assertEquals("Pink", friendList[3]);
        assertEquals("Link", friendList[4]);
    }

    @Test
    public void testCompileDescriptorWithInlineDeclaration() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementb title:$s{Complex Test}" + " user:$d{!test-uplelementa name:$s{John Doe} "
                        + " friendList:$m{uplcompiler.test.joe} friendList:$m{uplcompiler.test.sanders}}"
                        + " count:1234");
        assertEquals("Complex Test", uplElementAttributes.getAttributeValue(String.class, "title"));
        assertEquals(Long.valueOf(1234), uplElementAttributes.getAttributeValue(long.class, "count"));
        TestElementA smuc = uplElementAttributes.getAttributeValue(TestElementA.class, "user");
        assertNotNull(smuc);
        assertEquals("John Doe", smuc.getUplAttribute(String.class, "name"));
        String[] friendList = smuc.getUplAttribute(String[].class, "friendList");
        assertFalse(friendList == null);
        assertEquals(2, friendList.length);
        assertEquals("Joe", friendList[0]);
        assertEquals("Sanders", friendList[1]);
    }

    @Test
    public void testCompileDescriptorWithInlineArray() throws Exception {
        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.getDefault(),
                "!test-uplelementb title:$s{Inline Arrays}" + " manyUser:$d{!test-uplelementa name:$s{John Doe}} "
                        + " manyUser:$d{!test-uplelementa name:$s{JI Jane}} " + " count:54321");
        assertEquals("Inline Arrays", uplElementAttributes.getAttributeValue(String.class, "title"));
        assertEquals(Long.valueOf(54321), uplElementAttributes.getAttributeValue(long.class, "count"));
        TestElementA[] smuc = uplElementAttributes.getAttributeValue(TestElementA[].class, "manyUser");
        assertNotNull(smuc);
        assertEquals(Integer.valueOf(2), Integer.valueOf(smuc.length));
        assertEquals("John Doe", smuc[0].getUplAttribute(String.class, "name"));
        assertEquals("JI Jane", smuc[1].getUplAttribute(String.class, "name"));
    }

    @Test
    public void testCompileDescriptorWithComponentAttribute() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 19);
        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.YEAR, 2014);
        Date testDate = cal.getTime();

        UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementc");
        DateFormatter dateFormatter =
                (DateFormatter) uplElementAttributes.getAttributeValue(DateFormatter.class, "formatter");
        assertNotNull(dateFormatter);
        assertEquals("19 October 2014", dateFormatter.format(testDate));

        uplElementAttributes =
                uplCompiler.compileDescriptor(Locale.UK, "!test-uplelementc formatter:$d{!dateformat style:$s{short}}");
        dateFormatter = (DateFormatter) uplElementAttributes.getAttributeValue(DateFormatter.class, "formatter");
        assertNotNull(dateFormatter);
        assertNotNull(dateFormatter.format(testDate));
    }

    @Test
    public void testCompileDocument() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumenta");

        // Assert document attributes
        UplElementReferences uer = uplDocumentAttributes.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(2, idList.size());
        assertEquals("firstId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        List<String> longNameList = uer.getLongNames();
        assertEquals(2, longNameList.size());
        assertEquals("test-upldocumenta.firstId", longNameList.get(0));
        assertEquals("test-upldocumenta.secondId", longNameList.get(1));

        assertEquals("Man In Sky", uplDocumentAttributes.getAttributeValue(String.class, "fourthName"));

        // Assert that all long names are properly generated
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumenta.firstId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumenta.secondId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumenta.thirdId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumenta.fourthId"));

        // Assert first child element
        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumenta.firstId");
        assertEquals("Man Called Slone", uea.getAttributeValue(String.class, "name"));
        assertEquals("Anchor Man", uea.getAttributeValue(String.class, "description"));

        // Assert second child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumenta.secondId");
        assertEquals("Hillary McCain", uea.getAttributeValue(String.class, "name"));
        assertEquals("News Editor", uea.getAttributeValue(String.class, "description"));

        // Assert third child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumenta.thirdId");
        assertNull(uea.getAttributeValue(String.class, "name")); // Name is null
                                                                 // because
                                                                 // no
                                                                 // default
                                                                 // foreign
                                                                 // reference
        assertEquals("Cable Man", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("secondId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumenta.secondId", longNameList.get(0));

        // Assert fourth child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumenta.fourthId");
        assertEquals("Man In Sky", uea.getAttributeValue(String.class, "name"));
        assertEquals("Twiddler", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("firstId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumenta.thirdId", longNameList.get(0));
        assertEquals("test-upldocumenta.secondId", longNameList.get(1));
        assertEquals("test-upldocumenta.firstId", longNameList.get(2));
    }

    @Test
    public void testCompileDocumentLongNameReference() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumenta");
        assertEquals("test-upldocumenta.fourthId",
                uplDocumentAttributes.getAttributeValue(String.class, "longNameRef"));
    }

    @Test
    public void testCompileDocumentWithInheritanceAndNoOverride() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-document-b");

        // Assert document attributes
        UplElementReferences uer = uplDocumentAttributes.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(2, idList.size());
        assertEquals("firstId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        List<String> longNameList = uer.getLongNames();
        assertEquals(2, longNameList.size());
        assertEquals("test-document-b.firstId", longNameList.get(0));
        assertEquals("test-document-b.secondId", longNameList.get(1));

        assertEquals("Man In Sky", uplDocumentAttributes.getAttributeValue(String.class, "fourthName"));

        // Assert that all long names are properly generated
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-document-b.firstId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-document-b.secondId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-document-b.thirdId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-document-b.fourthId"));

        // Assert first child element
        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-document-b.firstId");
        assertEquals("Man Called Slone", uea.getAttributeValue(String.class, "name"));
        assertEquals("Anchor Man", uea.getAttributeValue(String.class, "description"));

        // Assert second child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-document-b.secondId");
        assertEquals("Hillary McCain", uea.getAttributeValue(String.class, "name"));
        assertEquals("News Editor", uea.getAttributeValue(String.class, "description"));

        // Assert third child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-document-b.thirdId");
        assertNull(uea.getAttributeValue(String.class, "name")); // Name is null
                                                                 // because
                                                                 // no
                                                                 // default
                                                                 // foreign
                                                                 // reference
        assertEquals("Cable Man", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("secondId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-document-b.secondId", longNameList.get(0));

        // Assert fourth child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-document-b.fourthId");
        assertEquals("Man In Sky", uea.getAttributeValue(String.class, "name"));
        assertEquals("Twiddler", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("firstId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-document-b.thirdId", longNameList.get(0));
        assertEquals("test-document-b.secondId", longNameList.get(1));
        assertEquals("test-document-b.firstId", longNameList.get(2));
    }

    @Test
    public void testCompileDocumentWithInheritanceAndOverride() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumentc");

        // Assert document attributes
        UplElementReferences uer = uplDocumentAttributes.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("fifthId", idList.get(2));
        List<String> longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumentc.thirdId", longNameList.get(0));
        assertEquals("test-upldocumentc.secondId", longNameList.get(1));
        assertEquals("test-upldocumentc.fifthId", longNameList.get(2));

        assertEquals("Sammy", uplDocumentAttributes.getAttributeValue(String.class, "firstName"));
        assertEquals("Shaka Zulu", uplDocumentAttributes.getAttributeValue(String.class, "thirdName"));

        // Assert that all long names are properly generated
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentc.firstId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentc.secondId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentc.thirdId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentc.fourthId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentc.fifthId"));

        // Assert overriden first child element
        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc.firstId");
        assertEquals("Sammy", uea.getAttributeValue(String.class, "name"));
        assertEquals("Developer", uea.getAttributeValue(String.class, "description"));

        // Assert overriden second child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc.secondId");
        assertEquals("Chip", uea.getAttributeValue(String.class, "name"));
        assertEquals("Newscaster", uea.getAttributeValue(String.class, "description"));

        // Assert inherited third child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc.thirdId");
        assertEquals("Shaka Zulu", uea.getAttributeValue(String.class, "name"));
        assertEquals("Cable Man", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("secondId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumentc.secondId", longNameList.get(0));

        // Assert inherited fourth child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc.fourthId");
        assertEquals("Man In Sky", uea.getAttributeValue(String.class, "name"));
        assertEquals("Twiddler", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("firstId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumentc.thirdId", longNameList.get(0));
        assertEquals("test-upldocumentc.secondId", longNameList.get(1));
        assertEquals("test-upldocumentc.firstId", longNameList.get(2));

        // Assert new fifth child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc.fifthId");
        assertEquals("The Document", uea.getAttributeValue(String.class, "title"));
        assertEquals(Long.valueOf(1515), uea.getAttributeValue(long.class, "count"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("firstId", idList.get(1));
        assertEquals("fourthId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumentc.thirdId", longNameList.get(0));
        assertEquals("test-upldocumentc.firstId", longNameList.get(1));
        assertEquals("test-upldocumentc.fourthId", longNameList.get(2));

        TestElementA inlineUea = uea.getAttributeValue(TestElementA.class, "user");
        uer = inlineUea.getUplAttribute(UplElementReferences.class, "action");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("firstId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumentc.firstId", longNameList.get(0));
        assertEquals("Trish", inlineUea.getUplAttribute(String.class, "name"));
        assertEquals("Singer", inlineUea.getUplAttribute(String.class, "description"));
    }

    @Test
    public void testCompileAttributeWithMultipleInline() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumentc");

        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc.fifthId");

        TestElementA[] inlineUea = uea.getAttributeValue(TestElementA[].class, "manyUser");
        assertNotNull(inlineUea);
        assertEquals(2, inlineUea.length);

        UplElementReferences uer = inlineUea[0].getUplAttribute(UplElementReferences.class, "action");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("firstId", idList.get(0));
        List<String> longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumentc.firstId", longNameList.get(0));

        uer = inlineUea[1].getUplAttribute(UplElementReferences.class, "action");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(2, idList.size());
        assertEquals("firstId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        longNameList = uer.getLongNames();
        assertEquals(2, longNameList.size());
        assertEquals("test-upldocumentc.firstId", longNameList.get(0));
        assertEquals("test-upldocumentc.secondId", longNameList.get(1));

        assertEquals("Tom", inlineUea[0].getUplAttribute(String.class, "name"));
        assertEquals("Founder A", inlineUea[0].getUplAttribute(String.class, "description"));

        assertEquals("Dick", inlineUea[1].getUplAttribute(String.class, "name"));
        assertEquals("Founder B", inlineUea[1].getUplAttribute(String.class, "description"));
    }

    @Test
    public void testCompileWithAllAncestorAttributeExtension() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumentk");

        // Assert attribute extension
        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentk.fifthId");

        TestElementA inlineUea = uea.getAttributeValue(TestElementA.class, "user");
        assertEquals("Dax", inlineUea.getUplAttribute(String.class, "name"));
    }

    @Test
    public void testCompileAttributeWithMultipleInlineMultiline() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumentc-m");

        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentc-m.fifthId");

        TestElementA[] inlineUea = uea.getAttributeValue(TestElementA[].class, "manyUser");
        assertNotNull(inlineUea);
        assertEquals(2, inlineUea.length);

        UplElementReferences uer = inlineUea[0].getUplAttribute(UplElementReferences.class, "action");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("firstId", idList.get(0));
        List<String> longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumentc-m.firstId", longNameList.get(0));

        uer = inlineUea[1].getUplAttribute(UplElementReferences.class, "action");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(2, idList.size());
        assertEquals("firstId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        longNameList = uer.getLongNames();
        assertEquals(2, longNameList.size());
        assertEquals("test-upldocumentc-m.firstId", longNameList.get(0));
        assertEquals("test-upldocumentc-m.secondId", longNameList.get(1));

        assertEquals("Tom", inlineUea[0].getUplAttribute(String.class, "name"));
        assertEquals("Founder A", inlineUea[0].getUplAttribute(String.class, "description"));

        assertEquals("Dick", inlineUea[1].getUplAttribute(String.class, "name"));
        assertEquals("Founder B", inlineUea[1].getUplAttribute(String.class, "description"));
    }

    @Test
    public void testCompileDeepDocument() throws Exception {
        // Deep documents contain child documents
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumentd");

        // Assert document attributes
        UplElementReferences uer = uplDocumentAttributes.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("firstDocumentId", idList.get(0));
        List<String> longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumentd.firstDocumentId", longNameList.get(0));

        assertEquals("Robocop", uplDocumentAttributes.getAttributeValue(String.class, "firstName"));
        assertEquals("Danger Mouse", uplDocumentAttributes.getAttributeValue(String.class, "thirdName"));

        // Assert that all long names are properly generated
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentd.firstDocumentId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentd.firstDocumentId.firstId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentd.firstDocumentId.secondId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentd.firstDocumentId.thirdId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentd.firstDocumentId.fourthId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumentd.firstDocumentId.fifthId"));

        // Assert first child element, which is a document in this case
        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.firstDocumentId");
        uer = uea.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("fifthId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumentd.firstDocumentId.thirdId", longNameList.get(0));
        assertEquals("test-upldocumentd.firstDocumentId.secondId", longNameList.get(1));
        assertEquals("test-upldocumentd.firstDocumentId.fifthId", longNameList.get(2));

        assertEquals("Robocop", uea.getAttributeValue(String.class, "firstName"));
        assertEquals("Danger Mouse", uea.getAttributeValue(String.class, "thirdName"));
        assertEquals("Tommy", uea.getAttributeValue(String.class, "fourthName"));

        // Assert overriden first grand child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.firstDocumentId.firstId");
        assertEquals("Robocop", uea.getAttributeValue(String.class, "name"));
        assertEquals("Developer", uea.getAttributeValue(String.class, "description"));

        // Assert overriden second grand child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.firstDocumentId.secondId");
        assertEquals("Chip", uea.getAttributeValue(String.class, "name"));
        assertEquals("Newscaster", uea.getAttributeValue(String.class, "description"));

        // Assert inherited third grand child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.firstDocumentId.thirdId");
        assertEquals("Danger Mouse", uea.getAttributeValue(String.class, "name"));
        assertEquals("Cable Man", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("secondId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumentd.firstDocumentId.secondId", longNameList.get(0));

        // Assert inherited fourth grand child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.firstDocumentId.fourthId");
        assertEquals("Tommy", uea.getAttributeValue(String.class, "name"));
        assertEquals("Twiddler", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("firstId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumentd.firstDocumentId.thirdId", longNameList.get(0));
        assertEquals("test-upldocumentd.firstDocumentId.secondId", longNameList.get(1));
        assertEquals("test-upldocumentd.firstDocumentId.firstId", longNameList.get(2));

        // Assert new fifth grand child element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.firstDocumentId.fifthId");
        assertEquals("The Document", uea.getAttributeValue(String.class, "title"));
        assertEquals(Long.valueOf(1515), uea.getAttributeValue(long.class, "count"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("firstId", idList.get(1));
        assertEquals("fourthId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumentd.firstDocumentId.thirdId", longNameList.get(0));
        assertEquals("test-upldocumentd.firstDocumentId.firstId", longNameList.get(1));
        assertEquals("test-upldocumentd.firstDocumentId.fourthId", longNameList.get(2));

        TestElementA inlineUea = uea.getAttributeValue(TestElementA.class, "user");
        assertEquals("Trish", inlineUea.getUplAttribute(String.class, "name"));
        assertEquals("Singer", inlineUea.getUplAttribute(String.class, "description"));

        // Assert deep reference element
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumentd.tenthId");
        assertEquals("Optimus Prime", uea.getAttributeValue(String.class, "name"));
        assertEquals("Anchor Man", uea.getAttributeValue(String.class, "description"));
        assertEquals("test-upldocumentd.firstDocumentId.firstId", uea.getAttributeValue(String.class, "componentRef"));
    }

    @Test
    public void testCompileDeeperDocument() throws Exception {
        // Deep documents contain grand child documents
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumente");

        // Assert document attributes
        UplElementReferences uer = uplDocumentAttributes.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        List<String> idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("firstDocumentId", idList.get(0));
        List<String> longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumente.firstDocumentId", longNameList.get(0));

        // Assert that all long names are properly generated
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumente.firstDocumentId"));
        assertTrue(uplDocumentAttributes.isElementWithLongName("test-upldocumente.firstDocumentId.firstDocumentId"));
        assertTrue(uplDocumentAttributes
                .isElementWithLongName("test-upldocumente.firstDocumentId.firstDocumentId.firstId"));
        assertTrue(uplDocumentAttributes
                .isElementWithLongName("test-upldocumente.firstDocumentId.firstDocumentId.secondId"));
        assertTrue(uplDocumentAttributes
                .isElementWithLongName("test-upldocumente.firstDocumentId.firstDocumentId.thirdId"));
        assertTrue(uplDocumentAttributes
                .isElementWithLongName("test-upldocumente.firstDocumentId.firstDocumentId.fourthId"));
        assertTrue(uplDocumentAttributes
                .isElementWithLongName("test-upldocumente.firstDocumentId.firstDocumentId.fifthId"));

        // Assert first child element, which is a document in this case
        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumente.firstDocumentId");
        uer = uea.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("firstDocumentId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId", longNameList.get(0));

        assertEquals("Troop", uea.getAttributeValue(String.class, "firstName"));
        assertEquals("Creep", uea.getAttributeValue(String.class, "thirdName"));

        // Assert first grand child element, also a document in this case
        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumente.firstDocumentId.firstDocumentId");
        uer = uea.getAttributeValue(UplElementReferences.class, "rootList");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("fifthId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.thirdId", longNameList.get(0));
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.secondId", longNameList.get(1));
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.fifthId", longNameList.get(2));

        assertEquals("Troop", uea.getAttributeValue(String.class, "firstName"));
        assertEquals("Creep", uea.getAttributeValue(String.class, "thirdName"));
        assertEquals("Tommy", uea.getAttributeValue(String.class, "fourthName"));

        // Assert overriden first great grand child element
        uea = uplDocumentAttributes
                .getChildElementByLongName("test-upldocumente.firstDocumentId.firstDocumentId.firstId");
        // First name as propagated deep down to great grand child
        assertEquals("Troop", uea.getAttributeValue(String.class, "name"));
        assertEquals("Developer", uea.getAttributeValue(String.class, "description"));

        // Assert overriden second great grand child element
        uea = uplDocumentAttributes
                .getChildElementByLongName("test-upldocumente.firstDocumentId.firstDocumentId.secondId");
        assertEquals("Chip", uea.getAttributeValue(String.class, "name"));
        assertEquals("Newscaster", uea.getAttributeValue(String.class, "description"));

        // Assert inherited third great grand child element
        uea = uplDocumentAttributes
                .getChildElementByLongName("test-upldocumente.firstDocumentId.firstDocumentId.thirdId");
        // Third name as propagated deep down to great grand child
        assertEquals("Creep", uea.getAttributeValue(String.class, "name"));
        assertEquals("Cable Man", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(1, idList.size());
        assertEquals("secondId", idList.get(0));
        longNameList = uer.getLongNames();
        assertEquals(1, longNameList.size());
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.secondId", longNameList.get(0));

        // Assert inherited fourth great grand child element
        uea = uplDocumentAttributes
                .getChildElementByLongName("test-upldocumente.firstDocumentId.firstDocumentId.fourthId");
        assertEquals("Tommy", uea.getAttributeValue(String.class, "name"));
        assertEquals("Twiddler", uea.getAttributeValue(String.class, "description"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("secondId", idList.get(1));
        assertEquals("firstId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.thirdId", longNameList.get(0));
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.secondId", longNameList.get(1));
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.firstId", longNameList.get(2));

        // Assert new fifth great grand child element
        uea = uplDocumentAttributes
                .getChildElementByLongName("test-upldocumente.firstDocumentId.firstDocumentId.fifthId");
        assertEquals("The Document", uea.getAttributeValue(String.class, "title"));
        assertEquals(Long.valueOf(1515), uea.getAttributeValue(long.class, "count"));
        uer = uea.getAttributeValue(UplElementReferences.class, "components");
        assertNotNull(uer);
        idList = uer.getIds();
        assertEquals(3, idList.size());
        assertEquals("thirdId", idList.get(0));
        assertEquals("firstId", idList.get(1));
        assertEquals("fourthId", idList.get(2));
        longNameList = uer.getLongNames();
        assertEquals(3, longNameList.size());
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.thirdId", longNameList.get(0));
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.firstId", longNameList.get(1));
        assertEquals("test-upldocumente.firstDocumentId.firstDocumentId.fourthId", longNameList.get(2));

        TestElementA inlineUea = uea.getAttributeValue(TestElementA.class, "user");
        assertEquals("Trish", inlineUea.getUplAttribute(String.class, "name"));
        assertEquals("Singer", inlineUea.getUplAttribute(String.class, "description"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCompileGeneratedDocument() throws Exception {
        UplDocumentAttributes uplDocumentAttributes1 =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-uplgenerator>g>type1");
        assertNotNull(uplDocumentAttributes1);
        assertEquals("test-generateddoc", uplDocumentAttributes1.getComponentName());
        String[] names =
                ((List<String>) (uplDocumentAttributes1.getAttributeValue(List.class, "names"))).toArray(new String[0]);
        assertNotNull(names);
        assertEquals(3, names.length);
        assertEquals("Tweak", names[0]);
        assertEquals("Peak", names[1]);
        assertEquals("Leak", names[2]);

        UplElementAttributes uea =
                uplDocumentAttributes1.getChildElementByLongName("test-uplgenerator>g>type1.namesDocumentId");
        assertNotNull(uea);
        names = ((List<String>) (uea.getAttributeValue(List.class, "names"))).toArray(new String[0]);
        assertNotNull(names);
        assertEquals(3, names.length);
        assertEquals("Tweak", names[0]);
        assertEquals("Peak", names[1]);
        assertEquals("Leak", names[2]);

        UplDocumentAttributes uplDocumentAttributes2 =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-uplgenerator>g>type2");
        assertNotNull(uplDocumentAttributes2);
        assertEquals("test-generateddoc", uplDocumentAttributes2.getComponentName());
        names = ((List<String>) (uplDocumentAttributes2.getAttributeValue(List.class, "names"))).toArray(new String[0]);
        assertNotNull(names);
        assertEquals(2, names.length);
        assertEquals("Gain", names[0]);
        assertEquals("Train", names[1]);

        uea = uplDocumentAttributes2.getChildElementByLongName("test-uplgenerator>g>type2.namesDocumentId");
        assertNotNull(uea);
        names = ((List<String>) (uea.getAttributeValue(List.class, "names"))).toArray(new String[0]);
        assertNotNull(names);
        assertEquals(2, names.length);
        assertEquals("Gain", names[0]);
        assertEquals("Train", names[1]);

        assertFalse(uplDocumentAttributes1 == uplDocumentAttributes2);
    }

    @Test
    public void testCompileGeneratedDocumentWithNewVersion() throws Exception {
        UplDocumentAttributes uplDocumentAttributes1 =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-uplgenerator>g>type1");
        assertNotNull(uplDocumentAttributes1);

        UplDocumentAttributes uplDocumentAttributes2 =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-uplgenerator>g>type2");
        assertNotNull(uplDocumentAttributes2);
        assertFalse(uplDocumentAttributes1 == uplDocumentAttributes2);

        UplDocumentAttributes uplDocumentAttributes3 =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-uplgenerator>g>type1");
        assertNotNull(uplDocumentAttributes3);
        assertFalse(uplDocumentAttributes1 == uplDocumentAttributes3);

        UplDocumentAttributes uplDocumentAttributes4 =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-uplgenerator>g>type2");
        assertNotNull(uplDocumentAttributes4);
        assertTrue(uplDocumentAttributes2 == uplDocumentAttributes4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFarForeignAttributeContainerDefault() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumenti");
        assertNotNull(uplDocumentAttributes);
        String[] name =
                ((List<String>) (uplDocumentAttributes.getAttributeValue(List.class, "names"))).toArray(new String[0]);
        assertNotNull(name);
        assertEquals(3, name.length);
        assertEquals("Bling", name[0]);
        assertEquals("Cling", name[1]);
        assertEquals("Sling", name[2]);

        UplElementAttributes uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumenti.namesDocumentId");
        assertNotNull(uea);
        name = ((List<String>) (uea.getAttributeValue(List.class, "names"))).toArray(new String[0]);
        assertNotNull(name);
        assertEquals(3, name.length);
        assertEquals("Bling", name[0]);
        assertEquals("Cling", name[1]);
        assertEquals("Sling", name[2]);

        uea = uplDocumentAttributes.getChildElementByLongName("test-upldocumenti.namesDocumentId.namesElementId");
        assertNotNull(uea);
        name = uea.getAttributeValue(String[].class, "names");
        assertNotNull(name);
        assertEquals(3, name.length);
        assertEquals("Bling", name[0]);
        assertEquals("Cling", name[1]);
        assertEquals("Sling", name[2]);
    }

    @Test
    public void testDeepFarForeignAttributeContainerDefault() throws Exception {
        UplDocumentAttributes uplDocumentAttributes =
                uplCompiler.compileComponentDocuments(Locale.getDefault(), "test-upldocumentj");

        UplElementAttributes uea = uplDocumentAttributes
                .getChildElementByLongName("test-upldocumentj.iDocument.namesDocumentId.namesElementId");
        assertNotNull(uea);
        String[] name = uea.getAttributeValue(String[].class, "names");
        assertNotNull(name);
        assertEquals(3, name.length);
        assertEquals("Bling", name[0]);
        assertEquals("Cling", name[1]);
        assertEquals("Sling", name[2]);
    }

    @Test
    public void testGetUplAttributesInfo() throws Exception {
        UplAttributesInfo uai = uplCompiler.getUplAttributesInfo("test-uplelementa");
        assertNotNull(uai);
        assertEquals(8, uai.size());

        assertTrue(uai.isAttribute("name"));
        UplAttributeInfo uaii = uai.getUplAttributeInfo("name");
        assertNotNull(uaii);
        assertEquals(String.class, uaii.getAttributeClass());
        assertNull(uaii.getDefaultValue());
        assertFalse(uaii.isMandatory());
    }

    @Override
	protected void doAddSettingsAndDependencies() throws Exception {
        addContainerSetting("uplcompiler.test.tiger", "Unify Tiger");
        addContainerSetting("uplcompiler.test.sanders", "Unify Sanders");
	}

	@Override
    protected void onSetup() throws Exception {
        uplCompiler = (UplCompiler) getComponent(ApplicationComponents.APPLICATION_UPLCOMPILER);
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
