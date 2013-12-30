package sg.edu.nus.util;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import java.io.*;

/**
 * Utility for data encryption function
 * 
 * @author VHTam
 * @version 1.0 2010-03-18
 */

public class EncryptionUtils {
	// private static final String KEY_STRING = "193-155-248-97-234-56-100-241";
	//private static final String KEY_STRING = "236-117-181-248-76-176-76-84";

	public static String generateKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("DES");
			SecretKey desKey = keygen.generateKey();
			byte[] bytes = desKey.getEncoded();
			return getString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Key getKey(String keyString) {
		try {
			byte[] bytes = getBytes(keyString);
			DESKeySpec pass = new DESKeySpec(bytes);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey s = skf.generateSecret(pass);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			sb.append((int) (0x00FF & b));
			if (i + 1 < bytes.length) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	private static byte[] getBytes(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StringTokenizer st = new StringTokenizer(str, "-", false);
		while (st.hasMoreTokens()) {
			int i = Integer.parseInt(st.nextToken());
			bos.write((byte) i);
		}
		return bos.toByteArray();
	}

	public static void main(String[] args) {
		args = new String[] { "generate-key" };

		if (args.length < 1) {
			System.out.println("Usage: EncryptionUtils <command> <args>");
			System.out.println("\t<command>: encrypt, decrypt, generate-key");
			System.exit(0);
		}
		String command = args[0];
		if (command.equalsIgnoreCase("generate-key")) {
			System.out.println("New key: " + EncryptionUtils.generateKey());
		} 
		else {
			System.out.println("Usage: EncryptionUtils <command> <args>");
			System.out.println("\t<command>: encrypt, decrypt, generate-key");
			System.exit(0);

		}
	}

	public static void showProviders() {
		try {
			Provider[] providers = Security.getProviders();
			for (int i = 0; i < providers.length; i++) {
				System.out.println("Provider: " + providers[i].getName() + ", "
						+ providers[i].getInfo());
				for (Iterator itr = providers[i].keySet().iterator(); itr
						.hasNext();) {
					String key = (String) itr.next();
					String value = (String) providers[i].get(key);
					System.out.println("\t" + key + " = " + value);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
