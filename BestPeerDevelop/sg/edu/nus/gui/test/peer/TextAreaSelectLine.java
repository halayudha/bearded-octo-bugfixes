package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

public class TextAreaSelectLine extends JFrame implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2211716527826545148L;
	JTextArea textArea;
	Action selectLine;

	public TextAreaSelectLine() {

		textArea = new JTextArea("one two\nthree four", 10, 30);
		textArea.addMouseListener(this);

		JScrollPane scrollPane = new JScrollPane(textArea);
		getContentPane().add(scrollPane, BorderLayout.SOUTH);
		getContentPane().add(new JTextArea());

		selectLine = getAction(DefaultEditorKit.selectLineAction);

	}

	private Action getAction(String name) {
		Action action = null;
		Action[] actions = textArea.getActions();

		for (int i = 0; i < actions.length; i++) {
			if (name.equals(actions[i].getValue(Action.NAME).toString())) {
				action = actions[i];
				break;
			}
		}

		return action;
	}

	public void mouseClicked(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
			selectLine.actionPerformed(null);

		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public static void main(String[] args) {
		TextAreaSelectLine frame = new TextAreaSelectLine();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
