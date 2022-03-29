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
package com.tcdng.unify.core.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.constant.BooleanType;

/**
 * Database table entity child category CRUD tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseChildCategoryCRUDTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testUpdateLeanRecordByIdWithBlankChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly LoanReport");
            report.setParameters(null); // blank
            db.updateLeanById(report);

            LoanReport fetchedReport = db.find(LoanReport.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly LoanReport", fetchedReport.getDescription());

            List<LoanReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly LoanReport");
            report.setParameters(Arrays.asList(new LoanReportParameter("resolutionDate")));
            db.updateLeanById(report);

            LoanReport fetchedReport = db.find(LoanReport.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly LoanReport", fetchedReport.getDescription());

            List<LoanReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly LoanReport");
            report.setParameters(Arrays.asList(new LoanReportParameter("resolutionDate")));
            db.updateByIdVersion(report);

            LoanReport fetchedReport = db.find(LoanReport.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly LoanReport", fetchedReport.getDescription());

            List<LoanReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(1, reportParamList.size());
            assertEquals("resolutionDate", reportParamList.get(0).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersion() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setColor("green");
            apple.setPrice(50.00);
            assertEquals(1, db.updateLeanByIdVersion(apple));

            Fruit foundFruit = db.find(Fruit.class, apple.getId());
            assertEquals(apple, foundFruit);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersionWithBlankChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly LoanReport");
            report.setParameters(null); // Blank child list
            db.updateLeanByIdVersion(report);

            LoanReport fetchedReport = db.find(LoanReport.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly LoanReport", fetchedReport.getDescription());

            List<LoanReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly LoanReport");
            report.setParameters(Arrays.asList(new LoanReportParameter("resolutionDate")));
            db.updateLeanByIdVersion(report);

            LoanReport fetchedReport = db.find(LoanReport.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly LoanReport", fetchedReport.getDescription());

            List<LoanReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());

            int count = db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithNullChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setParameters(null);
            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());

            int count = db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            LoanReportParameter reportForm = new LoanReportParameter("sampleEditor");
            report.setReportForm(reportForm);

            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());
            assertEquals(id, reportForm.getReportId());

            int count = db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            LoanReportParameter rpStart = new LoanReportParameter("startDate");
            LoanReportParameter rpEnd = new LoanReportParameter("endDate");
            report.addParameter(rpStart).addParameter(rpEnd);

            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());
            assertEquals(id, rpStart.getReportId());
            assertEquals(id, rpEnd.getReportId());

            int count = db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(2, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateMultipleRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id1 = (Long) db.create(report);

            report = new LoanReport("salaryReport", "Salary LoanReport");
            report.addParameter(new LoanReportParameter("staffNo", BooleanType.FALSE));
            Long id2 = (Long) db.create(report);

            int count = db.countAll(new LoanReportParameterQuery().reportId(id1));
            assertEquals(2, count);

            count = db.countAll(new LoanReportParameterQuery().reportId(id2));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("greenEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.findLean(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.findLean(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("greenEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("greenEditor", foundReport.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.findLean(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.findLean(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("beanEditor", foundReport.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithSelectChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(new LoanReportQuery().addEquals("id", id).addSelect("name", "reportForm"));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertNull(foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("beanEditor", foundReport.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithSelectChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(new LoanReportQuery().addEquals("id", id).addSelect("name", "parameters"));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertNull(foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("editor10"));
            db.create(report);

            List<LoanReport> list = db.findAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            assertNull(list.get(0).getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            db.create(report);

            List<LoanReport> list = db.findAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            assertNull(list.get(0).getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllWithChildrenWithChildOnly() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("editor10"));
            db.create(report);

            List<LoanReport> list = db.findAllWithChildren(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            LoanReportParameter reportForm = list.get(0).getReportForm();
            assertNotNull(reportForm);
            assertEquals("editor10", reportForm.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllWithChildrenWithChildOnlyAndSelect() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("editor10"));
            db.create(report);

            List<LoanReport> list = db.findAllWithChildren(
                    new LoanReportQuery().addSelect("name", "description").ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            LoanReportParameter reportForm = list.get(0).getReportForm();
            assertNull(reportForm);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllWithChildrenWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            db.create(report);

            List<LoanReport> list = db.findAllWithChildren(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            List<LoanReportParameter> parameterList = list.get(0).getParameters();
            assertNotNull(parameterList);
            assertEquals(2, parameterList.size());
            LoanReportParameter rp = parameterList.get(0);
            assertNotNull(rp);
            assertEquals("startDate", rp.getName());
            assertEquals(BooleanType.FALSE, rp.getScheduled());
            assertNull(rp.getScheduledDesc());

            rp = parameterList.get(1);
            assertNotNull(rp);
            assertEquals("endDate", rp.getName());
            assertEquals(BooleanType.FALSE, rp.getScheduled());
            assertNull(rp.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllWithChildrenWithChildListWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            db.create(report);

            List<LoanReport> list = db.findAllWithChildren(
                    new LoanReportQuery().addSelect("name", "description").ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            List<LoanReportParameter> parameterList = list.get(0).getParameters();
            assertNull(parameterList);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());
            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("beanEditor", foundReport.getReportForm().getName());
            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordById() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.find(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());
            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("beanEditor", foundReport.getReportForm().getName());
            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindChildren() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.findLean(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
            assertNull(foundReport.getParameters());

            db.findChildren(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());
            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("beanEditor", foundReport.getReportForm().getName());
            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("cyanEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.listLean(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.listLean(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("cyanEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.list(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("cyanEditor", foundReport.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.list(LoanReport.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            assertEquals("startDate", foundReport.getParameters().get(0).getName());
            assertEquals("endDate", foundReport.getParameters().get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("grayEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.listLean(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.listLean(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("grayEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.list(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("grayEditor", foundReport.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.list(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            assertEquals("startDate", foundReport.getParameters().get(0).getName());
            assertEquals("endDate", foundReport.getParameters().get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithSelectChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("grayEditor"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db
                    .list(new LoanReportQuery().addEquals("id", id).addSelect("description", "reportForm"));
            assertNotNull(foundReport);
            assertNull(foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("grayEditor", foundReport.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithSelectChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.list(new LoanReportQuery().addEquals("id", id).addSelect("parameters"));
            assertNotNull(foundReport);
            assertNull(foundReport.getName());
            assertNull(foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            assertEquals("startDate", foundReport.getParameters().get(0).getName());
            assertEquals("endDate", foundReport.getParameters().get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("blueEditor"));
            db.create(report);

            List<LoanReport> list = db.listAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            assertNull(list.get(0).getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            db.create(report);

            List<LoanReport> list = db.listAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            assertNull(list.get(0).getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllWithChildrenWithChildOnly() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("editor10"));
            db.create(report);

            List<LoanReport> list = db.listAllWithChildren(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            LoanReportParameter reportForm = list.get(0).getReportForm();
            assertNotNull(reportForm);
            assertEquals("form", reportForm.getCategory());
            assertEquals("editor10", reportForm.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllWithChildrenWithChildOnlyWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("editor10"));
            db.create(report);

            List<LoanReport> list = db.listAllWithChildren(
                    new LoanReportQuery().addSelect("name", "description").ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            LoanReportParameter reportForm = list.get(0).getReportForm();
            assertNull(reportForm);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllWithChildrenWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            db.create(report);

            List<LoanReport> list = db.listAllWithChildren(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            List<LoanReportParameter> parameterList = list.get(0).getParameters();
            assertNotNull(parameterList);
            assertEquals(2, parameterList.size());
            LoanReportParameter rp = parameterList.get(0);
            assertNotNull(rp);
            assertEquals("startDate", rp.getName());
            assertEquals(BooleanType.FALSE, rp.getScheduled());
            assertEquals("False", rp.getScheduledDesc());

            rp = parameterList.get(1);
            assertNotNull(rp);
            assertEquals("endDate", rp.getName());
            assertEquals(BooleanType.FALSE, rp.getScheduled());
            assertEquals("False", rp.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllWithChildrenWithChildListWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            db.create(report);

            List<LoanReport> list = db.listAllWithChildren(
                    new LoanReportQuery().addSelect("name", "description").ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly LoanReport", list.get(0).getDescription());

            List<LoanReportParameter> parameterList = list.get(0).getParameters();
            assertNull(parameterList);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListChildren() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("beanEditor"));
            report.addParameter(new LoanReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new LoanReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            LoanReport foundReport = db.findLean(new LoanReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
            assertNull(foundReport.getParameters());

            db.listChildren(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly LoanReport", foundReport.getDescription());
            assertNotNull(foundReport.getReportForm());
            assertEquals("form", foundReport.getReportForm().getCategory());
            assertEquals("beanEditor", foundReport.getReportForm().getName());
            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            LoanReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertEquals("Weekly LoanReport", rParam.getReportDesc());
            assertEquals("False", rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("parameter", rParam.getCategory());
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertEquals("Weekly LoanReport", rParam.getReportDesc());
            assertEquals("True", rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setParameters(new ArrayList<LoanReportParameter>());
            Long id = (Long) db.create(report);
            db.delete(LoanReport.class, id);
            assertEquals(0, db.countAll(new LoanReportQuery().addEquals("id", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNullChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setParameters(null);
            Long id = (Long) db.create(report);
            db.delete(LoanReport.class, id);
            assertEquals(0, db.countAll(new LoanReportQuery().addEquals("id", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("sampleEditor"));
            Long id = (Long) db.create(report);
            db.delete(LoanReport.class, id);
            assertEquals(0, db.countAll(new LoanReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate")).addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);
            db.delete(LoanReport.class, id);
            assertEquals(0, db.countAll(new LoanReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdVersionWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("sampleEditor"));
            Long id = (Long) db.create(report);
            report = db.find(LoanReport.class, id);
            db.deleteByIdVersion(report);
            assertEquals(0, db.countAll(new LoanReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate"));
            report.addParameter(new LoanReportParameter("endDate"));
            Long id = (Long) db.create(report);
            report = db.find(LoanReport.class, id);
            db.deleteByIdVersion(report);
            assertEquals(0, db.countAll(new LoanReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            LoanReportParameter reportForm = new LoanReportParameter("sampleEditor");
            report.setReportForm(reportForm);
            db.create(report);
            db.deleteAll(new LoanReportQuery().ignoreEmptyCriteria(true));

            int count = db.countAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);

            count = db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate"));
            report.addParameter(new LoanReportParameter("endDate"));
            db.create(report);
            db.deleteAll(new LoanReportQuery().ignoreEmptyCriteria(true));

            int count = db.countAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);

            count = db.countAll(new LoanReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteSingleFromMultipleRecordsWithChild() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.setReportForm(new LoanReportParameter("editor1"));
            Long id1 = (Long) db.create(report);

            report = new LoanReport("salaryReport", "Salary LoanReport");
            report.setReportForm(new LoanReportParameter("editor2"));
            Long id2 = (Long) db.create(report);

            db.deleteAll(new LoanReportQuery().addEquals("id", id1));

            int count = db.countAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);

            report = db.find(LoanReport.class, id2);
            assertNotNull(report);
            assertEquals("salaryReport", report.getName());
            assertEquals("Salary LoanReport", report.getDescription());
            assertNotNull(report.getReportForm());
            assertEquals("form", report.getReportForm().getCategory());
            assertEquals("editor2", report.getReportForm().getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteSingleFromMultipleRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanReport report = new LoanReport("weeklyReport", "Weekly LoanReport");
            report.addParameter(new LoanReportParameter("startDate"));
            report.addParameter(new LoanReportParameter("endDate"));
            Long id1 = (Long) db.create(report);

            report = new LoanReport("salaryReport", "Salary LoanReport");
            report.addParameter(new LoanReportParameter("staffNo"));
            Long id2 = (Long) db.create(report);

            db.deleteAll(new LoanReportQuery().addEquals("id", id1));

            int count = db.countAll(new LoanReportQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);

            report = db.find(LoanReport.class, id2);
            assertNotNull(report);
            assertEquals("salaryReport", report.getName());
            assertEquals("Salary LoanReport", report.getDescription());
            assertEquals(1, report.getParameters().size());
            assertEquals("staffNo", report.getParameters().get(0).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        addContainerSetting(UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT, 8);
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(LoanReportParameter.class, LoanReport.class);
    }
}
