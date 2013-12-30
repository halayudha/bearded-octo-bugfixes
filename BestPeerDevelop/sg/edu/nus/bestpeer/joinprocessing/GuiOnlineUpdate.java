package sg.edu.nus.bestpeer.joinprocessing;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;

/**
 * 
 * @author dcsvht
 *
 */
public class GuiOnlineUpdate {

	SelectExecutor selectExecutor = null;
	String underlyTable = null;
	String sql = null;

	public GuiOnlineUpdate(SelectExecutor selectExecutor, String underlyTable,
			String sql) {
		this.selectExecutor = selectExecutor;
		this.underlyTable = underlyTable;
		this.sql = sql;
	}

	public void updateViewTableResult() {
		selectExecutor.viewResult(underlyTable, sql);
	}
}
