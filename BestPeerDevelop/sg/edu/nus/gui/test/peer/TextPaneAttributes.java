package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class TextPaneAttributes extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2377010785151181150L;

	public TextPaneAttributes() {
		JTextPane textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();

		// Set alignment to be centered for all paragraphs

		MutableAttributeSet standard = new SimpleAttributeSet();
		StyleConstants.setAlignment(standard, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, 0, standard, true);

		// Define a keyword attribute

		MutableAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.red);
		StyleConstants.setItalic(keyWord, true);

		// Add initial text

		textPane.setText("one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\n");

		// Highlight some keywords

		doc.setCharacterAttributes(0, 3, keyWord, false);
		doc.setCharacterAttributes(19, 4, keyWord, false);

		// Add some text

		try {
			doc.insertString(0, "Start of text\n", null);
			doc.insertString(doc.getLength(), "End of text\n", keyWord);
		} catch (Exception e) {
		}

		// Add text pane to frame

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		getContentPane().add(scrollPane);

		// Add a bold button
		JButton button = new JButton("bold");
		getContentPane().add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new StyledEditorKit.BoldAction().actionPerformed(null);
			}
		});

	}

	public static void main(String[] args) {
		TextPaneAttributes frame = new TextPaneAttributes();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
