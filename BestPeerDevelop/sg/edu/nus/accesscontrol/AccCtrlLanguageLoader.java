package sg.edu.nus.accesscontrol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.peer.LanguageLoader;

public abstract class AccCtrlLanguageLoader {

	/* system supported language */
	public static final int english = LanguageLoader.english;
	public static final int chinese = LanguageLoader.chinese;

	private static final String cnPropFile = "./sg/edu/nus/accesscontrol/gui/property_access_control_cn.ini";
	private static final String enPropFile = "./sg/edu/nus/accesscontrol/gui/property_access_control_en.ini";

	/* store language-specific strings */
	private static Properties keys = null;

	/**
	 * Initate a system property with the specified language, and
	 * system can get the property value with the corresponding 
	 * property key.
	 *
	 * @param lan the language option
	 */
	public static void newLanguageLoader(int lan) {
		try {
			/* load corresponding configuration file */
			FileInputStream fin = null;
			switch (lan) {
			case english:
				fin = new FileInputStream(enPropFile);
				break;

			case chinese:
				fin = new FileInputStream(cnPropFile);
				break;

			default:
				fin = new FileInputStream(enPropFile);
			}

			/* load items into memory */
			if (fin != null) {
				keys = new Properties();
				keys.load(fin);

				fin.close();
			}

			// load interface property
			GuiLoader.load();

		} catch (IOException e) {
			throw new RuntimeException(
					"Cannot find access control property file", e);
		}
	}

	/**
	 * Searches for the property with the specified key in this property list. 
	 * The method returns null if the property is not found.
	 * 
	 * @param key the property key 
	 * @return returns the value in this property list with the specified key value
	 */
	public static String getProperty(String key) {
		if (keys == null)
			throw new RuntimeException(
					"Access Control Language loader is not initiated");

		String tmp = keys.getProperty(key);

		if (tmp == null) {
			tmp = "No property for this";
		}

		try {
			return new String(tmp.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			return tmp;
		}
	}

	public static Properties getProperties() {
		return keys;
	}

	public static void main(String[] args) {
		
		System.out.println("Access control laguager loader!");

		AccCtrlLanguageLoader.newLanguageLoader(AccCtrlLanguageLoader.english);

		Properties prop = AccCtrlLanguageLoader.getProperties();
		prop.list(System.out);

		System.out.println("End access control laguager loader!");
	}

}
