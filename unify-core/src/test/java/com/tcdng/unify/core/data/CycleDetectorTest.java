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
package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * Cyclic detector tests
 * 
 * @author The Code Department
 * @since 4.1
 */
public class CycleDetectorTest {

    @Test
    public void testEmptyReference() throws Exception {
        CycleDetector<String> cd = new CycleDetector<String>();
        assertEquals(0, cd.detect().size());
    }

    @Test
    public void testNonCyclicSingleReference() throws Exception {
        CycleDetector<String> cd = new CycleDetector<String>();
        cd.addReference("a", "b");
        assertEquals(0, cd.detect().size());
    }

    @Test
    public void testCyclicSingleReference() throws Exception {
        CycleDetector<String> cd = new CycleDetector<String>();
        cd.addReference("a", "a");
        List<String> cycle = cd.detect();
        assertEquals(2, cycle.size());
        assertEquals("a", cycle.get(0));
        assertEquals("a", cycle.get(1));
    }

    @Test
    public void testNonCyclicMultipleReferences() throws Exception {
        CycleDetector<String> cd = new CycleDetector<String>();
        cd.addReference("a", "b");
        cd.addReference("b", "c");
        cd.addReference("c", "d");
        cd.addReference("d", "e");
        assertEquals(0, cd.detect().size());

        // Reordering should not affect non cyclic references
        cd.clear();
        cd.addReference("d", "e");
        cd.addReference("a", "b");
        cd.addReference("c", "d");
        cd.addReference("b", "c");
        assertEquals(0, cd.detect().size());
    }

    @Test
    public void testCyclicMultipleReferences() throws Exception {
        CycleDetector<String> cd = new CycleDetector<String>();
        cd.addReference("a", "b");
        cd.addReference("b", "c");
        cd.addReference("c", "d");
        cd.addReference("d", "e");
        cd.addReference("e", "a");
        List<String> cycle = cd.detect();
        assertEquals(6, cycle.size());
        assertEquals("[a, b, c, d, e, a]", cycle.toString());

        // Reordering should not affect cyclic references
        cd.clear();
        cd.addReference("d", "e");
        cd.addReference("a", "b");
        cd.addReference("e", "a");
        cd.addReference("c", "d");
        cd.addReference("b", "c");
        cycle = cd.detect();
        assertEquals(6, cycle.size());
        assertEquals("[d, e, a, b, c, d]", cycle.toString());

        // Try some other reordering
        cd.clear();
        cd.addReference("b", "c");
        cd.addReference("d", "e");
        cd.addReference("a", "b");
        cd.addReference("c", "d");
        cd.addReference("e", "a");
        cycle = cd.detect();
        assertEquals(6, cycle.size());
        assertEquals("[b, c, d, e, a, b]", cycle.toString());

        // Sprinkle some non cyclic references into the cyclic mix. Should still
        // work
        cd.clear();
        cd.addReference("c", "m");
        cd.addReference("c", "n");
        cd.addReference("b", "c");
        cd.addReference("d", "e");
        cd.addReference("d", "z");
        cd.addReference("a", "b");
        cd.addReference("c", "d");
        cd.addReference("d", "k");
        cd.addReference("e", "a");
        cd.addReference("a", "y");
        cycle = cd.detect();
        assertEquals(6, cycle.size());
        assertEquals("[b, c, d, e, a, b]", cycle.toString());
    }

    @Test
    public void testLassoCyclicReferences() throws Exception {
        CycleDetector<String> cd = new CycleDetector<String>();
        cd.addReference("b", "a");
        cd.addReference("a", "c");
        cd.addReference("c", "a");
        List<String> cycle = cd.detect();
        assertEquals(4, cycle.size());
        assertEquals("[b, a, c, a]", cycle.toString());
    }
}
