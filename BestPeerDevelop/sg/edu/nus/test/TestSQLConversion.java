/**
 * Created on Aug 18, 2009
 */
package sg.edu.nus.test;

import sg.edu.nus.bestpeer.queryprocessing.QueryRewriter;
import sg.edu.nus.sqlparser.SelectStatement;
import junit.framework.TestCase;

/**
 * @author David Jiang
 *
 */
public class TestSQLConversion extends TestCase {
	
	public void testAppendGlobalTermColWithTable(){
		System.out.println("testAppendGlobalTermColWithTable");
		String sql = "select mrn, test_name from test";
		SelectStatement stmt = new SelectStatement(sql);
		QueryRewriter qr = new QueryRewriter("userName");
		System.out.println();
		System.out.println(sql+"\n ==> "+ qr.rewriteSingleTableQuery(stmt));		
		
		sql = "select mrn, test_name from test where test_name = 'xxx' ";
		stmt = new SelectStatement(sql);
		System.out.println();
		System.out.println(sql+"\n ==> "+qr.rewriteSingleTableQuery(stmt));		

	}
}
