package ru.retbansk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ru.retbansk.domain.*;

public class Main {
	public static String OH_NIFIGA_ZH_TAKOE_BOLSHOE_SLOVO_AZH_PRJAM_NEMOGU = "¬Œ“ “¿  ¬Œ“";
	public static String OH_NIFIGA_ZH_TAKOE_BOLSHOE_SLOVO_AZH_PRJAM_KLASS = "¬Œ“ “¿  “Œ∆≈ ¡€¬¿≈“";
	private static final String INPUT_ZIP_FILE = "H_SISTEMA.ZIP";
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
		
		xmlRoot = new XmlRoot();
		xmlRoot.setHeader(header);
		
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
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws JAXBException, FileNotFoundException {
		prepare();
		JAXBContext context = JAXBContext.newInstance(XmlRoot.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//	    m.setProperty(Marshaller.JAXB_ENCODING, Marshaller.);

	    // Write to System.out
	    m.marshal(xmlRoot, System.out);
		
	    m.marshal(xmlRoot, new File("lol.xml"));
	    
	    Unmarshaller um = context.createUnmarshaller();
	    
	    XmlRoot xmlRoot2 = (XmlRoot) um.unmarshal(new FileReader("H_SISTEMA.xml"));
	    System.out.println(xmlRoot2.getRowsList().get(6).getRowColumnList().get(5).getBody());
	    
	}

}
