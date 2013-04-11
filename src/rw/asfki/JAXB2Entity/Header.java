/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Заголовок для Jaxb2
 * 
 * @author Yanusheusky S.
 * @since 15.01.2013
 */
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
