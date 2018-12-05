/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.core.task;

import java.util.Date;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.Taskable;

/**
 * Test taskable method component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("test-taskablecomponent")
public class TestTaskableMethodComponent extends AbstractUnifyComponent {

	@Taskable(name = "computegalactic-task", description = "Galactic Calculator", parameters = { @Parameter("name"),
			@Parameter(name = "factor", type = Double.class) })
	public int computeGalactic(TaskMonitor taskMonitor, String name, Double factor) throws UnifyException {
		if (name != null) {
			return Double.valueOf((factor * 1000000)).intValue();
		}

		return 0;
	}

	@Taskable(name = "getone-task", description = "Get One Task", parameters = {})
	public int getOne(TaskMonitor taskMonitor) throws UnifyException {
		return 1;
	}

	@Taskable(name = "generatedailyrpt-task", description = "Generate Daily Report", parameters = {
			@Parameter(name = "reportBase", description = "Report Base", editor = "!ui-text"),
			@Parameter(name = "workingDt", description = "Working Date", editor = "!ui-date", type = Date.class) }, schedulable = true)
	public String generateDailyReport(TaskMonitor taskMonitor, String reportBase, Date workingDt)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append(reportBase).append('_').append(workingDt);
		return sb.toString();
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}
}
