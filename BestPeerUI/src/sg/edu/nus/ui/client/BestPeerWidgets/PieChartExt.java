package sg.edu.nus.ui.client.BestPeerWidgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.gwtext.client.core.GenericConfig;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.chart.yui.PieChart;

public class PieChartExt extends PieChart {

	public void setLegendConfig(GenericConfig legendConfig) {
		JavaScriptObject styleConfig = 
			JavaScriptObjectHelper.getAttributeAsJavaScriptObject(chartConfig, "style");
		if(styleConfig == null){
			JavaScriptObjectHelper.setAttribute(chartConfig, "style", JavaScriptObjectHelper.createObject());
			styleConfig = 
				JavaScriptObjectHelper.getAttributeAsJavaScriptObject(chartConfig, "style");
		}
		
		JavaScriptObjectHelper.setAttribute(styleConfig, "legend", legendConfig.getJsObj());
		
	}
}
