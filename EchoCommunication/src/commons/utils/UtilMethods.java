package commons.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import commons.metaData.ServerInfo;

public class UtilMethods {
	private static MessageDigest md;
	private static Logger logger = Logger.getRootLogger();
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String[] getTokens(String source) {
		String delims = "[ ]+";
		String[] tokens = source.split(delims);
		return tokens;
	}

	public static String getHashMD5(String data) {
		md.reset();
		md.update(data.getBytes());
		byte byteData[] = md.digest();
		// convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static ServerInfo getServerInfoByName(String name,
			ArrayList<ServerInfo> nodes) {
		for (ServerInfo item : nodes) {
			if (item.getServerName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public static ServerInfo getServerInfoByKey(String key,ArrayList<ServerInfo> nodes) {
		if (nodes != null) {
			for (ServerInfo item : nodes) {
				if (item.isInsideKeyRange(key)) {
					return item;
				}
			}
		}
		return null;
	}

	public static void initConsoleLogging() {
		ConsoleAppender ca = new ConsoleAppender();
		ca.setWriter(new OutputStreamWriter(System.out));
		ca.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
		logger.addAppender(ca);
	}

	public static void initLogging() {
		String localDir = System.getProperty("user.dir");
		String log4JPropertyFile = localDir + File.separator + "config"
				+ File.separator + "log4j.properties";
		Properties p = new Properties();
		FileInputStream inStream = null;
		try {
			File file = new File(log4JPropertyFile);
			if (file.exists()) {
				inStream = new FileInputStream(file);
				p.load(inStream);
			}
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			System.out.println("Logging configuration failed to load."
					+ e.getMessage());
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					System.out.println("Stream to logging configuration"
							+ " file failed when was closed." + e.getMessage());
				}
			}
		}
	}

	public static ServerInfo getServerInfoByHashFrom(String hashFrom,
			ArrayList<ServerInfo> nodes) {
		for (ServerInfo item : nodes) {
			if (item.getHashFrom().equals(hashFrom)) {
				return item;
			}
		}
		return null;
	}
}
