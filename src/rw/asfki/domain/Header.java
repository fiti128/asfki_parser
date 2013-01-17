package rw.asfki.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "head")
public class Header {
	private List<HeadColumn> headColumnlist;

	public List<HeadColumn> getHeadColumnlist() {
		return headColumnlist;
	}
	@XmlElement(name = "col")
	public void setHeadColumnlist(List<HeadColumn> headColumnlist) {
		this.headColumnlist = headColumnlist;
	}
	
	
}
