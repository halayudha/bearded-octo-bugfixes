/**
 * Created on Sep 23, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.chart.yui.LineChart;
import com.gwtext.client.widgets.chart.yui.NumericAxis;
import com.gwtext.client.widgets.chart.yui.SeriesDefY;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * @author David Jiang
 *
 */
public class LineChartPanel extends ChartPanelBase {
	
	@Override
	public Panel getViewPanel() {
		if(panel == null) {
			panel = new Panel();
			panel.setLayout(new VerticalLayout(15));
			MemoryProxy proxy = new MemoryProxy(getData());
      RecordDef recordDef = new RecordDef(
              new FieldDef[]{
                      new StringFieldDef("month"),
                      new FloatFieldDef("rent"),
                      new FloatFieldDef("utilities")
              }
      );

      ArrayReader reader = new ArrayReader(recordDef);
      final Store store = new Store(proxy, reader);
      store.load();

      SeriesDefY[] seriesDef = new SeriesDefY[]{

              new SeriesDefY("Rent", "rent"),
              new SeriesDefY("Utilities", "utilities")

      };

      NumericAxis currencyAxis = new NumericAxis();
      currencyAxis.setMinimum(800);
      currencyAxis.setLabelFunction("formatCurrencyAxisLabel");
      final LineChart chart = new LineChart();
      chart.setTitle("Monthly Expenses");
      chart.setWMode("transparent");
      chart.setStore(store);
      chart.setSeries(seriesDef);
      chart.setXField("month");
      chart.setYAxis(currencyAxis);
      chart.setDataTipFunction("getDataTipText");
      chart.setExpressInstall("js/yui/assets/expressinstall.swf");
      chart.setWidth(500);
      chart.setHeight(400);

      panel.add(chart);
		}
		return panel;
	}

	private static Object[][] getData() {
		return new Object[][] {
				new Object[] { "January", new Double(880.00), new Double(894.68) },
				new Object[] { "February", new Double(880.00), new Double(901.35) },
				new Object[] { "March", new Double(880.00), new Double(889.32) },
				new Object[] { "April", new Double(880.00), new Double(884.71) },
				new Object[] { "May", new Double(910.00), new Double(879.811) },
				new Object[] { "June", new Double(910.00), new Double(897.95) } };
	}
}
