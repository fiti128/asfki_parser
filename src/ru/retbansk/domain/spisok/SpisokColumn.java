package ru.retbansk.domain.spisok;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="col")
public class SpisokColumn {
	
	private String cor_time;
	private String format;
	private String body;
	
	public String getCor_time() {
		return cor_time;
	}
	@XmlAttribute(name="cor_time")
	public void setCor_time(String cor_time) {
		this.cor_time = cor_time;
	}
	public String getFormat() {
		return format;
	}
	@XmlAttribute(name="format")
	public void setFormat(String format) {
		this.format = format;
	}
	public String getBody() {
		return body;
	}
	@XmlValue
	public void setBody(String body) {
		this.body = body;
	}
	
	
	
}
