package rw.asfki.JAXB2Entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "col")
public class HeadColumn {
	private String comment;
	private int num;
	private String type;
	private String body;
	
	public String getComment() {
		return comment;
	}
	@XmlAttribute(name = "comment")
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getNum() {
		return num;
	}
	@XmlAttribute(name = "num")
	public void setNum(int num) {
		this.num = num;
	}
	public String getType() {
		return type;
	}
	@XmlAttribute(name = "type")
	public void setType(String type) {
		this.type = type;
	}
	public String getBody() {
		return body;
	}
	@XmlValue
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
