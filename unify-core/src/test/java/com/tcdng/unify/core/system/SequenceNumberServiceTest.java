/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.system.entities.ClusterDateSequenceNumber;
import com.tcdng.unify.core.system.entities.ClusterUniqueString;
import com.tcdng.unify.core.task.TaskManager;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Sequence number service tests (Standalone mode).
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SequenceNumberServiceTest extends AbstractUnifyComponentTest {

    @Test
    public void testGetNextSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);

        snService.reset();
        assertEquals(Long.valueOf(1L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(2L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(3L), snService.getNextSequenceNumber("sequenceA"));

        assertEquals(Long.valueOf(4L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(5L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(6L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(7L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(8L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(9L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(10L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(11L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(12L), snService.getNextSequenceNumber("sequenceA"));

        assertEquals(Long.valueOf(13L), snService.getNextSequenceNumber("sequenceA"));

        assertEquals(Long.valueOf(1L), snService.getNextSequenceNumber("sequenceB"));
        assertEquals(Long.valueOf(2L), snService.getNextSequenceNumber("sequenceB"));
        assertEquals(Long.valueOf(3L), snService.getNextSequenceNumber("sequenceB"));
        assertEquals(Long.valueOf(4L), snService.getNextSequenceNumber("sequenceB"));

        assertEquals(Long.valueOf(14L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(15L), snService.getNextSequenceNumber("sequenceA"));
        assertEquals(Long.valueOf(16L), snService.getNextSequenceNumber("sequenceA"));
    }

    @Test
    public void testMultiThreadGetNextSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        snService.reset();
        TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
        Map<String, Object> inputParameters1 = new HashMap<String, Object>();
        inputParameters1.put(SequenceNumberTestTaskConstants.SEQUENCEID, "sequenceA");
        inputParameters1.put(SequenceNumberTestTaskConstants.SEQUENCECOUNT, 773);
        TaskMonitor taskMonitor1 = taskManager.startTask("sequencenumber-test", inputParameters1, false, null);

        Map<String, Object> inputParameters3 = new HashMap<String, Object>();
        inputParameters3.put(SequenceNumberTestTaskConstants.SEQUENCEID, "sequenceB");
        inputParameters3.put(SequenceNumberTestTaskConstants.SEQUENCECOUNT, 345);
        TaskMonitor taskMonitor3 = taskManager.startTask("sequencenumber-test", inputParameters3, false, null);

        Map<String, Object> inputParameters2 = new HashMap<String, Object>();
        inputParameters2.put(SequenceNumberTestTaskConstants.SEQUENCEID, "sequenceA");
        inputParameters2.put(SequenceNumberTestTaskConstants.SEQUENCECOUNT, 411);
        TaskMonitor taskMonitor2 = taskManager.startTask("sequencenumber-test", inputParameters2, false, null);

        Map<String, Object> inputParameters4 = new HashMap<String, Object>();
        inputParameters4.put(SequenceNumberTestTaskConstants.SEQUENCEID, "sequenceC");
        inputParameters4.put(SequenceNumberTestTaskConstants.SEQUENCECOUNT, 206);
        TaskMonitor taskMonitor4 = taskManager.startTask("sequencenumber-test", inputParameters4, false, null);

        while (!taskMonitor1.isDone() || !taskMonitor2.isDone() || !taskMonitor3.isDone() || !taskMonitor4.isDone()) {
            Thread.yield();
        }

        Long sequenceNo = snService.getNextSequenceNumber("sequenceA");
        assertEquals(Long.valueOf(773L + 411L + 1L), sequenceNo);

        sequenceNo = snService.getNextSequenceNumber("sequenceB");
        assertEquals(Long.valueOf(345L + 1L), sequenceNo);

        sequenceNo = snService.getNextSequenceNumber("sequenceC");
        assertEquals(Long.valueOf(206L + 1L), sequenceNo);
    }

    @Test
    public void testSameDayGetNextDateSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Date testDate = CalendarUtils.getMidnightDate(new Date());
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(testDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(testDate);
        cal2.add(Calendar.HOUR, 1);
        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(testDate);
        cal3.add(Calendar.HOUR, 2);

        Long sequenceNo1 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", cal1.getTime());
        Long sequenceNo2 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", cal2.getTime());
        Long sequenceNo3 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", cal3.getTime());
        assertEquals(Long.valueOf(1), sequenceNo1);
        assertEquals(Long.valueOf(2), sequenceNo2);
        assertEquals(Long.valueOf(3), sequenceNo3);
    }

    @Test
    public void testSameSequenceSameDateGetNextDateSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Date testDate = new Date();
        Long sequenceNo1 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", testDate);
        Long sequenceNo2 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", testDate);
        Long sequenceNo3 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", testDate);
        assertEquals(Long.valueOf(1), sequenceNo1);
        assertEquals(Long.valueOf(2), sequenceNo2);
        assertEquals(Long.valueOf(3), sequenceNo3);
    }

    @Test
    public void testDifferentSequenceSameDateGetNextDateSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Date testDate = new Date();
        Long sequenceNo1 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", testDate);
        Long sequenceNo2 = snService.getNextSequenceNumber("day-rpt-batch-counter", testDate);
        Long sequenceNo3 = snService.getNextSequenceNumber("day-outward-posting-batch-counter", testDate);
        assertEquals(Long.valueOf(1), sequenceNo1);
        assertEquals(Long.valueOf(1), sequenceNo2);
        assertEquals(Long.valueOf(1), sequenceNo3);
    }

    @Test
    public void testSameSequenceDifferentDateGetNextDateSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.DAY_OF_YEAR, 2);

        Long sequenceNo1 = snService.getNextSequenceNumber("day-rpt-batch-counter", cal1.getTime());
        Long sequenceNo2 = snService.getNextSequenceNumber("day-rpt-batch-counter", cal2.getTime());
        Long sequenceNo3 = snService.getNextSequenceNumber("day-rpt-batch-counter", cal3.getTime());
        assertEquals(Long.valueOf(1), sequenceNo1);
        assertEquals(Long.valueOf(1), sequenceNo2);
        assertEquals(Long.valueOf(1), sequenceNo3);
    }

    @Test
    public void testDifferentSequenceDifferentDateGetNextDateSequenceNumber() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.DAY_OF_YEAR, 2);

        Long sequenceNo1 = snService.getNextSequenceNumber("day-cheque-upload-batch-counter", cal1.getTime());
        Long sequenceNo2 = snService.getNextSequenceNumber("day-rpt-batch-counter", cal2.getTime());
        Long sequenceNo3 = snService.getNextSequenceNumber("day-outward-posting-batch-counter", cal3.getTime());
        assertEquals(Long.valueOf(1), sequenceNo1);
        assertEquals(Long.valueOf(1), sequenceNo2);
        assertEquals(Long.valueOf(1), sequenceNo3);
    }

    @Test
    public void testMultiThreadGetNextDateSequenceNumber() throws Exception {
        TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
        Date testDate = new Date();
        Map<String, Object> inputParameters1 = new HashMap<String, Object>();
        inputParameters1.put(DateSequenceNumberTaskConstants.SEQUENCENAME, "day-cheque-upload-batch-counter");
        inputParameters1.put(DateSequenceNumberTaskConstants.DATE, testDate);
        inputParameters1.put(DateSequenceNumberTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor1 = taskManager.startTask("datesequencenumber-task", inputParameters1, true, null);

        Map<String, Object> inputParameters2 = new HashMap<String, Object>();
        inputParameters2.put(DateSequenceNumberTaskConstants.SEQUENCENAME, "day-rpt-batch-counter");
        inputParameters2.put(DateSequenceNumberTaskConstants.DATE, testDate);
        inputParameters2.put(DateSequenceNumberTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor2 = taskManager.startTask("datesequencenumber-task", inputParameters2, true, null);

        Map<String, Object> inputParameters3 = new HashMap<String, Object>();
        inputParameters3.put(DateSequenceNumberTaskConstants.SEQUENCENAME, "day-outward-posting-batch-counter");
        inputParameters3.put(DateSequenceNumberTaskConstants.DATE, testDate);
        inputParameters3.put(DateSequenceNumberTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor3 = taskManager.startTask("datesequencenumber-task", inputParameters3, true, null);

        Map<String, Object> inputParameters4 = new HashMap<String, Object>();
        inputParameters4.put(DateSequenceNumberTaskConstants.SEQUENCENAME, "I've got a feeling!");
        inputParameters4.put(DateSequenceNumberTaskConstants.DATE, testDate);
        inputParameters4.put(DateSequenceNumberTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor4 = taskManager.startTask("datesequencenumber-task", inputParameters4, true, null);

        while (!taskMonitor1.isDone() || !taskMonitor2.isDone() || !taskMonitor3.isDone() || !taskMonitor4.isDone()) {
            Thread.yield();
        }

        assertEquals("Test task 1 failed", 0, taskMonitor1.getExceptions().length);
        assertEquals("Test task 2 failed", 0, taskMonitor2.getExceptions().length);
        assertEquals("Test task 3 failed", 0, taskMonitor3.getExceptions().length);
        assertEquals("Test task 4 failed", 0, taskMonitor4.getExceptions().length);
    }

    @Test
    public void testSingleGetUniqueStringId() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Long id = snService.getUniqueStringId("this.is.a.unique.string");
        assertNotNull(id);
    }

    @Test
    public void testMultipleGetSameUniqueStringId() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Long id1 = snService.getUniqueStringId("this.is.a.unique.string");
        Long id2 = snService.getUniqueStringId("this.is.a.unique.string");
        Long id3 = snService.getUniqueStringId("this.is.a.unique.string");
        Long id4 = snService.getUniqueStringId("this.is.a.unique.string");
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertEquals(id1, id2);
        assertEquals(id2, id3);
        assertEquals(id3, id4);
    }

    @Test
    public void testSingleGetDifferentUniqueStringId() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Long id1 = snService.getUniqueStringId("this.is.a.unique.string");
        Long id2 = snService.getUniqueStringId("this.is.another.unique.string");
        Long id3 = snService.getUniqueStringId("this.is.some.unique.string");
        Long id4 = snService.getUniqueStringId("this.is.some.other.unique.string");
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertFalse(id1.equals(id2));
        assertFalse(id1.equals(id3));
        assertFalse(id1.equals(id4));
        assertFalse(id2.equals(id3));
        assertFalse(id2.equals(id4));
        assertFalse(id3.equals(id4));
    }

    @Test
    public void testMultipleGetDifferentUniqueStringId() throws Exception {
        SequenceNumberService snService =
                (SequenceNumberService) getComponent(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE);
        Long id1 = snService.getUniqueStringId("this.is.a.unique.string");
        Long id2 = snService.getUniqueStringId("this.is.another.unique.string");
        Long id3 = snService.getUniqueStringId("this.is.some.unique.string");
        Long id4 = snService.getUniqueStringId("this.is.some.other.unique.string");
        Long id5 = snService.getUniqueStringId("this.is.a.unique.string");
        Long id6 = snService.getUniqueStringId("this.is.another.unique.string");
        Long id7 = snService.getUniqueStringId("this.is.some.unique.string");
        Long id8 = snService.getUniqueStringId("this.is.some.other.unique.string");
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertFalse(id1.equals(id2));
        assertFalse(id1.equals(id3));
        assertFalse(id1.equals(id4));
        assertFalse(id2.equals(id3));
        assertFalse(id2.equals(id4));
        assertFalse(id3.equals(id4));
        assertEquals(id1, id5);
        assertEquals(id2, id6);
        assertEquals(id3, id7);
        assertEquals(id4, id8);
    }

    @Test
    public void testMultiThreadGetUniqueStringId() throws Exception {
        String[] uniqueString =
                { "this.is.a.unique.string", "this.is.another.unique.string", "this.is.some.other.unique.string" };

        TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
        Map<String, Object> inputParameters1 = new HashMap<String, Object>();
        inputParameters1.put(UniqueStringTestTaskConstants.UNIQUESTRINGLIST, uniqueString);
        inputParameters1.put(UniqueStringTestTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor1 = taskManager.startTask("uniquestringtest-task", inputParameters1, true, null);

        Map<String, Object> inputParameters2 = new HashMap<String, Object>();
        inputParameters2.put(UniqueStringTestTaskConstants.UNIQUESTRINGLIST, uniqueString);
        inputParameters2.put(UniqueStringTestTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor2 = taskManager.startTask("uniquestringtest-task", inputParameters2, true, null);

        Map<String, Object> inputParameters3 = new HashMap<String, Object>();
        inputParameters3.put(UniqueStringTestTaskConstants.UNIQUESTRINGLIST, uniqueString);
        inputParameters3.put(UniqueStringTestTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor3 = taskManager.startTask("uniquestringtest-task", inputParameters3, true, null);

        Map<String, Object> inputParameters4 = new HashMap<String, Object>();
        inputParameters4.put(UniqueStringTestTaskConstants.UNIQUESTRINGLIST, uniqueString);
        inputParameters4.put(UniqueStringTestTaskConstants.ITERATIONS, 28);
        TaskMonitor taskMonitor4 = taskManager.startTask("uniquestringtest-task", inputParameters4, true, null);

        while (!taskMonitor1.isDone() || !taskMonitor2.isDone() || !taskMonitor3.isDone() || !taskMonitor4.isDone()) {
            Thread.yield();
        }

        if (taskMonitor1.getExceptions().length > 0) {
            taskMonitor1.getExceptions()[0].printStackTrace();
        }
        assertEquals("Test task 1 failed", 0, taskMonitor1.getExceptions().length);
        if (taskMonitor2.getExceptions().length > 0) {
            taskMonitor2.getExceptions()[0].printStackTrace();
        }
        assertEquals("Test task 2 failed", 0, taskMonitor2.getExceptions().length);
        if (taskMonitor3.getExceptions().length > 0) {
            taskMonitor3.getExceptions()[0].printStackTrace();
        }
        assertEquals("Test task 3 failed", 0, taskMonitor3.getExceptions().length);
        if (taskMonitor4.getExceptions().length > 0) {
            taskMonitor4.getExceptions()[0].printStackTrace();
        }
        assertEquals("Test task 4 failed", 0, taskMonitor4.getExceptions().length);
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        addDependency(ApplicationComponents.APPLICATION_SEQUENCENUMBERSERVICE, SequenceNumberServiceImpl.class, true,
                true, new Setting("sequenceBlockSize", "11"));
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        this.deleteAll(ClusterUniqueString.class, ClusterDateSequenceNumber.class);
    }
}
