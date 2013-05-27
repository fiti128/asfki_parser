package rw.asfki.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import rw.asfki.Db2Writer;

public class AsfkiHandler extends DefaultHandler {
	
	private Db2Writer writer;
	private String string;
	private List<String> list = new ArrayList<String>();
	private String rowTag;
	private String colTag;
	
	private AsfkiHandler(Db2Writer writer, String rowTag,String colTag) {
		super();
		this.writer = writer;
		this.rowTag = rowTag.intern();
		this.colTag = colTag.intern();
	}
	public static AsfkiHandler getInstance(Db2Writer writer,String rowTag, String colTag) {
		return new AsfkiHandler(writer,rowTag,colTag);
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
								
								writer.writeLine(list);
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


		public void characters (char[] ch, int start, int length) 
		{
			string = new String(ch,start,length);
		}
}
