package sg.edu.nus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import sg.edu.nus.accesscontrol.AccCtrlLanguageLoader;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;

/**
 * 
 * @author dcsvht
 *
 */

public class HtmlDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2041625766044987125L;
	String htmlContent = null;
	Vector<String[]> allPrivileges = null;

	public HtmlDialog(String title, Vector<String[]> allPrivileges) {
		this.setModal(true);
		this.setTitle(title);
		this.setSize(350, 350);
		this.allPrivileges = allPrivileges;

		htmlContent = getHtml();

		JLabel content = new JLabel(htmlContent);
		content.setBackground(Color.WHITE);
		this.getContentPane().setBackground(Color.WHITE);
		this.getContentPane().add(content, BorderLayout.CENTER);

		init();

		this.setVisible(true);
		this.setResizable(true);
	}

	private String getHtml() {

		String html = "";
		String caption = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege");
		String label = "<html><font size = 5 >" + "<font color=\"#0000ff\">"
				+ caption + "</font><br>";

		html += label + "\n";

		html += "<table border=\"1\" width=\"100%\">" + "\n";

		String strPrivilege = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege_name");
		String strObject = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege_object");
		String strDesc = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege_type");

		String tr = "<tr>";
		tr += "<td><b><font color=\"#FF6600\">" + strPrivilege + "</b></td>";
		tr += "<td><b><font color=\"#FF6600\">" + strObject + "</b></td>";
		tr += "<td><b><font color=\"#FF6600\">" + strDesc + "</b></td>";
		tr += "</tr>";
		html += tr + "\n";

		for (int i = 0; i < allPrivileges.size(); i++) {
			String[] arr = allPrivileges.get(i);
			tr = "<tr>";
			tr += "<td>" + arr[0] + "</td>";
			String temp = new String(arr[1]);
			if (arr[2].equals("row level")) {
				temp = temp.replace("<", " &lt ");
			}
			tr += "<td>" + temp + "</td>";
			tr += "<td>" + arr[2] + "</td>";
			tr += "</tr>";
			html += "\t" + tr + "\n";
		}
		html += "</table>";
		return html;
	}

	void init() {
		JToolBar toolBar = new JToolBar();
		String imageLoc = AbstractMainFrame.SRC_PATH + "pdf" + ".png";
		JButton buttonPdf = new JButton();
		buttonPdf.setToolTipText("Print to pdf");

		try { // image found
			buttonPdf.setIcon(new ImageIcon(imageLoc));
		} catch (Exception e) { // no image found
		}
		toolBar.add(buttonPdf);

		// add word button
		imageLoc = AbstractMainFrame.SRC_PATH + "word" + ".png";
		/* create and initialize the button */
		JButton buttonWord = new JButton();
		buttonWord.setToolTipText("Print to word document");

		try { // image found
			buttonWord.setIcon(new ImageIcon(imageLoc));
		} catch (Exception e) { // no image found
		}
		toolBar.add(buttonWord);

		buttonWord.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// This is where a real application would save the file.
					String fileName = file.getAbsolutePath();
					if (!fileName.contains(".doc")) {
						fileName = fileName + ".doc";
					}
					writeToRTF(fileName);
				}
			}
		});

		buttonPdf.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// This is where a real application would save the file.
					String fileName = file.getAbsolutePath();
					if (!fileName.contains(".pdf")) {
						fileName = fileName + ".pdf";
					}
					writeToPdf(fileName);
				}

			}
		});
		// add tool bar

		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);

	}

	private void writeToRTF(String fileName) {

		Document document = new Document();
		try {

			RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			document.open();

			/* Create the font. The font name must exactly match the font name on the client system. */
			Paragraph para = null;

			String caption = AccCtrlLanguageLoader
					.getProperty("panel_schema_privilege.granted_privilege");

			RtfFont embossedFont = new RtfFont("Times New Roman", 12,
					RtfFont.STYLE_EMBOSSED);
			para = new Paragraph(caption, embossedFont);
			document.add(para);

			Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 12);
			PdfPTable table = null;
			float[] columnDefinitionSize = { 33.33F, 33.33F, 33.33F };
			float width = document.getPageSize().getWidth();

			table = new PdfPTable(columnDefinitionSize);
			table.setHorizontalAlignment(0);
			table.setTotalWidth(width - 72);
			table.setLockedWidth(true);

			String strPrivilege = AccCtrlLanguageLoader
					.getProperty("panel_schema_privilege.granted_privilege_name");
			String strObject = AccCtrlLanguageLoader
					.getProperty("panel_schema_privilege.granted_privilege_object");
			String strDesc = AccCtrlLanguageLoader
					.getProperty("panel_schema_privilege.granted_privilege_type");
			table.addCell(new Phrase(strPrivilege, embossedFont));
			table.addCell(new Phrase(strObject, embossedFont));
			table.addCell(new Phrase(strDesc, embossedFont));

			for (int i = 0; i < allPrivileges.size(); i++) {
				String[] arr = allPrivileges.get(i);
				table.addCell(new Phrase(arr[0], font8));
				table.addCell(new Phrase(arr[1], font8));
				table.addCell(new Phrase(arr[2], font8));
			}
			document.add(table);

		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		document.close();
	}

	private void writeToPdf(String fileName) {

		Document document = new Document();
		try {

			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();

			/* Create the font. The font name must exactly match the font name on the client system. */
			Paragraph para = null;

			para = new Paragraph("Granted Privileges");
			document.add(para);
			para = new Paragraph("   ");
			document.add(para);

			Font font8 = FontFactory.getFont(FontFactory.HELVETICA, 12);
			PdfPTable table = null;
			float[] columnDefinitionSize = { 33.33F, 33.33F, 33.33F };
			float width = document.getPageSize().getWidth();

			table = new PdfPTable(columnDefinitionSize);
			table.setHorizontalAlignment(0);
			table.setTotalWidth(width - 72);
			table.setLockedWidth(true);

			table.addCell(new Phrase("Privilege"));
			table.addCell(new Phrase("Object"));
			table.addCell(new Phrase("Description"));

			for (int i = 0; i < allPrivileges.size(); i++) {
				String[] arr = allPrivileges.get(i);
				table.addCell(new Phrase(arr[0], font8));
				table.addCell(new Phrase(arr[1], font8));
				table.addCell(new Phrase(arr[2], font8));
			}
			document.add(table);

		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		document.close();

		System.out.println("written: " + fileName);
	}

	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			dispose();
		}
	}

}
