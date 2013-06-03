package rw.asfki.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class PipeHandler extends DefaultHandler {
	
	private String tableName;
	private String string;
	private List<String> list = new ArrayList<String>();
	private String rowTag;
	private String colTag;
	
	private PipeHandler(String tableName, String rowTag,String colTag) {
		super();

		this.rowTag = rowTag.intern();
		this.colTag = colTag.intern();
		this.tableName = tableName;
	}
	public static PipeHandler getInstance(String tableName,String rowTag, String colTag) {
		return new PipeHandler(tableName,rowTag,colTag);
	}
		
	public void startElement(String namespaceURI,
			String localName,
			String qName, 
			Attributes atts)
		throws SAXException {
				if (qName == rowTag) {
				list.clear();
			}
				if (qName == colTag) {
					string = "";
					
				}
		}

		public void endElement (String uri, String name, String qName)
		{
			if(qName == rowTag){
				
						try {
							if (list.size() > 0) {
								
								writeLine(list);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						

					}
			if(qName == colTag) {
//				System.out.print(string);
				list.add(string.trim());
			}
		}


		private void writeLine(List<String> row) {
			
			
		}
		public void characters (char[] ch, int start, int length) 
		{
			string = new String(ch,start,length);
		}
}
