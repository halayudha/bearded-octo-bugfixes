/**
 * Created on Apr 23, 2009
 */
package sg.edu.nus.ui.client;

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
public class BestPeerDBManagerBox extends Composite implements ClickHandler {
	public static interface I18nConstants extends Constants {
		String dbConfigure();
		String dbSchemaMapping();
		String dbExportTitle();
	}
	
	private I18nConstants content;
	private FlexTable table = new FlexTable();
	private int selectedRow = -1;
	
	public BestPeerDBManagerBox(BestPeerUIConstants appContent) {
		content = appContent;
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
		table.setText(0, 0, content.dbConfigure());
		table.setText(1, 0, content.dbSchemaMapping());
		table.setText(2, 0, content.dbExportTitle());
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Object sender = event.getSource();
		if(sender == table) {
			Cell cell = table.getCellForEvent(event);
			if( cell != null ){
				selectRow(cell.getRowIndex());
				switch(cell.getRowIndex()) {
				case 0:
					//dbConfigureWidget = new DatabaseConfigureWidget();
					//BestPeerDesktop.get().showWidget(dbConfigureWidget, dbConfigureWidget.getTabBar());
					break;
				default:
					break;
				}
			}
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
