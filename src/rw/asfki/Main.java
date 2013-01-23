package rw.asfki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import rw.asfki.JAXB2Entity.*;
import rw.asfki.JAXB2Entity.spisok.Root;
import rw.asfki.JAXB2Entity.spisok.SpisokColumn;

public class Main {
	public static String OH_NIFIGA_ZH_TAKOE_BOLSHOE_SLOVO_AZH_PRJAM_NEMOGU = "¬Œ“ “¿  ¬Œ“";
	public static String OH_NIFIGA_ZH_TAKOE_BOLSHOE_SLOVO_AZH_PRJAM_KLASS = "¬Œ“ “¿  “Œ∆≈ ¡€¬¿≈“";
	private static final String INPUT_ZIP_FILE = "H_SISTEMA.ZIP";
	public static Set<Row> rowSet = new HashSet<Row>();
	public static long counter = 0;
	static XmlRoot xmlRoot;
	static HeadColumn hc1;
	static HeadColumn hc2;
	static HeadColumn hc3;
	static Header header;
	static List<HeadColumn> headColumnList;
	static RowColumn rc1;
	static RowColumn rc2;
	static RowColumn rc3;
	static List<RowColumn> rowColumnList;
	static Row row1;
	static Row row2;
	static Row row3;
	static List<Row> rowList;
	
	
	public  static void prepare()   {
		hc1 = new HeadColumn();
		hc1.setComment("Id ÚËÔ‡");
		hc1.setNum(1);
		hc1.setType("INTEGER");
		hc1.setBody("GENA_TIP_ID");
		hc2 = new HeadColumn();
		hc2.setComment("“ËÔ ‚‡„ÓÌ‡");
		hc2.setNum(2);
		hc2.setType("SMALLINT");
		hc2.setBody("KOD");
		hc3 = new HeadColumn();
		hc3.setComment(" Ó‰ ÒÚÓÍË");
		hc3.setNum(3);
		hc3.setType("SMALLINT");
		hc3.setBody("LINE");
		headColumnList = new ArrayList<HeadColumn>();
		headColumnList.add(hc1);
		headColumnList.add(hc2);
		headColumnList.add(hc3);
		System.out.println(headColumnList);
		
		header = new Header();
		header.setHeadColumnlist(headColumnList);
		List<Header> headerList = new ArrayList<Header>();
		headerList.add(header);
		
		xmlRoot = new XmlRoot();
		xmlRoot.setHeaderList(headerList);
		
		rc1 = new RowColumn();
		rc2 = new RowColumn();
		rc3 = new RowColumn();
		rc1.setBody("some body1");
		rc1.setRowColumnNum(1);
		rc2.setBody("some body2");
		rc2.setRowColumnNum(2);
		rc3.setBody("some body3");
		rc3.setRowColumnNum(3);
		rowColumnList = new ArrayList<RowColumn>();
		rowColumnList.add(rc1); rowColumnList.add(rc2); rowColumnList.add(rc3);
		
		row1 = new Row();
		row2 = new Row();
		row3 = new Row();
		row1.setRowColumnList(rowColumnList);
		row1.setRowNum(1);
		row2.setRowColumnList(rowColumnList);
		row2.setRowNum(2);
		row3.setRowColumnList(rowColumnList);
		row3.setRowNum(3);
		rowList = new ArrayList<Row>();
		rowList.add(row1);
		rowList.add(row2);
		rowList.add(row3);
		
		xmlRoot.setRowsList(rowList);
		
		
	}
	/**
	 * @param args
	 * @throws JAXBException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws JAXBException, SAXException, ParserConfigurationException, IOException {
		prepare();
		JAXBContext context = JAXBContext.newInstance(XmlRoot.class);
		Unmarshaller um = context.createUnmarshaller();
//	    Marshaller m = context.createMarshaller();
//	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
////	    m.setProperty(Marshaller.JAXB_ENCODING, Marshaller.);
//
//	    // Write to System.out
//	    m.marshal(xmlRoot, System.out);
//		
//	    m.marshal(xmlRoot, new File("lol.xml"));
	    
	    List<Row> list = new ArrayList<Row>();
	    
	    final XmlRoot.RowListener xmlRootListener = new XmlRoot.RowListener() {
			
			@Override
			public void handleRow(XmlRoot xmlRoot, Row row) {
//				System.out.println(row.getRowColumnList().get(26).getBody());
				
//				rowSet.add(row);
				if (XmlRoot.publicList.size() >= 10000)  XmlRoot.publicList.clear();
					XmlRoot.publicList.add(row);
				counter++;
				
				
			}


			
		};
		final XmlRoot.HeadListener xmlHeadListener = new XmlRoot.HeadListener() {
			
			@Override
			public void handleHeader(XmlRoot xmlRoot, Header header) {
				System.out.println("In handleHeader");
				System.out.println(header.getHeadColumnlist().size());
				for (HeadColumn hc : header.getHeadColumnlist()) {
					System.out.print(hc.getNum()+ " ");
					System.out.print(hc.getComment() + " ");
					System.out.println(hc.getBody());
				}
				
			}
		};
		um.setListener(new Unmarshaller.Listener() {
			public void beforeUnmarshal(Object target, Object parent) {
				if (target instanceof XmlRoot) {
					 ((XmlRoot) target).setRowListener(xmlRootListener);
					 ((XmlRoot) target).setHeadListener(xmlHeadListener);
					 
				}
			}
			public void afterUnmarshal(Object target, Object parent) {
				if (target instanceof XmlRoot) {
					 ((XmlRoot) target).setRowListener(null);
					 ((XmlRoot) target).setHeadListener(null);
				}
			}
		});
		
        // create a new XML parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        
        reader.setContentHandler(um.getUnmarshallerHandler());


        reader.parse(new File("lol.xml").toURI().toString());
		
        System.out.println(counter);
        
        URL website = new URL("http://ircm-srv.mnsk.rw/ASFKI_XML/spisok.xml");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("d_spisok.xml");
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        
        context = JAXBContext.newInstance(Root.class);
        um = context.createUnmarshaller();
        Root root = (Root)um.unmarshal(new File("d_spisok.xml")); 
        
        for (SpisokColumn column : root.getRow().getColumn()) {
			System.out.println(column.getBody());
		}
        
	}

}
