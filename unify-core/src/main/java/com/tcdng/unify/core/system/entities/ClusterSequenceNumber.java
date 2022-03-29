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
package com.tcdng.unify.core.system.entities;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;

/**
 * Entity for storing sequence number generation information.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "UNCLUSTERSEQ", uniqueConstraints = { @UniqueConstraint({ "sequenceName"}) })
public class ClusterSequenceNumber extends AbstractSystemSequencedEntity {

    @Column(name = "SEQUENCE_NM", length = 256)
    private String sequenceName;

    @Column
    private Long sequenceCounter;

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Long getSequenceCounter() {
        return sequenceCounter;
    }

    public void setSequenceCounter(Long sequenceCounter) {
        this.sequenceCounter = sequenceCounter;
    }

}
