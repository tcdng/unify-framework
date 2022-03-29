/*
 * Copyright 2018-2022 The Code Department.
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
import com.tcdng.unify.core.data.BeanValueStore;
import com.tcdng.unify.core.data.ValueStore;

/**
 * Object filter by value tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ObjectFilterByValueTest {

    @Test
    public void testFilterEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new Equals("name", "specs"));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterNotEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new NotEquals("name", "bandana"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterGreaterThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 10.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 9.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new Greater("costPrice", 10.00));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterGreaterThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 72.45);

        ObjectFilter objectFilter = new ObjectFilter(new GreaterOrEqual("salesPrice", 60.00));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterLessThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new Less("costPrice", 20.00));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterLessThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new LessOrEqual("costPrice", 20.00));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterLike() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new Like("description", "an"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterNotLike() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new NotLike("description", "an"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterBeginsWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new BeginsWith("description", "Blue"));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterNotBeginWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new NotBeginWith("description", "hat"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterEndsWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new EndsWith("description", "Red"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterNotEndWith() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "hat In Red", 60.00, 52.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new NotEndWith("description", "ana"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterBetween() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new Between("costPrice", 45.00, 50.00));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterNotBetween() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new NotBetween("costPrice", 45.00, 50.00));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterIsNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        ObjectFilter objectFilter = new ObjectFilter(new IsNull("salesPrice"));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterIsNotNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        ObjectFilter objectFilter = new ObjectFilter(new IsNotNull("salesPrice"));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterAmongst() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        ObjectFilter objectFilter = new ObjectFilter(new Amongst("name", Arrays.asList("specs", "pants")));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterNotAmongst() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, null);
        Product d = new Product("pants", "Wonder pants", 49.50, null);

        ObjectFilter objectFilter = new ObjectFilter(new NotAmongst("name", Arrays.asList("specs", "pants")));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterShallowAnd() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginAnd()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
        assertFalse(objectFilter.match(e));
    }

    @Test
    public void testFilterDeepAnd() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginAnd().beginAnd()
                .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
        assertFalse(objectFilter.match(e));
    }

    @Test
    public void testFilterShallowOr() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginOr()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
        assertTrue(objectFilter.match(e));
    }

    @Test
    public void testFilterDeepOr() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 25.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00);
        Product c = new Product("specs", "Blue Spectacles", 45.00, 45.00);
        Product d = new Product("pants", "Wonder pants", 49.50, 17.45);
        Product e = new Product("tie", "Blue Tie", 60.00, 60.00);

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginOr().beginAnd()
                .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
        assertTrue(objectFilter.match(e));
    }

    @Test
    public void testFilterCollectionNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));

        ObjectFilter objectFilter = new ObjectFilter(new IsNull("orders"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionNotNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));

        ObjectFilter objectFilter = new ObjectFilter(new IsNotNull("orders"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));

        ObjectFilter objectFilter = new ObjectFilter(new EqualsCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeNotEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));

        ObjectFilter objectFilter = new ObjectFilter(new NotEqualsCollection("orders", 2));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeGreaterThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5),
                        new Order("Scott", "Oracle Avenue", 5)));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeGreaterThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5),
                        new Order("Scott", "Oracle Avenue", 5)));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterOrEqualCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeLessThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new LessCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterCollectionSizeLessThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2)));
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5)));
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new LessOrEqualCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" });

        ObjectFilter objectFilter = new ObjectFilter(new IsNull("days"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterArraynNotNull() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" });

        ObjectFilter objectFilter = new ObjectFilter(new IsNotNull("days"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayLengthEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" });

        ObjectFilter objectFilter = new ObjectFilter(new EqualsCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayLengthNotEquals() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" });

        ObjectFilter objectFilter = new ObjectFilter(new NotEqualsCollection("days", 2));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayLengthGreaterThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                new String[] { "Monday", "Tuesday", "Wednesday" });

        ObjectFilter objectFilter = new ObjectFilter(new GreaterCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayLengthGreaterThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45,
                new String[] { "Monday", "Tuesday", "Wednesday" });

        ObjectFilter objectFilter = new ObjectFilter(new GreaterOrEqualCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayLengthLessThan() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new LessCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testFilterArrayLengthLessThanEqual() throws Exception {
        Product a = new Product("bandana", "bandana", 20.00, 19.25);
        Product b = new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" });
        Product c = new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" });
        Product d = new Product("pants", "Wonder pants", 15.00, 17.45);

        ObjectFilter objectFilter = new ObjectFilter(new LessOrEqualCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterEquals() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new Equals("name", "specs"));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterNotEquals() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new NotEquals("name", "bandana"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterGreaterThan() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 10.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 9.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new Greater("costPrice", 10.00));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterGreaterThanEqual() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 72.45));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterOrEqual("salesPrice", 60.00));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterLessThan() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new Less("costPrice", 20.00));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterLessThanEqual() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new LessOrEqual("costPrice", 20.00));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterLike() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new Like("description", "an"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterNotLike() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new NotLike("description", "an"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterBeginsWith() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "hat In Red", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new BeginsWith("description", "Blue"));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterNotBeginWith() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "hat In Red", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new NotBeginWith("description", "hat"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterEndsWith() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "hat In Red", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new EndsWith("description", "Red"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterNotEndWith() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "hat In Red", 60.00, 52.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new NotEndWith("description", "ana"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterBetween() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new Between("costPrice", 45.00, 50.00));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterNotBetween() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new NotBetween("costPrice", 45.00, 50.00));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterIsNull() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, null));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, null));

        ObjectFilter objectFilter = new ObjectFilter(new IsNull("salesPrice"));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterIsNotNull() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, null));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, null));

        ObjectFilter objectFilter = new ObjectFilter(new IsNotNull("salesPrice"));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterAmongst() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, null));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, null));

        ObjectFilter objectFilter = new ObjectFilter(new Amongst("name", Arrays.asList("specs", "pants")));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterNotAmongst() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, null));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, null));

        ObjectFilter objectFilter = new ObjectFilter(new NotAmongst("name", Arrays.asList("specs", "pants")));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterShallowAnd() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, 17.45));
        ValueStore e = new BeanValueStore(new Product("tie", "Blue Tie", 60.00, 60.00));

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginAnd()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
        assertFalse(objectFilter.match(e));
    }

    @Test
    public void testValueStoreFilterDeepAnd() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, 17.45));
        ValueStore e = new BeanValueStore(new Product("tie", "Blue Tie", 60.00, 60.00));

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginAnd().beginAnd()
                .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
        assertFalse(objectFilter.match(e));
    }

    @Test
    public void testValueStoreFilterShallowOr() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, 17.45));
        ValueStore e = new BeanValueStore(new Product("tie", "Blue Tie", 60.00, 60.00));

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginOr()
                .addBetween("costPrice", 45.00, 50.00).addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
        assertTrue(objectFilter.match(e));
    }

    @Test
    public void testValueStoreFilterDeepOr() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 25.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 45.00));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 49.50, 17.45));
        ValueStore e = new BeanValueStore(new Product("tie", "Blue Tie", 60.00, 60.00));

        ObjectFilter objectFilter = new ObjectFilter(new CriteriaBuilder().beginOr().beginAnd()
                .addGreaterThanEqual("costPrice", 45.00).addLessThanEqual("costPrice", 50.00).endCompound()
                .addBeginsWith("description", "B").endCompound().build());
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
        assertTrue(objectFilter.match(e));
    }

    @Test
    public void testValueStoreFilterCollectionNull() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));

        ObjectFilter objectFilter = new ObjectFilter(new IsNull("orders"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionNotNull() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));

        ObjectFilter objectFilter = new ObjectFilter(new IsNotNull("orders"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionSizeEquals() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));

        ObjectFilter objectFilter = new ObjectFilter(new EqualsCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionSizeNotEquals() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));

        ObjectFilter objectFilter = new ObjectFilter(new NotEqualsCollection("orders", 2));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionSizeGreaterThan() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5), new Order("Scott", "Oracle Avenue", 5))));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionSizeGreaterThanEqual() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, Arrays.asList(new Order("Abel", "24 Parklane", 2),
                        new Order("Scott", "Oracle Avenue", 5), new Order("Scott", "Oracle Avenue", 5))));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterOrEqualCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionSizeLessThan() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new LessCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterCollectionSizeLessThanEqual() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(
                new Product("hat", "Red Hat", 60.00, 60.00, Arrays.asList(new Order("Abel", "24 Parklane", 2))));
        ValueStore c = new BeanValueStore(new Product("specs", "Blue Spectacles", 45.00, 42.00,
                Arrays.asList(new Order("Abel", "24 Parklane", 2), new Order("Scott", "Oracle Avenue", 5))));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new LessOrEqualCollection("orders", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayNull() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" }));

        ObjectFilter objectFilter = new ObjectFilter(new IsNull("days"));
        assertTrue(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArraynNotNull() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" }));

        ObjectFilter objectFilter = new ObjectFilter(new IsNotNull("days"));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayLengthEquals() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" }));

        ObjectFilter objectFilter = new ObjectFilter(new EqualsCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayLengthNotEquals() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday" }));

        ObjectFilter objectFilter = new ObjectFilter(new NotEqualsCollection("days", 2));
        assertTrue(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayLengthGreaterThan() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday", "Wednesday" }));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayLengthGreaterThanEqual() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(
                new Product("pants", "Wonder pants", 15.00, 17.45, new String[] { "Monday", "Tuesday", "Wednesday" }));

        ObjectFilter objectFilter = new ObjectFilter(new GreaterOrEqualCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertFalse(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertTrue(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayLengthLessThan() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new LessCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertFalse(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }

    @Test
    public void testValueStoreFilterArrayLengthLessThanEqual() throws Exception {
        ValueStore a = new BeanValueStore(new Product("bandana", "bandana", 20.00, 19.25));
        ValueStore b = new BeanValueStore(new Product("hat", "Red Hat", 60.00, 60.00, new String[] { "Monday" }));
        ValueStore c = new BeanValueStore(
                new Product("specs", "Blue Spectacles", 45.00, 42.00, new String[] { "Monday", "Tuesday" }));
        ValueStore d = new BeanValueStore(new Product("pants", "Wonder pants", 15.00, 17.45));

        ObjectFilter objectFilter = new ObjectFilter(new LessOrEqualCollection("days", 2));
        assertFalse(objectFilter.match(a));
        assertTrue(objectFilter.match(b));
        assertTrue(objectFilter.match(c));
        assertFalse(objectFilter.match(d));
    }
}
