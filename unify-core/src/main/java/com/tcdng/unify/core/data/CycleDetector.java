/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.ArrayList;
import java.util.List;

/**
 * The cyclic detector class. Detects a cycle by attempting to build one from a
 * list of references. Lets say we have a set of objects A, B, C and D that
 * refer to one another like
 * 
 * <pre>
 * A -&gt; B
 * B -&gt; C
 * C -&gt; D
 * D -&gt; B
 * </pre>
 * 
 * We can detect a cycle by
 * 
 * <pre>
 * <code>{@code
 * CycleDetector&lt;Object&gt; cd = new CycleDetector&lt;Object&gt;();
 * cd.addReference(A, B)
 *   .addReference(B, C)
 *   .addReference(C, D)
 *   .addReference(D, B);
 * if(!cd.detect().isEmpty())
 *   //Cycle detected
 *   ...
 * }
 * </code>
 * </pre>
 * 
 * @author The Code Department
 */
public class CycleDetector<T> {

    private List<T> referenceList;

    public CycleDetector() {
        referenceList = new ArrayList<T>();
    }

    /**
     * Adds a reference to the detector's reference list.
     * 
     * @param referrer
     * @param referee
     * @return this cycle detector
     */
    public CycleDetector<T> addReference(T referrer, T referee) {
        if (referrer == null || referee == null) {
            throw new IllegalArgumentException();
        }
        referenceList.add(referrer);
        referenceList.add(referee);
        return this;
    }

    /**
     * Removes the last reference.
     */
    public CycleDetector<T> removeLast() {
        if (!referenceList.isEmpty()) {
            referenceList.remove(referenceList.size() - 1);
            referenceList.remove(referenceList.size() - 1);
        }
        return this;
    }

    /**
     * Clears the detector's reference list
     */
    public CycleDetector<T> clear() {
        referenceList.clear();
        return this;
    }

    public boolean isEmpty() {
        return referenceList.isEmpty();
    }

    /**
     * Detects a cycle in the detector's reference list.
     * 
     * @return a list containing a trail of detected cycle, otherwise an empty list
     *         is returned
     */
    public List<T> detect() {
        List<T> resultList = new ArrayList<T>();
        for (int i = 0; i < referenceList.size(); i += 2) {
            T a = referenceList.get(i);
            T b = referenceList.get(i + 1);
            resultList.add(a);
            if (scanChain(resultList, b)) {
                break;
            }
            resultList.clear();
        }
        return resultList;
    }

    private boolean scanChain(List<T> resultList, T b) {
        for (int j = 0; j < referenceList.size(); j += 2) {
            T c = referenceList.get(j);
            if (c.equals(b)) {
                if (resultList.contains(c)) {
                    resultList.add(c);
                    return true;
                }
                resultList.add(c);
                if (scanChain(resultList, referenceList.get(j + 1))) {
                    return true;
                }
                resultList.remove(resultList.size() - 1);
            }
        }
        return false;
    }
}
