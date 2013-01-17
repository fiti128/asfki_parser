package rw.asfki.domain.spisok;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="table")
public class Root {
	
	private SpisokRow row;

	public SpisokRow getRow() {
		return row;
	}
	@XmlElement(name="row")
	public void setRow(SpisokRow row) {
		this.row = row;
	}
	
}
