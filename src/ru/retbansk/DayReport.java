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

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Main domain class. 
 * <p> implements <code> Comparable </code> so it can be sorted
 * <p> <code>hashcode</code> and <code>equals</code> are overridden
 * @author Siarhei Yanusheuski
 * @since 25.10.2012
 * 
 */
@XmlRootElement(name="DayReport")
public class DayReport implements Comparable<DayReport> {
	
	private Calendar calendar;
	private String personId;
	private List<TaskReport> reportList;
	private String subject;
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.get(Calendar.DAY_OF_MONTH));
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.get(Calendar.MONTH));
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.get(Calendar.YEAR));
		result = prime * result
				+ ((personId == null) ? 0 : personId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DayReport other = (DayReport) obj;
		if (calendar == null) {
			if (other.calendar != null)
				return false;
		} else if ((calendar.get(Calendar.DAY_OF_MONTH) != other.calendar.get(Calendar.DAY_OF_MONTH)) ||
				   (calendar.get(Calendar.MONTH) != other.calendar.get(Calendar.MONTH)) ||
				   (calendar.get(Calendar.YEAR) != other.calendar.get(Calendar.YEAR)))
			return false;
		if (personId == null) {
			if (other.personId != null)
				return false;
		} else if (!personId.equals(other.personId))
			return false;
		return true;
	}
	public String getPersonId() {
		return personId;
	}
	@XmlAttribute
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public List<TaskReport> getReportList() {
		return reportList;
	}
	@XmlElement(name="Report")
	public void setReportList(List<TaskReport> reportList) {
		this.reportList = reportList;
	}
	
	public Calendar getCalendar() {
		return calendar;
	}
	@XmlTransient
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	@Override
	public int compareTo(DayReport o) {
		return calendar.compareTo(o.getCalendar());
	}

	public String getSubject() {
		return subject;
	}
	@XmlTransient
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
