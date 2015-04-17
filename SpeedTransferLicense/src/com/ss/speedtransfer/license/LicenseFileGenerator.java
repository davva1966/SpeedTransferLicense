package com.ss.speedtransfer.license;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Properties;

public class LicenseFileGenerator {
	
	public static final String VERSION = "version";
	public static final String EXP_DATE = "expdate";
	public static final String EXP_MSG = "expmessage";
	public static final String LICENSE = "license";
	public static final String SELECT_ONLY = "selectonly";

	public static final int TYPE_STUDIO = 1;
	public static final int TYPE_BROWSER = 2;

	public static final String STUDIO = "studio";
	public static final String BROWSER = "browser";

	protected String licNumber;
	protected String directory;
	protected int type = TYPE_STUDIO;
	protected boolean selectOnly = false;
	protected Date expiryDate;
	protected String expiryMessage;

	public String getLicNumber() {
		return licNumber;
	}

	public void setLicNumber(String licNumber) {
		this.licNumber = licNumber;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isSelectOnly() {
		return selectOnly;
	}

	public void setSelectOnly(boolean selectOnly) {
		this.selectOnly = selectOnly;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getExpiryMessage() {
		return expiryMessage;
	}

	public void setExpiryMessage(String expiryMessage) {
		this.expiryMessage = expiryMessage;
	}

	// public static void main(String[] args) throws Exception {
	// try {
	// String file;
	// String type;
	// boolean selectOnly = false;
	//
	// // Browser
	// file = "C:\\Temp\\Browser_License";
	// type = "browser";
	// selectOnly = false;
	// createLicenseFile(file, type, selectOnly);
	//
	// // Browser (SELECT only)
	// file = "C:\\Temp\\Browser_License_SelectOnly";
	// type = "browser";
	// selectOnly = true;
	// createLicenseFile(file, type, selectOnly);
	//
	// // Studio
	// file = "C:\\Temp\\Studio_License";
	// type = "studio";
	// selectOnly = false;
	// createLicenseFile(file, type, selectOnly);
	//
	// // Studio (SELECT only)
	// file = "C:\\Temp\\Studio_License_SelectOnly";
	// type = "studio";
	// selectOnly = true;
	// createLicenseFile(file, type, selectOnly);
	//
	// } catch (Exception e) {
	// System.out.println(e);
	// }
	//
	// }

	// public static void createLicenseFile(String file, String type, boolean selectOnly) throws Exception {
	// try {
	//
	// Properties props = new Properties();
	// props.put(encrypt(LicenseManager.VERSION), encrypt(type));
	// if (selectOnly)
	// props.put(encrypt(LicenseManager.SELECT_ONLY), encrypt("true"));
	//
	// props.put(encrypt(LicenseManager.EXP_DATE),
	// encrypt(ExpiryDateGenerator.generate(2011, 9, 13, false)));
	// props.put(encrypt(LicenseManager.EXP_MSG),
	// encrypt("Your trial period has ended"));
	// props.put(encrypt(LicenseManager.EXP_DATE), encrypt("notused"));
	// props.put(encrypt(LicenseManager.LICENSE), encrypt("123456789"));
	//
	// FileOutputStream fos = new FileOutputStream(file);
	// ObjectOutputStream oos = new ObjectOutputStream(fos);
	// oos.writeObject(props);
	//
	// fos.close();
	//
	// } catch (Exception e) {
	// System.out.println(e);
	// }
	//
	// }

	public void generate() throws Exception {

		Properties props = new Properties();

		if (getLicNumber() == null || getLicNumber().trim().length() == 0)
			throw new Exception("License number must be entered");

		props.put(encrypt(LICENSE), encrypt(getLicNumber()));

		String t = STUDIO;
		if (getType() == TYPE_STUDIO)
			t = STUDIO;
		else
			t = BROWSER;
		props.put(encrypt(VERSION), encrypt(t));
		if (isSelectOnly())
			props.put(encrypt(SELECT_ONLY), encrypt("true"));

		if (getExpiryDate() != null) {
			props.put(encrypt(EXP_DATE), encrypt(ExpiryDateGenerator.generate(getExpiryDate(), false)));
			if (getExpiryMessage() != null && getExpiryMessage().trim().length() > 0)
				props.put(encrypt(EXP_MSG), encrypt(getExpiryMessage()));
			else
				props.put(encrypt(EXP_MSG), encrypt("Your trial period has ended"));
		} else {
			props.put(encrypt(EXP_DATE), encrypt("notused"));
		}

		String file = getDirectory() + "\\License";
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(props);

		fos.close();

	}

	protected static String encrypt(String text) throws IOException {
		return StringHelper.encodePassword(text);

	}

}
