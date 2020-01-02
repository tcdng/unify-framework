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
package com.tcdng.unify.core.system.entities;

import java.util.Date;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;

/**
 * Entity for storing date sequence number generation information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "UNCLUSTERDATESEQ", uniqueConstraints = { @UniqueConstraint({ "sequenceName", "sequenceDate" }) })
public class ClusterDateSequenceNumber extends AbstractSystemSequencedEntity {

    @Column(name = "SEQUENCE_NM", length = 256)
    private String sequenceName;

    @Column(name = "SEQUENCE_DT")
    private Date sequenceDate;

    @Column
    private Long sequenceCounter;

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Date getSequenceDate() {
        return sequenceDate;
    }

    public void setSequenceDate(Date sequenceDate) {
        this.sequenceDate = sequenceDate;
    }

    public Long getSequenceCounter() {
        return sequenceCounter;
    }

    public void setSequenceCounter(Long sequenceCounter) {
        this.sequenceCounter = sequenceCounter;
    }

}
