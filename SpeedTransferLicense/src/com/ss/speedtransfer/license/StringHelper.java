package com.ss.speedtransfer.license;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ss.speedtransfer.license.util.crypto.Base64;


/**
 * This class is a helper class for string operations
 */
public class StringHelper {

	/** Lead characters to denote a password string **/
	public static String PASSWORD_START = "#$&%0x";

	private StringHelper() {
		super();
	}

	static public String encodePassword(String password) throws IOException {
		String encoded = null;
		if (password != null && password.length() > 0) {
			encoded = Base64.coding(password, Base64.ENCRYPT);
			encoded = PASSWORD_START + encoded;
		}

		return encoded;
	}
}
