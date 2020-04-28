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

import java.util.Arrays;

import org.junit.Test;

import com.tcdng.unify.core.criterion.Amongst;
import com.tcdng.unify.core.criterion.BeginsWith;
import com.tcdng.unify.core.criterion.Between;
import com.tcdng.unify.core.criterion.CriteriaBuilder;
import com.tcdng.unify.core.criterion.EndsWith;
import com.tcdng.unify.core.criterion.Equals;
import com.tcdng.unify.core.criterion.EqualsCollection;
import com.tcdng.unify.core.criterion.Greater;
import com.tcdng.unify.core.criterion.GreaterCollection;
import com.tcdng.unify.core.criterion.GreaterOrEqual;
import com.tcdng.unify.core.criterion.GreaterOrEqualCollection;
import com.tcdng.unify.core.criterion.IsNotNull;
import com.tcdng.unify.core.criterion.IsNull;
import com.tcdng.unify.core.criterion.Less;
import com.tcdng.unify.core.criterion.LessCollection;
import com.tcdng.unify.core.criterion.LessOrEqual;
import com.tcdng.unify.core.criterion.LessOrEqualCollection;
import com.tcdng.unify.core.criterion.Like;
import com.tcdng.unify.core.criterion.NotAmongst;
import com.tcdng.unify.core.criterion.NotBeginWith;
import com.tcdng.unify.core.criterion.NotBetween;
import com.tcdng.unify.core.criterion.NotEndWith;
import com.tcdng.unify.core.criterion.NotEquals;
import com.tcdng.unify.core.criterion.NotEqualsCollection;
import com.tcdng.unify.core.criterion.NotLike;

/**
 * Bean filter by value tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BeanFilterByValueTest {

    @Test
    public void testFilterEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new Equals("name", "specs"));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterNotEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotEquals("name", "bandana"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterGreaterThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 10.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 9.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new Greater("costPrice", 10.00));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterGreaterThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 72.45);

        BeanFilter beanFilter = new BeanFilter(new GreaterOrEqual("salesPrice", 60.00));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterLessThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new Less("costPrice", 20.00));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterLessThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new LessOrEqual("costPrice", 20.00));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterLike() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new Like("description", "an"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterNotLike() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotLike("description", "an"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterBeginsWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new BeginsWith("description", "Blue"));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterNotBeginWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotBeginWith("description", "hat"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterEndsWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new EndsWith("description", "Red"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterNotEndWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotEndWith("description", "ana"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterBetween() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);

        BeanFilter beanFilter = new BeanFilter(new Between("costPrice", 45.00, 50.00));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterNotBetween() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);

        BeanFilter beanFilter = new BeanFilter(new NotBetween("costPrice", 45.00, 50.00));
        assertTrue(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterIsNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        BeanFilter beanFilter = new BeanFilter(new IsNull("salesPrice"));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterIsNotNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        BeanFilter beanFilter = new BeanFilter(new IsNotNull("salesPrice"));
        assertTrue(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterAmongst() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        BeanFilter beanFilter = new BeanFilter(new Amongst("name", Arrays.asList("specs", "pants")));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterNotAmongst() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        BeanFilter beanFilter = new BeanFilter(new NotAmongst("name", Arrays.asList("specs", "pants")));
        assertTrue(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterShallowAnd() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        BeanFilter beanFilter = new BeanFilter(new CriteriaBuilder().beginAnd().addBetween("costPrice", 45.00, 50.00)
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
        assertFalse(beanFilter.match(e));
    }

    @Test
    public void testFilterDeepAnd() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        BeanFilter beanFilter = new BeanFilter(new CriteriaBuilder().beginAnd().beginAnd()
                .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
        assertFalse(beanFilter.match(e));
    }

    @Test
    public void testFilterShallowOr() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        BeanFilter beanFilter = new BeanFilter(new CriteriaBuilder().beginOr().addBetween("costPrice", 45.00, 50.00)
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
        assertTrue(beanFilter.match(e));
    }

    @Test
    public void testFilterDeepOr() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        BeanFilter beanFilter = new BeanFilter(new CriteriaBuilder().beginOr().beginAnd()
                .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
        assertTrue(beanFilter.match(e));
    }

    @Test
    public void testFilterCollectionNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d =
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5)));

        BeanFilter beanFilter = new BeanFilter(new IsNull("orders"));
        assertTrue(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionNotNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d =
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5)));

        BeanFilter beanFilter = new BeanFilter(new IsNotNull("orders"));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d =
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5)));

        BeanFilter beanFilter = new BeanFilter(new EqualsCollection("orders", 2));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeNotEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d =
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5)));

        BeanFilter beanFilter = new BeanFilter(new NotEqualsCollection("orders", 2));
        assertTrue(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeGreaterThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d =
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5), new Order("Scott", "Oracle Avenue", 5)));

        BeanFilter beanFilter = new BeanFilter(new GreaterCollection("orders", 2));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeGreaterThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d =
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5), new Order("Scott", "Oracle Avenue", 5)));

        BeanFilter beanFilter = new BeanFilter(new GreaterOrEqualCollection("orders", 2));
        assertFalse(beanFilter.match(a));
        assertFalse(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertTrue(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeLessThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new LessCollection("orders", 2));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertFalse(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeLessThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        BeanFilter beanFilter = new BeanFilter(new LessOrEqualCollection("orders", 2));
        assertFalse(beanFilter.match(a));
        assertTrue(beanFilter.match(b));
        assertTrue(beanFilter.match(c));
        assertFalse(beanFilter.match(d));
    }
}
