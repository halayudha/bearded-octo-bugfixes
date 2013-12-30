package sg.edu.nus.ui.client.BestPeerWidgets;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.chart.yui.LineChart;
import com.gwtext.client.widgets.chart.yui.NumericAxis;
import com.gwtext.client.widgets.chart.yui.SeriesDefY;

/**
 * 
 * @author Wu Sai
 * 
 */
public class StatisticsUI extends BestPeerPanel implements Callback {

	public static interface I18nConstants extends Constants {
		String statistics();

		String IncomingMsg();

		String OutgoingMsg();

		String TotalQuery();

		String SelfQuery();

		String CompletedQuery();

		String IndexQuery();

		String IncomingByte();

		String OutgoingByte();

		String Minute();
	}

	private Panel staticsPanel = null;

	@Override
	public Panel getViewPanel() {
		if (staticsPanel == null) {
			JSONObject params = new JSONObject();

			params.put("statistics", new JSONString("get statistics"));

			params.put("TIME_UNIT", new JSONString(unit.toString()));
			params.put("NUM_UNITS", new JSONString(numUnits + ""));

			new RPCCall().invoke("StatisticsService", params, this);

			staticsPanel = new Panel();

			MessageBox.show(new MessageBoxConfig() {
				{
					setMsg("loading data, please wait...");

					setWidth(300);
					setWait(true);
					setWaitConfig(new WaitConfig() {
						{
							setInterval(200);
						}
					});
				}
			});
		}

		return staticsPanel;
	}

	public LineChart createChart(String title, String xField, String yField,
			String xLabel, String yLabel, Object[][] data, int min) {
		final Store store = new SimpleStore(new String[] { xField, yField },
				data);
		store.load();

		NumericAxis currencyAxis = new NumericAxis();
		currencyAxis.setMinimum(min);

		SeriesDefY[] seriesDef = new SeriesDefY[] { new SeriesDefY(yLabel,
				yField) };

		LineChart chart = new LineChart();
		chart.setTitle(title);
		chart.setWMode("transparent");
		chart.setStore(store);
		chart.setSeries(seriesDef);
		chart.setXField(xField);

		chart.setYAxis(currencyAxis);
		chart.setDataTipFunction("getDataTipText");
		chart.setExpressInstall("js/yui/assets/expressinstall.swf");
		chart.setWidth(600);
		chart.setHeight(200);

		return chart;
	}

	@Override
	public void onFailure() {
		MessageBox.hide();
		MessageBox.alert("Fail to connect to the server!");
	}

	TIME_UNIT unit = TIME_UNIT.MINUTE;
	int numUnits = 60;

	@Override
	public void onReady(JSONObject resultSet) {
		MessageBox.hide();

		for (int itemno = 0; itemno < REPORT_ITEM.itemset.length; itemno++) {
			REPORT_ITEM item = REPORT_ITEM.itemset[itemno];

			JSONArray dataArray = (JSONArray) resultSet.get(item.toString());
			Integer[][] data = new Integer[dataArray.size()][2];
			int min = Integer.MAX_VALUE;

			for (int i = 0; i < dataArray.size(); i++) {
				data[i][0] = new Integer(i + 1);
				data[i][1] = new Integer((int) ((JSONNumber) dataArray.get(i))
						.doubleValue());
				if (min > data[i][1])
					min = data[i][1];
			}

			if (item.toString().equals("NUM_LOCAL_QUERIES")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.SelfQuery(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			} else if (item.toString().equals("NUM_SUCCESSFULL_LOCAL_QUERIES")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.CompletedQuery(), unit.getName(), item.toString(),
						unit.getName(), item.toString(), data, min));
			} else if (item.toString().equals("NUM_QUERIES")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.TotalQuery(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			} else if (item.toString().equals("NUM_INDEX_QUERIES")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.IndexQuery(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			} else if (item.toString().equals("NUM_INCOMING_MSGS")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.IncomingMsg(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			} else if (item.toString().equals("NUM_OUTGOING_MSGS")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.OutgoingMsg(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			} else if (item.toString().equals("SIZE_INCOMING_MSGS")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.IncomingByte(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			} else if (item.toString().equals("SIZE_OUTGOING_MSGS")) {
				staticsPanel.add(this.createChart(BestPeerDesktop.constants
						.OutgoingByte(), unit.getName(), item.toString(), unit
						.getName(), item.toString(), data, min));
			}
		}

		staticsPanel.doLayout();
	}
}
