package rw.asfki.error;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
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

import org.apache.log4j.Logger;

import rw.asfki.domain.Db2Table;
import rw.asfki.util.UsefulMethods;



public class ErrorManager {
	private static String ZIP_NAME = "errors.zip";
	protected Logger logger = Logger.getLogger("Service");
	private File errorDir;
	private List<Db2Table> localTablesList;
	private List<Db2Table> errorTablesList;
	
	public ErrorManager(File errorDir, List<Db2Table> localTablesList) {
		this.localTablesList = localTablesList;
		this.errorDir = errorDir;
		if (!errorDir.isDirectory()) {
			errorDir.mkdir();
		}

	}
	public void addErrorFile(File errorFile) throws IOException {
		if (errorTablesList == null) {
			errorTablesList = new ArrayList<Db2Table>();
		}
		File storedErrorFile = new File(errorDir,errorFile.getName());
		UsefulMethods.copyFile(errorFile, storedErrorFile, true);
		String fullName = errorFile.getName();
		String name = fullName.substring(0, fullName.length()-4);
		logger.warn(name);
		for (int i = 0; i < localTablesList.size(); i++) {
			if (localTablesList.get(i).getName().equals(name)) {
				Db2Table table = localTablesList.remove(i);
				logger.warn(table);
				errorTablesList.add(table);
				break;
			}
		}
		
		
	}

	public void sendToMail(String email) throws IOException, AddressException, MessagingException {
		// Archive error folder
		File zipFile = new File(ZIP_NAME);
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
		
		
		Properties props = new Properties();
		props.setProperty("host", "10.200.2.5");
		props.setProperty("emailFrom", "ircm_natalia@mnsk.rw");
		props.setProperty("emailTo", email);
		// Sending email
		String host = "10.200.2.5";
		String emailFrom = "ircm_natalia@mnsk.rw";
		
		Properties sysProps = System.getProperties();
		sysProps.put("mail.smtp.host", host);
		System.out.println(sysProps.getProperty("mail.smtp.port"));
		Session session = Session.getInstance(sysProps,null);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(emailFrom));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email,false));
		msg.setSentDate(new Date());
		StringBuilder additionalInfo = new StringBuilder();
		additionalInfo.append("\n--------------------------------\n")
		.append("������ ����������� ������: \n");
		for (Db2Table table : localTablesList) {
			additionalInfo.append(table).append("\n");
		}
		if (errorTablesList != null) {
			additionalInfo.append("� " + errorTablesList.size() + " �������� �������� ��������: \n");
			for (Db2Table table : errorTablesList) {
				additionalInfo.append(table).append("\n");
			}
		}
		
		StringBuilder sb = new StringBuilder();
		if (files.length > 0) {
			msg.setSubject("����� �� ������ ����� �������. ���� ������.");
			MimeBodyPart mbp1 = new MimeBodyPart();
			sb.append("���������� �������: ").append(files.length)
				.append(" ������.\n����������� � �������������� �����\n\n�������\n")
				.append(additionalInfo); 
			mbp1.setText(sb.toString());
			MimeBodyPart mbp2 = new MimeBodyPart();
			mbp2.attachFile(zipFile);
			MimeMultipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);
			msg.setContent(mp);
			
		}
		else {
			msg.setSubject("����� �� ������ ����� �������. ������ �� ����.");
			sb.append("������ ������� ������ �������. ������ �������� �� ����\n\n�������")
				.append(additionalInfo);
			msg.setText(sb.toString());
		}
		
		Transport.send(msg);
		zipFile.delete();
		
	}
	public List<Db2Table> getErrorTablesList() {
		return errorTablesList;
	}


	
	
}
