package rw.asfki.error;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;



public class ErrorManager {
	File errorDir;
	public ErrorManager() {
		errorDir = new File("error");
		if (!errorDir.isDirectory()) {
			errorDir.mkdir();
		}
	}
	public void addErrorFile(File errorFile) throws IOException {
		File storedErrorFile = new File(errorDir,errorFile.getName());
		FileWriter fw = new FileWriter(storedErrorFile,false);
		BufferedReader br = new BufferedReader(new FileReader(errorFile));
		String str;
		while((str = br.readLine()) != null)
		{
			fw.write(str);
			fw.write("\n");
		}
		
		fw.flush();
		fw.close();
	}
	public void sendToMail() throws FileNotFoundException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("yeho.zip")))
		ZipOutputStream zos = new ZipOutputStream(bos);
		
		ZipEntry zipEntry = new ZipEntry("new");
		
	}
	
}
