package sg.edu.nus.gui.test.peer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import sg.edu.nus.peer.LanguageLoader;

public class DBExplorerMappingPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 15463489722346L;

	Vector<mappingPointPair> mappingPointPairSet = null;

	public DBExplorerMappingPanel() {
		super();
		mappingPointPairSet = new Vector<mappingPointPair>();

		initPane();
	}

	private void initPane() {
		this.setPreferredSize(new Dimension(150, 600));

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.mappingPanel")));
	}

	public void delSinglePoint(String sourceSchemaName,
			String targetSchemaName, String nodeType) {
		for (int i = 0; i < mappingPointPairSet.size(); i++) {
			mappingPointPair pointPair = mappingPointPairSet.get(i);

			if (pointPair.sourceSchemaName.equals(sourceSchemaName)
					&& (pointPair.targetSchemaName.equals(targetSchemaName))) {
				if (nodeType.equals("LOCAL")) {
					pointPair.localSchemaTreeLocationX = -1;
					pointPair.localSchemaTreeLocationY = -1;
				} else {
					pointPair.globalSchemaTreeLocationX = -1;
					pointPair.globalSchemaTreeLocationY = -1;
				}

				return;
			}
		}
	}

	public void addSinglePoint(String sourceSchemaName,
			String targetSchemaName, String nodeType, int x_location,
			int y_location) {
		Point absolutePosition = this.getLocationOnScreen();

		for (int i = 0; i < mappingPointPairSet.size(); i++) {
			mappingPointPair pointPair = mappingPointPairSet.get(i);

			if (pointPair.sourceSchemaName.equals(sourceSchemaName)
					&& (pointPair.targetSchemaName.equals(targetSchemaName))) {
				if (nodeType.equals("LOCAL")) {
					pointPair.localSchemaTreeLocationX = 0;
					pointPair.localSchemaTreeLocationY = y_location
							- absolutePosition.y;
				} else {
					pointPair.globalSchemaTreeLocationX = this.getSize().width;
					pointPair.globalSchemaTreeLocationY = y_location
							- absolutePosition.y;
				}

				return;
			}
		}

		mappingPointPair pair = new mappingPointPair();
		pair.sourceSchemaName = sourceSchemaName;
		pair.targetSchemaName = targetSchemaName;

		if (nodeType.equals("LOCAL")) {
			pair.localSchemaTreeLocationX = 0;
			pair.localSchemaTreeLocationY = y_location - absolutePosition.y;
		} else {
			pair.globalSchemaTreeLocationX = this.getSize().width;
			pair.globalSchemaTreeLocationY = y_location - absolutePosition.y;
		}

		mappingPointPairSet.add(pair);

		return;
	}

	private class mappingPointPair {
		public String sourceSchemaName = null;
		public String targetSchemaName = null;

		public int localSchemaTreeLocationX = -1;
		public int localSchemaTreeLocationY = -1;

		public int globalSchemaTreeLocationX = -1;
		public int globalSchemaTreeLocationY = -1;

		public String toString() {
			return sourceSchemaName + ", " + targetSchemaName + "["
					+ localSchemaTreeLocationX + ", "
					+ localSchemaTreeLocationY + "], ["
					+ globalSchemaTreeLocationX + ", "
					+ globalSchemaTreeLocationY + "]";
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		for (int i = 0; i < mappingPointPairSet.size(); i++) {
			mappingPointPair pointPair = mappingPointPairSet.get(i);

			if ((pointPair.localSchemaTreeLocationX != -1)
					&& (pointPair.localSchemaTreeLocationY != -1)
					&& (pointPair.globalSchemaTreeLocationX != -1)
					&& (pointPair.globalSchemaTreeLocationY != -1)) {
				g2.setPaint(Color.GREEN);
				g2.setStroke(new BasicStroke(2));

				g2.drawLine(pointPair.localSchemaTreeLocationX,
						pointPair.localSchemaTreeLocationY,
						pointPair.globalSchemaTreeLocationX,
						pointPair.globalSchemaTreeLocationY);
			}

		}
	}
}
