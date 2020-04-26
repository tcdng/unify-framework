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

package com.tcdng.unify.core.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.criterion.BeginsWithField;
import com.tcdng.unify.core.criterion.EndsWithField;
import com.tcdng.unify.core.criterion.EqualsField;
import com.tcdng.unify.core.criterion.GreaterField;
import com.tcdng.unify.core.criterion.GreaterOrEqualField;
import com.tcdng.unify.core.criterion.LessField;
import com.tcdng.unify.core.criterion.LessOrEqualField;
import com.tcdng.unify.core.criterion.LikeField;
import com.tcdng.unify.core.criterion.NotBeginWithField;
import com.tcdng.unify.core.criterion.NotEndWithField;
import com.tcdng.unify.core.criterion.NotEqualsField;
import com.tcdng.unify.core.criterion.NotLikeField;

/**
 * Bean filter by field tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BeanFilterByFieldTest {

    @Test
    public void testFilterEqualsField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new EqualsField("name", "description"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));

        beanFilter = new BeanFilter(new EqualsField("costPrice", "salesPrice"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterNotEqualsField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotEqualsField("name", "description"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));

        beanFilter = new BeanFilter(new NotEqualsField("costPrice", "salesPrice"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterGreaterThanField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new GreaterField("salesPrice", "costPrice"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterGreaterThanEqualField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new GreaterOrEqualField("salesPrice", "costPrice"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterLessThanField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new LessField("salesPrice", "costPrice"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterLessThanEqualField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new LessOrEqualField("salesPrice", "costPrice"));
        assertTrue(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterLikeField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new LikeField("description", "name"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterNotLikeField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotLikeField("description", "name"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterBeginsWithField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new BeginsWithField("description", "name"));
        assertTrue(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterNotBeginWithField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotBeginWithField("description", "name"));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterEndsWithField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new EndsWithField("description", "name"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterNotEndWithField() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotEndWithField("description", "name"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }
}
