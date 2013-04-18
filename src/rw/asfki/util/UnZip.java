/*
 *  Copyright belongs to Belarusian Railways. 
 *  Copying for commercial purposes is only allowed if the copyright owner's consent is obtained,
 *  or a copyright fee is paid, or it is made under licence.
 *  In order to obtain license call +375-17-2253017
 */
package rw.asfki.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
 /**
  *  Класс отвечающий за работу с архивами. 
  *  <p> На данный момент реализованно только разархивирование.
  * @author Yanusheusky S.
  * @since 21.02.2013
  *
  */
public class UnZip
{
	Logger logger = Logger.getLogger("service");
    List<String> fileList;
 

    /**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     * @throws IOException 
     */
    public void unZipIt(String zipFile, String outputFolder) throws IOException{
     int BUFFER = 2048;
     byte[] buffer = new byte[BUFFER];
 
     try{
 
    	//create output directory is not exists
    	File folder = new File(outputFolder);
    	if(!folder.exists()){
    		folder.mkdir();
    	}
 
    	//get the zip file content
    	ZipInputStream zis = 
    		new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile),BUFFER));
    	//get the zipped file list entry
    	ZipEntry ze;
 
    	while((ze=zis.getNextEntry())!=null){
    	
    	   String fileName = ze.getName();
           File newFile = new File(outputFolder + File.separator + fileName);
 
            
            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();
 
            FileOutputStream bos = new FileOutputStream(newFile);             
 
            int len;
            while ((len = zis.read(buffer,0,BUFFER)) != -1) {
       		bos.write(buffer, 0, len);
            }
            bos.flush();
            bos.close();   
            
    	}
 
        zis.closeEntry();
    	zis.close();
 
  
    }catch(IOException ex){
    logger.error("Failed unziping " + zipFile);
       throw ex; 
    }
   }    
}