
/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.JAXB2Entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jaxb2 Root для всех ASFKI файлов, если захотим 
 * использовать SAX и JAXB2. Класс уже рабочий - файлы саксом читаются.
 * Сделано на будущее, если на внешние файлы дадут гарантию их соответствию 
 * xml стандартам.
 * 
 * @author ircm_yanusheusky
 * @since 15.01.2013
 */
@XmlRootElement(name="table")
public class XmlRoot {
	private List<Header> headerList;
	private List<Row> rowsList;
	public static List<Row> publicList = new ArrayList<Row>();
	
	public List<Header> getHeaderList() {
		return headerList;
	}
	@XmlElement(name="head")
	public void setHeaderList(List<Header> headerList) {
		this.headerList = headerList;
	}

	public List<Row> getRowsList() {
		return rowsList;
	}
	@XmlElement(name="row")
	public void setRowsList(List<Row> rowsList) {
		this.rowsList = rowsList;
	}
	
	public void setHeadListener(final HeadListener hl ){
		headerList = (hl == null) ? null: new ArrayList<Header>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2388867149989331681L;

			public boolean add(Header header) {
				hl.handleHeader(XmlRoot.this, header);
				return false;
			}
		};
		
	}
	public void setRowListener(final RowListener rl) {
        rowsList = (rl == null) ? null : new ArrayList<Row>() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -6064464190091234083L;

			public boolean add(Row o) {
                rl.handleRow(XmlRoot.this, o);
                return false;
            }
        };
    }
    public static interface RowListener {
        void handleRow(XmlRoot xmlRoot, Row row);

    }
    public static interface HeadListener {
    	void handleHeader(XmlRoot xmlRoot, Header header);
    	
    }
}
