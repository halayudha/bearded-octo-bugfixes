import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;

//CREATE TABLE test(id int NOT NULL AUTO_INCREMENT, name varchar(100) NOT NULL, desg varchar(50) NOT NULL,  PRIMARY KEY  (id))
//CREATE TABLE test(name varchar(100) NOT NULL, desg varchar(50) NOT NULL,  PRIMARY KEY  (id))

//LOAD DATA INFILE 'c:/data/text.txt'INTO TABLE new_test FIELDS TERMINATED BY '|';

public class MySqlUpdate {

	private static Connection getConnection() throws Exception {
		// String driver = "org.gjt.mm.mysql.Driver";//com.mysql.jdbc.Driver
		String driver = "com.mysql.jdbc.Driver";//
		String url = "jdbc:mysql://localhost:3307/test";
		String usrname = "root";
		String passwd = "s3";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, usrname, passwd);
		return conn;
	}
	
	private static void bulkload(int numRec) throws Exception{
		Random rand = new Random();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			
			//drop table first
			ps = conn.prepareStatement("delete from test");
			ps.executeUpdate();
			
			//
			String query = "insert into test(name, desg) values(?, ?)";
			ps = conn.prepareStatement(query);
			
			long startTime = System.currentTimeMillis();
			
			int randomInt=0;
			for (int i = 0; i < numRec; i++) {
				randomInt = rand.nextInt();
				ps.setString(1, "ABC"+randomInt);
				ps.setString(2, "Software Engineer"+randomInt);
				ps.addBatch();
			}
			
			int[] updateCounts = ps.executeBatch();
			conn.commit();
			
			long endTime = System.currentTimeMillis();

			System.out.println("Num batch update:" + updateCounts.length + " Time: " + (endTime - startTime));
//			for (int count: updateCounts){
//				System.out.println(count);
//			}
			
		} catch (BatchUpdateException e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			conn.close();
		}

	}

	private static void sequentialLoad(int numRec) throws Exception{
		Random rand = new Random();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			
			//drop table first
			ps = conn.prepareStatement("delete from test");
			ps.executeUpdate();
			
			//
			String query = "insert into test(name, desg) values(?, ?)";
			ps = conn.prepareStatement(query);
			
			long startTime = System.currentTimeMillis();
			
			int randomInt=0;
			for (int i = 0; i < numRec; i++) {
				randomInt = rand.nextInt();
				ps.setString(1, "ABC"+randomInt);
				ps.setString(2, "Software Engineer"+randomInt);
				//ps.addBatch();
				ps.executeUpdate();
			}
			
			//int[] updateCounts = ps.executeBatch();
			conn.commit();
			
			long endTime = System.currentTimeMillis();

			System.out.println("Num sequential update:" + numRec + " Time: " + (endTime - startTime));
//			for (int count: updateCounts){
//				System.out.println(count);
//			}
			
		} catch (BatchUpdateException e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
			conn.close();
		}

	}

	private static void bulkWriteFile(int numRec) throws Exception{
		Random rand = new Random();
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("c:/data/bulkwrite.txt"));
			
			long startTime = System.currentTimeMillis();

			
			int randomInt=0;
			String record;
			
			for (int i = 0; i < numRec; i++) {
				randomInt = rand.nextInt();
				record = i + "|ABC"+randomInt+"|Software Engineer"+randomInt;
				writer.write(record+"\n");
			}
			
			writer.flush();
			
			long endTime = System.currentTimeMillis();

			System.out.println("Num record file:" + numRec + " Time: " + (endTime - startTime));
//			for (int count: updateCounts){
//				System.out.println(count);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void main(String[] args) throws Exception {
//		for (int i=1; i<=10; i++){
//			bulkload(100000*i);
//			sequentialLoad(100000*i);
//		}
		
		//sequentialLoad(200000);
		
		bulkWriteFile(200000);
	}
//result:
//Num update:10000 Time: 875

}