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

package com.tcdng.unify.web.ui.widget.data;

import java.util.List;

/**
 * Tree event.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TreeEvent {

    private TreeEventType type;

    private String menuCode;

    private List<Long> itemIdList;
    
    private Long dropTrgItemId;
    
    private String srcLongName;
    
    private List<Long> srcItemIdList;

    public TreeEvent(TreeEventType type, String menuCode, List<Long> itemIdList) {
        this.type = type;
        this.menuCode = menuCode;
        this.itemIdList = itemIdList;
    }

    public TreeEvent(TreeEventType type, List<Long> itemIdList, Long dropTrgItemId, String srcLongName, List<Long> srcItemIdList) {
        this.type = type;
        this.itemIdList = itemIdList;
        this.dropTrgItemId = dropTrgItemId;
        this.srcLongName = srcLongName;
        this.srcItemIdList = srcItemIdList;
    }

    public TreeEventType getType() {
        return type;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public List<Long> getItemIdList() {
        return itemIdList;
    }

    public Long getDropTrgItemId() {
        return dropTrgItemId;
    }

    public String getSrcLongName() {
        return srcLongName;
    }

    public List<Long> getSrcItemIdList() {
        return srcItemIdList;
    }
}
