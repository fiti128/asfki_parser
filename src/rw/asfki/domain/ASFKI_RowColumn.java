/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.domain;

import java.util.Map;
/**
 * Объект класса обозначает представление в файлах ASFKI
 * данных одной строки одного столбца с аттрибутами по данной записи.
 * 
 * @author Yanusheusky S.
 * @since 27.02.2013
 */
public class ASFKI_RowColumn {
	private Map<String, String> attributes;
	private String body;
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
