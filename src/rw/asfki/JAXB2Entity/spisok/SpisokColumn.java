/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity.spisok;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * Jaxb2 Колонка у списка
 * @author Yanusheusky S.
 * @since 27.02.2013
 */
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
