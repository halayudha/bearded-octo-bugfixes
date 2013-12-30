/*
 * @(#) LogEvent.java 1.0 2006-6-27
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.logging;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import sg.edu.nus.gui.AbstractMainFrame;

/**
 * A wrapper used for encapsulating event.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-6-23
 */
public final class LogEvent {

	// "Type", "Date", "Time", "Description"
	public static final int EVENT_ITEMS = 5;

	private static final String OFFSET = " ";

	private JLabel type; // log message type
	private String date; // date event occurs
	private String time; // time event occurs
	protected String description; // event description
	private String host; // message host (from /to)

	/**
	 * Constructor.
	 * 
	 * @param type the event type
	 * @param description the event description
	 */
	public LogEvent(String type, String description, String host) {
		this.makeType(type);
		this.formatTime();
		this.description = OFFSET + description;
		this.host = OFFSET + host;
	}

	private void makeType(String t) {
		if (LogEventType.checkValue(t)) {
			String name = LogEventType.getValue(t);
			String imageLoc = AbstractMainFrame.SRC_PATH + name + ".png";
			type = new JLabel(OFFSET + name);
			type.setIcon(new ImageIcon(imageLoc));
			type.setIconTextGap(2);
			type.setText(OFFSET + name);
			type.setBackground(Color.WHITE);
			type.setOpaque(true);
		}
	}

	private void formatTime() {
		Date d = new Date();

		// get MM/DD/YYYY string
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		formatter.setTimeZone(TimeZone.getDefault());
		date = OFFSET + formatter.format(d);

		// get HH:mm:ss a string
		formatter = new SimpleDateFormat("HH:mm:ss a", Locale.US);
		formatter.setTimeZone(TimeZone.getDefault());
		time = OFFSET + formatter.format(d);
	}

	/**
	 * Returns an array that contains all elements of <code>LogEvent</code>.
	 * 
	 * @return returns an array that contains all elements of <code>LogEvent</code>
	 */
	public Object[] toArray() {
		Object[] obj = new Object[EVENT_ITEMS];

		obj[0] = type;
		obj[1] = date;
		obj[2] = time;
		obj[3] = description;
		obj[4] = host;

		return obj;
	}

}
