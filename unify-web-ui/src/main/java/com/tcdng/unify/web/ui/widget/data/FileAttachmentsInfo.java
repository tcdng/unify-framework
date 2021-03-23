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
package com.tcdng.unify.web.ui.widget.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * File attachments information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FileAttachmentsInfo {

    private Object parentId;

    private List<FileAttachmentInfo> attachmentInfoList;

    private FileAttachmentType defaultType;

    private String handlerName;

    private int selectedIndex;

    private int maxAutoItems;

    private boolean viewTimestamped;

    private boolean adhoc;

    private boolean disabled;

    /**
     * Creates new instance of file attachments information in ad-hoc mode.
     * 
     * @param defaultType
     *                     the default file attachment type
     * @param maxAutoItems
     *                     the maximum number of attachments
     */
    public FileAttachmentsInfo(FileAttachmentType defaultType, int maxAutoItems) {
        this(null, defaultType, maxAutoItems);
    }

    /**
     * Creates new instance of file attachments information in ad-hoc mode.
     * 
     * @param parentId
     *                     the ID of the entity that attachments are attached to.
     *                     This is optional
     * @param defaultType
     *                     the default file attachment type
     * @param maxAutoItems
     *                     the maximum number of attachments
     */
    public FileAttachmentsInfo(Object parentId, FileAttachmentType defaultType, int maxAutoItems) {
        this.parentId = parentId;
        this.defaultType = defaultType;
        this.maxAutoItems = maxAutoItems;
        this.attachmentInfoList = new ArrayList<FileAttachmentInfo>();
        this.viewTimestamped = true;
        this.adhoc = true;
        this.attach();
    }

    /**
     * Creates new instance of file attachments information in fixed mode.
     * 
     * @param fileAttachmentInfo
     *                           array of file attachment information.
     */
    public FileAttachmentsInfo(FileAttachmentInfo... fileAttachmentInfo) {
        this(null, Arrays.asList(fileAttachmentInfo));
    }

    /**
     * Creates new instance of file attachments information in fixed mode.
     * 
     * @param fileAttachmentInfoList
     *                               list of file attachment information. Defines
     *                               fixed list.
     */
    public FileAttachmentsInfo(List<FileAttachmentInfo> fileAttachmentInfoList) {
        this(null, fileAttachmentInfoList);
    }

    /**
     * Creates new instance of file attachments information in fixed mode.
     * 
     * @param parentId
     *                           the ID of the entity that attachments are attached
     *                           to. This is optional
     * @param fileAttachmentInfo
     *                           array of file attachment information.
     */
    public FileAttachmentsInfo(Object parentId, FileAttachmentInfo... fileAttachmentInfo) {
        this(parentId, Arrays.asList(fileAttachmentInfo));
    }

    /**
     * Creates new instance of file attachments information in fixed mode.
     * 
     * @param parentId
     *                               the ID of the entity that attachments are
     *                               attached to. This is optional
     * @param fileAttachmentInfoList
     *                               list of file attachment information. Defines
     *                               fixed list.
     */
    public FileAttachmentsInfo(Object parentId, List<FileAttachmentInfo> fileAttachmentInfoList) {
        this.parentId = parentId;
        this.maxAutoItems = 0;
        this.defaultType = null;
        this.attachmentInfoList = Collections.unmodifiableList(fileAttachmentInfoList);
        this.viewTimestamped = true;
        this.adhoc = false;
    }

    private FileAttachmentsInfo(Object parentId, List<FileAttachmentInfo> attachmentInfoList,
            FileAttachmentType defaultType, String handlerName, int maxAutoItems, boolean viewTimestamped,
            boolean adhoc) {
        this.parentId = parentId;
        this.attachmentInfoList = attachmentInfoList;
        this.defaultType = defaultType;
        this.handlerName = handlerName;
        this.maxAutoItems = maxAutoItems;
        this.viewTimestamped = viewTimestamped;
        this.adhoc = adhoc;
    }

    public void attach() {
        if (this.adhoc) {
            if (!this.attachmentInfoList.isEmpty()) {
                if (this.attachmentInfoList.get(this.attachmentInfoList.size() - 1).isEmpty()) {
                    return;
                }
            }

            if (this.maxAutoItems > 0 && this.maxAutoItems <= this.attachmentInfoList.size()) {
                return;
            }

            this.attachmentInfoList.add(new FileAttachmentInfo(defaultType));
        }
    }

    public FileAttachmentInfo detach() {
        FileAttachmentInfo result = null;
        if (this.adhoc) {
            result = this.attachmentInfoList.remove(this.selectedIndex);
            this.attach();
        } else {
            result = this.attachmentInfoList.get(this.selectedIndex);
            result.setFilename(null);
            result.setAttachment(null);
        }

        return result;
    }

    public List<FileAttachmentInfo> getAttachmentInfoList() {
        return attachmentInfoList;
    }

    public FileAttachmentInfo getSelectedAttachmentInfo() {
        return this.attachmentInfoList.get(this.selectedIndex);
    }

    public FileAttachmentInfo getAttachmentInfo(int index) {
        return this.attachmentInfoList.get(index);
    }

    public Object getParentId() {
        return parentId;
    }

    public FileAttachmentType getDefaultType() {
        return defaultType;
    }

    public int size() {
        return this.attachmentInfoList.size();
    }

    public int getMaxAutoItems() {
        return maxAutoItems;
    }

    public boolean isViewTimestamped() {
        return viewTimestamped;
    }

    public boolean isAdhoc() {
        return adhoc;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public static Builder newBuilder(Object parentId) {
        return new Builder(parentId);
    }

    public static class Builder {

        private Object parentId;

        private Map<String, FileAttachmentInfo> attachmentInfos;

        private FileAttachmentType defaultType;

        private String handlerName;

        private int maxAutoItems;

        private boolean viewTimestamped;

        private boolean adhoc;

        public Builder(Object parentId) {
            this.parentId = parentId;
        }

        public Builder defaultAttachmentType(FileAttachmentType defaultType) {
            this.defaultType = defaultType;
            return this;
        }

        public Builder attachmentHandler(String handlerName) {
            this.handlerName = handlerName;
            return this;
        }

        public Builder maxAutoItems(int maxAutoItems) {
            this.maxAutoItems = maxAutoItems;
            return this;
        }

        public Builder viewTimestamped(boolean viewTimestamped) {
            this.viewTimestamped = viewTimestamped;
            return this;
        }

        public Builder adhoc(boolean adhoc) {
            this.adhoc = adhoc;
            return this;
        }

        public Builder addFileAttachmentInfo(FileAttachmentType type, String name, String description) {
            return this.addFileAttachmentInfo(type, name, description, null);
        }

        public Builder addFileAttachmentInfo(FileAttachmentType type, String name, String description,
                String filename) {
            checkAndEnsureAttachmentInfos(name);
            attachmentInfos.put(name, new FileAttachmentInfo(type, name, description, filename));
            return this;
        }

        public void checkAndEnsureAttachmentInfos(String name) {
            if (attachmentInfos == null) {
                attachmentInfos = new LinkedHashMap<String, FileAttachmentInfo>();
            }

            if (attachmentInfos.containsKey(name)) {
                throw new RuntimeException(
                        "File attachment information with name [" + name + "] already exists in this object.");
            }
        }

        public FileAttachmentsInfo build() {
            List<FileAttachmentInfo> attachmentInfoList = null;
            if (adhoc) {
                attachmentInfoList = new ArrayList<FileAttachmentInfo>();
                if (attachmentInfos != null) {
                    attachmentInfoList.addAll(attachmentInfos.values());
                }
            } else {
                attachmentInfoList = Collections.emptyList();
                if (attachmentInfos != null) {
                    attachmentInfoList = DataUtils.unmodifiableList(attachmentInfos.values());
                }
            }

            return new FileAttachmentsInfo(parentId, attachmentInfoList, defaultType, handlerName, maxAutoItems,
                    viewTimestamped, adhoc);
        }
    }

}
