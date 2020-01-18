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
package com.tcdng.unify.core.database.sql;

/**
 * A cache for storing native basic CRUD SQL for a record type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlCache {

    private String findSql;
    private String findViewSql;
    private String findByPkSql;
    private String findByPkVersionSql;
    private String listSql;
    private String listByPkSql;
    private String listByPkVersionSql;
    private String createSql;
    private String updateSql;
    private String updateByPkSql;
    private String updateByPkVersionSql;
    private String deleteSql;
    private String deleteByPkSql;
    private String deleteByPkVersionSql;
    private String countSql;
    private String countViewSql;
    private String testSql;

    public SqlCache(String findSql, String findViewSql, String findByPkSql, final String findByPkVersionSql, String listSql,
            final String listByPkSql, String listByPkVersionSql, final String createSql, String updateSql,
            final String updateByPkSql, String updateByPkVersionSql, final String deleteSql, String deleteByPkSql,
            final String deleteByPkVersionSql, String countSql, String countViewSql, final String testSql) {
        this.findSql = findSql;
        this.findViewSql = findViewSql;
        this.findByPkSql = findByPkSql;
        this.findByPkVersionSql = findByPkVersionSql;
        this.listSql = listSql;
        this.listByPkSql = listByPkSql;
        this.listByPkVersionSql = listByPkVersionSql;
        this.createSql = createSql;
        this.updateSql = updateSql;
        this.updateByPkSql = updateByPkSql;
        this.updateByPkVersionSql = updateByPkVersionSql;
        this.deleteSql = deleteSql;
        this.deleteByPkSql = deleteByPkSql;
        this.deleteByPkVersionSql = deleteByPkVersionSql;
        this.countSql = countSql;
        this.countViewSql = countViewSql;
        this.testSql = testSql;
    }

    public String getFindSql() {
        return findSql;
    }

    public String getFindViewSql() {
        return findViewSql;
    }

    public String getFindByPkSql() {
        return findByPkSql;
    }

    public String getFindByPkVersionSql() {
        return findByPkVersionSql;
    }

    public String getListSql() {
        return listSql;
    }

    public String getListByPkSql() {
        return listByPkSql;
    }

    public String getListByPkVersionSql() {
        return listByPkVersionSql;
    }

    public String getCreateSql() {
        return createSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getUpdateByPkSql() {
        return updateByPkSql;
    }

    public String getUpdateByPkVersionSql() {
        return updateByPkVersionSql;
    }

    public String getDeleteSql() {
        return deleteSql;
    }

    public String getDeleteByPkSql() {
        return deleteByPkSql;
    }

    public String getDeleteByPkVersionSql() {
        return deleteByPkVersionSql;
    }

    public String getCountSql() {
        return countSql;
    }

    public String getCountViewSql() {
        return countViewSql;
    }

    public String getTestSql() {
        return testSql;
    }

}
