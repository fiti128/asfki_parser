package rw.asfki.domain.spisok;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="row")
public class SpisokRow {
	
	private List<SpisokColumn> column;

	public List<SpisokColumn> getColumn() {
		return column;
	}
	@XmlElement(name="col")
	public void setColumn(List<SpisokColumn> column) {
		this.column = column;
	}
	
}
