/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Tree item type information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class TreeItemTypeInfo {

    private String code;

    private String icon;

    private String itemCaptionKey;

    private List<String> menuCodeList;

    private List<String> acceptDropList;

    private int eventFlags;

    private int buoyancy;

    private TreeItemTypeInfo(String code, String icon, String itemCaptionKey, List<String> menuCodeList,
            List<String> acceptDropList, int eventFlags, int buoyancy) {
        this.code = code;
        this.icon = icon;
        this.itemCaptionKey = itemCaptionKey;
        this.eventFlags = eventFlags;
        this.menuCodeList = menuCodeList;
        this.acceptDropList = acceptDropList;
        this.buoyancy = buoyancy;
    }

    public String getCode() {
        return code;
    }

    public String getIcon() {
        return icon;
    }

    public String getItemCaptionKey() {
        return itemCaptionKey;
    }

    public List<String> getMenuCodeList() {
        return menuCodeList;
    }

    public boolean isMenuCodeList() {
        return menuCodeList != null && !menuCodeList.isEmpty();
    }

    public List<String> getAcceptDropList() {
        return acceptDropList;
    }

    public boolean isAcceptDropList() {
        return acceptDropList != null && !acceptDropList.isEmpty();
    }

    public boolean isDraggable() {
        return (eventFlags & TreeEventType.TREEITEM_DRAG.flag()) > 0;
    }
    
    public int getEventFlags() {
        return eventFlags;
    }

    public int getBuoyancy() {
        return buoyancy;
    }

    public static Builder newBuilder(String code) {
        return new Builder(code);
    }

    public static class Builder {

        private String code;

        private String icon;

        private String itemCaptionKey;

        private List<String> acceptDropList;

        private List<String> menuCodeList;

        private int eventFlags;

        private int buoyancy;

        private Builder(String code) {
            this.code = code;
        }

        public Builder useIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder useItemCaptionKey(String itemCaptionKey) {
            this.itemCaptionKey = itemCaptionKey;
            return this;
        }

        public Builder atBuoyancy(int buoyancy) {
            this.buoyancy = buoyancy;
            return this;
        }

        public Builder listenTo(TreeEventType... eventTypes) {
            for (TreeEventType eventType : eventTypes) {
                eventFlags |= eventType.flag();
            }

            return this;
        }

        public Builder useMenuItems(String... codes) throws UnifyException {
            for (String code : codes) {
                useMenuItem(code);
            }

            return this;
        }

        public Builder useMenuItem(String code) throws UnifyException {
            if (menuCodeList == null) {
                menuCodeList = new ArrayList<String>();
            }

            menuCodeList.add(code);
            listenTo(TreeEventType.TREEITEM_RIGHTCLICK);
            return this;
        }

        public Builder acceptDrops(String... typeCodes) throws UnifyException {
            for (String typeCode : typeCodes) {
                acceptDrop(typeCode);
            }

            return this;
        }

        public Builder acceptDrop(String typeCode) throws UnifyException {
            if (acceptDropList == null) {
                acceptDropList = new ArrayList<String>();
            }

            acceptDropList.add(typeCode);
            return this;
        }

        public TreeItemTypeInfo build() {
            return new TreeItemTypeInfo(code, icon, itemCaptionKey, DataUtils.unmodifiableList(menuCodeList),
                    DataUtils.unmodifiableList(acceptDropList), eventFlags, buoyancy);
        }
    }
}
