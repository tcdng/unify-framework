/*
 * Copyright 2018-2024 The Code Department.
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

import java.math.BigDecimal;
import java.util.Date;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.core.annotation.Column;

/**
 * Staff entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "STAFF")
public class Staff extends AbstractTestVersionedTableEntity {

	@Column
	private String name;

	@Column
	private BigDecimal[] salaries;

	@Column(nullable = true)
	private int[] kpis;

	@Column
	private boolean[] cases;

	@Column(nullable = true)
	private Date[] employmentDates;

	public Staff(String name, BigDecimal[] salaries, int[] kpis, boolean[] cases, Date[] employmentDates) {
		this.name = name;
		this.salaries = salaries;
		this.kpis = kpis;
		this.cases = cases;
		this.employmentDates = employmentDates;
	}

	public Staff() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal[] getSalaries() {
		return salaries;
	}

	public void setSalaries(BigDecimal[] salaries) {
		this.salaries = salaries;
	}

	public int[] getKpis() {
		return kpis;
	}

	public void setKpis(int[] kpis) {
		this.kpis = kpis;
	}

	public boolean[] getCases() {
		return cases;
	}

	public void setCases(boolean[] cases) {
		this.cases = cases;
	}

	public Date[] getEmploymentDates() {
		return employmentDates;
	}

	public void setEmploymentDates(Date[] employmentDates) {
		this.employmentDates = employmentDates;
	}

}
