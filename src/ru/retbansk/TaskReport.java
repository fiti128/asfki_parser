/*
 * Copyright 2012 the original author.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.retbansk;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Sub report of the Day Report.
 * Date have pretty format
 * 
 * @author Siarhei Yanusheuski
 * @since 25.10.2012
 * @see ru.retbansk.mail.domain.DayReport
 * @see ru.retbansk.mail.domain.TaskReport#getDate()
 */
@XmlRootElement(name = "Report")
public class TaskReport {
	private Date date;
	private String workDescription;
	private String status;
	private int elapsedTime;
	/**
	 * Using adapter for pretty look
	 * @see ru.retbansk.utils.marshaller.NiceDateAdapter
	 * @return Date
	 */

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getWorkDescription() {
		return workDescription;
	}

	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

}
