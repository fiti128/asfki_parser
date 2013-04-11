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
	
	private AsfkiHandler(Db2Writer writer, String rowTag) {
		super();
		this.writer = writer;
		this.rowTag = rowTag.intern();
	}
	public static AsfkiHandler getInstance(Db2Writer writer,String rowTag) {
		return new AsfkiHandler(writer,rowTag);
	}
		
	public void startElement(String namespaceURI,
			String localName,
			String qName, 
			Attributes atts)
		throws SAXException {
				if (qName == rowTag) {
				list.clear();
			}
		}

		public void endElement (String uri, String name, String qName)
		{
			if(qName == rowTag){
				
						try {
							writer.writeLine(list);
						} catch (Exception e) {
							e.printStackTrace();
						}
//						for (String string : list) {
//							System.out.print(string);
//						}
//					System.out.println();
					}
		}


		public void characters (char[] ch, int start, int length) 
		{
			string = new String(ch,start,length);
			list.add(string);
			string = "";
		}
}
