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

import java.util.ArrayList;
import java.util.List;

/**
 * History data structure
 * 
 * @author The Code Department
 * @since 1.0
 */
public class History<T> {
    
    protected static final int DEFAULT_HISTORY_LEN = 10;
    
    private static final int MINIMUM_HISTORY_LEN = 1;

    private List<T> list;
    
    private int max;

    private boolean keepUnique;
    
    public History(int max) {
        this(max, false);
    }

    public History() {
        this(DEFAULT_HISTORY_LEN);
    }
    
    protected History(int max, boolean keepUnique) {
        this.max = max > 0 ? max: MINIMUM_HISTORY_LEN;
        this.list = new ArrayList<T>(this.max);
        this.keepUnique = keepUnique;
    }
    
    public T add(T item) {
        if (keepUnique) {
            list.remove(item);
        }
        
        T oldest = null;
        if (list.size() == max) {
            oldest = list.remove(0);
        }

        list.add(item);
        return oldest;
    }
    
    public T get(int index) {
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + list.size());
        }
        
        return list.get(index);
    }
    
    public void clear(int history) {
        while(history > 0 && !list.isEmpty()) {
            list.remove(0);
            history--;
        }
    }
    
    public void clear() {
        list.clear();
    }
    
    public int capacity() {
        return max;
    }
    
    public int size() {
        return list.size();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
