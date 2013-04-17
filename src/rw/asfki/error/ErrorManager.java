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
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class ErrorManager {
	Properties props;
	File errorDir;
	
	public ErrorManager(File errorDir) {
		this.errorDir = errorDir;
		if (!errorDir.isDirectory()) {
			errorDir.mkdir();
		}
		props = new Properties();
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
	public void sendToMail(String email) throws IOException, AddressException, MessagingException {
		// Archive error folder
		File zipFile = new File("errors.zip");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile, false));
		File[] files = errorDir.listFiles();
		ZipOutputStream zos = new ZipOutputStream(bos);
		for (File file : files) {
			zos.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream fileInputStream = new FileInputStream(file);
			int length;
			byte[] BUFFER = new byte[1024];
			while((length = fileInputStream.read(BUFFER)) > 0) {
				zos.write(BUFFER, 0, length);
				
			}
			zos.closeEntry();
			
		}
		zos.flush();
		zos.close();
		
		
		
		props.setProperty("host", "10.200.2.5");
		props.setProperty("emailFrom", "natalia@longplesure.da");
		props.setProperty("emailTo", email);
		// Sending email
		String host = "10.200.2.5";
		String emailFrom = "natali@longplesure.da";
		
		Properties sysProps = System.getProperties();
		sysProps.put("mail.smtp.host", host);
		System.out.println(sysProps.getProperty("mail.smtp.port"));
		Session session = Session.getInstance(sysProps,null);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(emailFrom));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email,false));
		msg.setSentDate(new Date());
		if (files.length > 0) {
			msg.setSubject("Отчет по работе АСФКИ парсера. Есть ошибки.");
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText("Количество проблем: " + files.length + " таблиц.\nПодробности в присоединенном файле\n\nНаталья");
			MimeBodyPart mbp2 = new MimeBodyPart();
			mbp2.attachFile(zipFile);
			MimeMultipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);
			msg.setContent(mp);
			
		}
		else {
			msg.setSubject("Отчет по работе АСФКИ парсера. Ошибок не было.");
			msg.setText("Работа парсера прошла успешно. Ошибок выявлено не было\n\nНаталья");
		}
		
		Transport.send(msg);
		zipFile.delete();
		
	}
	
	
}
