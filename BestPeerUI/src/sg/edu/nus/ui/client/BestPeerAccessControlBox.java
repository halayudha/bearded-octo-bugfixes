/**
 * Created on Apr 24, 2009
 */
package sg.edu.nus.ui.client;

import sg.edu.nus.ui.client.BestPeerDBManagerBox.I18nConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * @author David Jiang
 *
 */
public class BestPeerAccessControlBox extends Composite implements ClickHandler {
	public static interface I18nConstants extends Constants {
	}
	
	private FlexTable table = new FlexTable();
	private int selectedRow = -1;
	
	public BestPeerAccessControlBox() {
		// Setup the table
		table.setCellSpacing(0);
		table.setCellPadding(0);
		table.setWidth("100%");
		
		// Hook up events
		table.addClickHandler(this);
		
		initWidget(table);
		setStyleName("db-ManagerBox");
		initTable();
	}
	
	/**
	 * Initializes the functions table
	 */
	private void initTable() {
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Object sender = event.getSource();
		if(sender == table) {
			Cell cell = table.getCellForEvent(event);
			if( cell != null )
				selectRow(cell.getRowIndex());
		}
	}
	
	private void selectRow(int row) {
		styleRow(selectedRow, false);
		styleRow(row, true);
		selectedRow = row;
		// TODO: add widget to the right panel
	}
	
	private void styleRow(int row, boolean selected) {
		if(row == -1)
			return;
		if(selected) 
			table.getRowFormatter().addStyleName(row, "db-SelectedFunc");
		else
			table.getRowFormatter().removeStyleName(row, "db-SelectedFunc");
	}
}
