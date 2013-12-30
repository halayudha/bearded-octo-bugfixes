package sg.edu.nus.dbgen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import sg.edu.nus.dbconnection.DBProperty;

/**
 * 
 * @author VHTam
 */

public class GenerateSampleHealthCareDB {
	
	Vector<PrintStream> localdbsql_outputs = new Vector<PrintStream>();
	Vector<PrintStream> serversql_outputs = new Vector<PrintStream>();
	PrintStream bootstrapsql_output = null;

	Hashtable<String, Vector<String>> MRNs = new Hashtable<String, Vector<String>>();
	Hashtable<String, Vector<String>> Physicians = new Hashtable<String, Vector<String>>();
	Vector<String> PatientNames = new Vector<String>();
	
	Hashtable <String, String> addressOfPatient = new Hashtable<String, String>();
	Hashtable<String, Vector<String>> patientsByAddress = new Hashtable<String, Vector<String>>();
	
	Hashtable<String, Vector<String>> Terms = new Hashtable<String, Vector<String>>();
	
	Vector<String> Reasons = new Vector<String>();
	
	Vector<String> Locations = new Vector<String>();
	Vector<String> Addresses = new Vector<String>();
	
	Vector<String> Tests = new Vector<String>();
	Hashtable<String, Vector<String>> TestResults = new Hashtable<String, Vector<String>>();
	
	Vector<String> Allergies = new Vector<String>();
	
	Vector<String> Medicines = new Vector<String>();
	Hashtable<String, Vector<String>> Dosage = new Hashtable<String, Vector<String>>();
	
	
	int nPeer = 6;
	int nPatientPerPeer = 9;
	int percentPatientHaveMedication = 10;
	int percentH1N1 = 10;
	int nPhysicianPerPeer = 2;
	
	private String MRN_David = "S8284186E";
	private String MRN_Tam = "S8285084H";
	
	Hashtable<String, Vector<Vector<String>>> MappingTables = new Hashtable<String, Vector<Vector<String>>>();
	Vector<Vector<String>> GlobalTerms = new Vector<Vector<String>>();
	Vector<Vector<String>> SemanticMappings = new Vector<Vector<String>>();
	
	private void addMapping(String peerName, String localTerm, String globalTerm, String tableName, String colName){
		
		Vector<Vector<String>> mappingTable = MappingTables.get(peerName);
		if (mappingTable==null){
			mappingTable = new Vector<Vector<String>>();
			MappingTables.put(peerName, mappingTable);
		}
		
		Vector<String> mapping = new Vector<String>();
		mapping.add(localTerm);
		mapping.add(globalTerm);
		mapping.add(tableName);
		mapping.add(colName);
		
		boolean exist = false;
		for (Vector<String> line : mappingTable){
			if (line.get(0).equals(localTerm)){
				exist = true;
				break;
			}
		}
		if (!exist){
			mappingTable.add(mapping);
		}
		
		addSemanticMapping(localTerm, globalTerm, tableName, colName);
		
		addGlobalTerm(globalTerm, tableName, colName);
	}
	
	private void addSemanticMapping(String localTerm, String globalTerm, String tableName, String colName){
		boolean exist = false;
		Vector<String> semanticMapping = null;
		for (Vector<String> line : SemanticMappings){
			if (line.get(0).equals(globalTerm) && line.get(1).equals(tableName)&& line.get(2).equals(colName)){
				exist = true;
				semanticMapping = line;
				break;
			}
		}
		if (!exist){
			semanticMapping = new Vector<String>();
			semanticMapping.add(globalTerm);
			semanticMapping.add(tableName);
			semanticMapping.add(colName);
			
			semanticMapping.add(localTerm);

			SemanticMappings.add(semanticMapping);
		} else { // exist
			String semantics = semanticMapping.get(semanticMapping.size()-1);
			if (!semantics.contains(localTerm)){
				semantics +=";"+localTerm;
				semanticMapping.remove(semanticMapping.size()-1);
				semanticMapping.add(semantics);
			}
		}
		
	}
	
	private void addGlobalTerm(String globalTerm, String tableName, String colName){
		boolean exist = false;
		for (Vector<String> line : GlobalTerms){
			if (line.get(0).equals(globalTerm) && line.get(1).equals(tableName)&& line.get(2).equals(colName)){
				exist = true;
				break;
			}
		}
		if (!exist){
			Vector<String> termItem = new Vector<String>();
			termItem.add(globalTerm);
			termItem.add(tableName);
			termItem.add(colName);
			GlobalTerms.add(termItem);
		}
		
	}
	
	private boolean willInsert(String MRN){
		if (MRN.equals(MRN_David)||MRN.equals(MRN_Tam)){
			return true;
		}
		return random(1,100) < percentPatientHaveMedication ? true : false; 
	}
	
	private void openOutputFile() {
		try {
			String filename = null;
			//localdb sql file
			for (int peer = 1; peer <= nPeer; peer++) {
				filename = "./STORE/localdbsample"+peer+".sql";
				PrintStream fileoutput = new PrintStream(new FileOutputStream(
						filename));
				localdbsql_outputs.add(fileoutput);
				//initial sql statements
				fileoutput.println("drop database if exists localdb");
				fileoutput.println("create database if not exists localdb");
				fileoutput.println("use localdb");
				fileoutput.println();
			}
			
			// read existing serversql file
			String strContent = "";
			try {
				String thisLine;
				BufferedReader br = new BufferedReader(new FileReader(
						"./sqlscript/serversqlcommand.sql"));
				while ((thisLine = br.readLine()) != null) { // while loop begins
																// here
					if (thisLine.contains("MAPPING_TABLE"))
						continue;

					strContent += thisLine + "\n";
				} // end while
				br.close();
			} // end try
			catch (IOException e) {
				System.err.println("Error: " + e);
			}
						     
			//server sql file
			for (int peer = 1; peer <= nPeer; peer++) {
				filename = "./STORE/serversqlcommand"+peer+".sql";
				PrintStream fileoutput = new PrintStream(new FileOutputStream(
						filename));
				serversql_outputs.add(fileoutput);
				//initial sql statements
				fileoutput.println(strContent);
			}
			
			//bootstrap sql file
			filename = "./STORE/bootstrapsqlcommand.sql";
			bootstrapsql_output = new PrintStream(new FileOutputStream(filename));
			// read existing boostrapsql file
			strContent = "";
			try {
				String thisLine;
				BufferedReader br = new BufferedReader(new FileReader(
						"./sqlscript/bootstrapsqlcommand.sql"));
				while ((thisLine = br.readLine()) != null) { // while loop begins
																// here
					if (thisLine.contains("SEMANTIC_MAPPING"))
						continue;

					strContent += thisLine + "\n";
				} // end while
				br.close();
			} // end try
			catch (IOException e) {
				System.err.println("Error: " + e);
			}
			
			bootstrapsql_output.println(strContent);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void closeOutputFile(){
		try {
			for (int peer = 0; peer < nPeer; peer++) {
				PrintStream fileoutput = localdbsql_outputs.get(peer);
				fileoutput.close();
				
				fileoutput = serversql_outputs.get(peer);
				fileoutput.close();
				
				bootstrapsql_output.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private String READ_PARAM = "READ_PARAM";
	private String READ_TERM = "READ_TERM";
	private String READ_HOSPITAL = "READ_HOSPITAL";
	private String READ_NAME = "READ_NAME";
	private String READ_REASON = "READ_REASON";
	private String READ_TEST = "READ_TEST";
	private String READ_ALLERGY = "READ_ALLERGY";
	private String READ_MEDICINE = "READ_MEDICINE";
	private String READ_ADDRESS = "READ_ADDRESS";

	
	private void processLine(String lineType, String line){
		
		if (line.indexOf("--") == -1 && !line.equals("")) {
			if (lineType.equals(READ_PARAM)){
				processReadParam(line);
			} else
				if (lineType.equals(READ_TERM)){
					processReadTerm(line);
				} else
					if (lineType.equals(READ_HOSPITAL)){
						processReadHospital(line);
					} else
						if (lineType.equals(READ_NAME)){
							processReadPatientName(line);
						} else
							if (lineType.equals(READ_REASON)){
								processReadReason(line);
							}else
								if (lineType.equals(READ_TEST)){
									processReadTest(line);
								}else
									if (lineType.equals(READ_ALLERGY)){
										processReadAllergy(line);
									}else
										if (lineType.equals(READ_MEDICINE)){
											processReadMedicine(line);
										} else
											if (lineType.equals(READ_ADDRESS)){
												processReadAddress(line);
											}
		}
	}
	
	private void processReadAddress(String line){
		Addresses.add(line);
	}
	
	private void processReadParam(String line){
		String[] arr = line.split("=");
		if (arr[0].equals("nPeer")){
			nPeer = Integer.parseInt(arr[1]);
		} else 
			if (arr[0].equals("nPatientPerPeer")){
				nPatientPerPeer = Integer.parseInt(arr[1]);
			} else 
				if (arr[0].equals("percentageOfPatientHaveMedication")){
					percentPatientHaveMedication = Integer.parseInt(arr[1]);
				}  
	}
	
	private void processReadTerm(String line){
		String[] arr = line.split(";");
		Vector<String> terms = new Vector<String>();
		for (String term: arr){
			terms.add(term);
		}
		Terms.put(arr[0], terms);
	}
	
	private void processReadReason(String line){
		Reasons.add(line);
	}
 
	
	private void processReadHospital(String line){
		Locations.add(line);
	}
	
	private void processReadPatientName(String line){
		PatientNames.add(line);
	}
	
	private void processReadTest(String line){
		String[] testInfo = line.split(";");
		Vector<String> testResult = new Vector<String>();
		for (int i=1; i<testInfo.length; i++){
			testResult.add(testInfo[i]);
		}
		TestResults.put(testInfo[0], testResult);
		Tests.add(testInfo[0]);
	}
	
	private void processReadAllergy(String line){
		Allergies.add(line);
	}
	
	private void processReadMedicine(String line){
		String[] medInfo = line.split(";");
		Vector<String> medDosage = new Vector<String>();
		for (int i=1; i<medInfo.length; i++){
			medDosage.add(medInfo[i]);
		}
		Dosage.put(medInfo[0], medDosage);
		Medicines.add(medInfo[0]);
	}
	
	private void loadDataDictionary(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"sqlscript/datadictionary.txt"));

			String line = null;
			String lineType = null;
			
			while ((line = reader.readLine()) != null) {
				if (line.contains("--params")){
					lineType = READ_PARAM;
				} else
					if (line.contains("--temrs")){
						lineType = READ_TERM;
					} else
						if (line.contains("--hospitals")){
							lineType = READ_HOSPITAL;
						} else
							if (line.contains("--patient names")){
								lineType = READ_NAME;
							} else
								if (line.contains("--reasons")){
									lineType = READ_REASON;
								}else
									if (line.contains("--tests")){
										lineType = READ_TEST;
									}else
										if (line.contains("--allergies")){
											lineType = READ_ALLERGY;
										}else
											if (line.contains("--medicines")){
												lineType = READ_MEDICINE;
											} else
												if (line.contains("--postal address")){
													lineType = READ_ADDRESS;
												}				
				processLine(lineType, line);
			} 
			System.out.println("Finish loading data dictionary");
		} catch (Exception e) {
			e.printStackTrace();
		}

		///////////////////////////////
		
		for (int peer=1; peer<=nPeer; peer++ ){
			
			String peerName = getPeerName(peer);
			
			Vector<String> patients = new Vector<String>();
			Vector<String> physicians = new Vector<String>();
			
			if (peer==1){
				patients.add(MRN_David);
				patients.add(MRN_Tam);
			}
			
			for (int i=1; i<=nPatientPerPeer; i++){
				patients.add(getPatientMRN(peer));
			}
			MRNs.put(peerName, patients);
			
			for (int i=1; i<=nPhysicianPerPeer; i++ ){
				physicians.add("PH"+peer+"00000"+i);
			}
			Physicians.put(peerName,physicians);
			
		}

	}
	
	private void generateLocalDBSQL(){

		String sql = null;
		
		Vector<String> patients = new Vector<String>();
		
		//create patient list
		for (int peer=1; peer<=nPeer; peer++ ){
			String peerName = "peer"+peer;		
			Vector<String> peer_patients = MRNs.get(peerName);
			
			for (String mrn: peer_patients){
				
				if (willInsert(mrn)){
					patients.add(mrn);//patients list will have allergy records...
				}
				
				String address = getAddress();
				addressOfPatient.put(mrn, address);
				
				Vector<String> patientsThisAddress = patientsByAddress.get(address);
				if (patientsThisAddress == null){
					patientsThisAddress = new Vector<String>();
					patientsByAddress.put(address, patientsThisAddress);
				}
				patientsThisAddress.add(mrn);				
			}
		}

		// generate data
		for (int peer=1; peer<=nPeer; peer++ ){
			
			Hashtable<String, String> visitDates = new Hashtable<String, String>();
			for (String mrn: patients){
				String date = getNormalDate();
				visitDates.put(mrn, date);
			}
			
			int peerSqlFileIndex = peer - 1;//for specify output file
			
			System.out.println("\n---------- Peer "+peer+" ----------");
			
			String peerName = getPeerName(peer);
			
			String location = getLocation(peer);
			
			Vector<String> peer_patients = MRNs.get(peerName);
			//generate patient/physician/treat table
			//patient
			PrintlnLocalDbSql("CREATE TABLE patient(patient_id varchar(30) NOT NULL, first_name text, last_name text, date_of_birth text, age integer, gender text, address text, nationality text, CONSTRAINT patient_pkey PRIMARY KEY (patient_id))", peerSqlFileIndex);
			for (int i=0; i<peer_patients.size(); i++){
				sql = "insert into patient values (";
				String MRN = peer_patients.get(i); 
				sql += "'"+MRN+"'";
				String[] names = getName();
				sql += ", '"+names[1]+"'";
				sql += ", '"+names[0]+"'";
				int age = getAge();
				sql += ", '"+getDateOfBirth(age)+"'";
				sql += ", "+age+"";
				sql += ", '"+getGender()+"'";
				sql += ", '"+addressOfPatient.get(MRN)+"'";
				sql += ", '"+getNationality()+"'";
				sql += ")";
				PrintlnLocalDbSql(sql, peerSqlFileIndex);
			}

			//physician
			PrintlnLocalDbSql(peerSqlFileIndex);
			PrintlnLocalDbSql("CREATE TABLE physician(physician_id varchar(30) NOT NULL, first_name text, last_name text, CONSTRAINT patient_pkey PRIMARY KEY (physician_id))", peerSqlFileIndex);
			Vector<String> physicians = Physicians.get(peerName);
			for (int i=0; i<nPhysicianPerPeer; i++){
				sql = "insert into physician values (";
				String physicican_id = physicians.get(i);
				sql += "'"+physicican_id+"'";
				String[] names = getName();
				sql += ", '"+names[0]+"'";
				sql += ", '"+names[1]+"'";
				sql += ")";
				PrintlnLocalDbSql(sql, peerSqlFileIndex);
			}
			
			//treat
			PrintlnLocalDbSql(peerSqlFileIndex);
			PrintlnLocalDbSql("CREATE TABLE treat(patient_id varchar(30), physician_id varchar(30), start_date text, end_date text)", peerSqlFileIndex);
			for (int i=0; i<patients.size(); i++){
				String MRN = patients.get(i);
				for (String physicican_id : physicians) {
					sql = "insert into treat values (";
					sql += "'" + MRN + "'";
					sql += ", '" + physicican_id + "'";
					String[] dates = getStartEndDate(visitDates.get(MRN));
					sql += ", '" + dates[0] + "'";
					sql += ", '" + dates[1] + "'";
					sql += ")";
					PrintlnLocalDbSql(sql, peerSqlFileIndex);
				}
			}
			
			//generate other tables
			//visit table
			PrintlnLocalDbSql(peerSqlFileIndex);
			PrintlnLocalDbSql("CREATE TABLE visit(patient_id text, visit_date text, location text, reason text)", peerSqlFileIndex);
			for (int i=0; i<patients.size(); i++){
				String MRN = patients.get(i);
				sql = "insert into visit values (";
				sql += "'"+patients.get(i)+"'";
				sql += ", '"+visitDates.get(MRN)+"'";
				sql += ", '"+location+"'";
				sql += ", '"+getReason(peer)+"'";
				sql += ")";
				PrintlnLocalDbSql(sql, peerSqlFileIndex);
			}

			//test table
			PrintlnLocalDbSql(peerSqlFileIndex);
			PrintlnLocalDbSql("CREATE TABLE medical_test(patient_id varchar(30), test_name text, value text, test_date text)", peerSqlFileIndex);
			for (int i=0; i<patients.size(); i++){
				String MRN = patients.get(i);
				sql = "insert into medical_test values (";
				sql += "'"+patients.get(i)+"'";
				String testName = getTestName(MRN);
				sql += ", '"+getMappingTestName(testName,peer)+"'";
				sql += ", '"+getTestResult(testName)+"'";
				boolean isJulyDate = testName.equals("H1N1") || testName.equals("dengue"); 
				String testDate = isJulyDate ? getDateInMonth(7) : visitDates.get(MRN); 
				sql += ", '"+testDate+"'";
				sql += ")";
				PrintlnLocalDbSql(sql, peerSqlFileIndex);
			}
			//and generate distribution H1N1 and dengue

				Enumeration<String> enumAddr = patientsByAddress.keys();
				Vector<String> addrs = new Vector<String>();
				while (enumAddr.hasMoreElements()){
					addrs.add(enumAddr.nextElement());
				}
				
				int numH1N1places = 5;
				int numDengueplaces = 4;
				Vector<String> H1N1Addrs = new Vector<String>();
				Vector<String> DengueAddrs = new Vector<String>();
				for (int i=0; i<numH1N1places; i++){
					H1N1Addrs.add(addrs.get(i));
				}
				for (int i=numH1N1places-1; i<numH1N1places+numDengueplaces-1; i++){
					DengueAddrs.add(addrs.get(i));
				}
				
				int maxNumH1N1Case = 2;
				for (String place: H1N1Addrs){
					Vector<String> ptList = patientsByAddress.get(place);
					int nCase = (int)random(1, maxNumH1N1Case);
					for (int i=0; i<nCase; i++){
						String MRN = getRandomElementInVector(ptList);
						sql = "insert into medical_test values (";
						sql += "'"+MRN+"'";
						String testName = "H1N1";
						sql += ", '"+getMappingTestName(testName,peer)+"'";
						String testResult = "positive";
						sql += ", '"+testResult+"'";
						boolean isJulyDate = testName.equals("H1N1") || testName.equals("dengue"); 
						String testDate = isJulyDate ? getDateInMonth(7) : visitDates.get(MRN); 
						sql += ", '"+testDate+"'";
						sql += ")";
						PrintlnLocalDbSql(sql, peerSqlFileIndex);						
					}
				}
			if (peer == 1||peer==2){				
				int maxNumDengueCase = 3;
				for (String place: DengueAddrs){
					Vector<String> ptList = patientsByAddress.get(place);
					int nCase = (int)random(1, maxNumDengueCase);
					for (int i=0; i<nCase; i++){
						String MRN = getRandomElementInVector(ptList);
						sql = "insert into medical_test values (";
						sql += "'"+MRN+"'";
						String testName = "dengue";
						sql += ", '"+getMappingTestName(testName,peer)+"'";
						String testResult = "positive";
						sql += ", '"+testResult+"'";
						boolean isJulyDate = testName.equals("H1N1") || testName.equals("dengue"); 
						String testDate = isJulyDate ? getDateInMonth(7) : visitDates.get(MRN); 
						sql += ", '"+testDate+"'";
						sql += ")";
						PrintlnLocalDbSql(sql, peerSqlFileIndex);						
					}
				}
			}
			
			//allergy table
			PrintlnLocalDbSql(peerSqlFileIndex);
			PrintlnLocalDbSql("CREATE TABLE allergy(patient_id varchar(30), allergy_name text)", peerSqlFileIndex);
			for (int i=0; i<patients.size(); i++){
				String MRN = patients.get(i);
				sql = "insert into allergy values (";
				sql += "'"+MRN+"'";
				sql += ", '"+getAllery(MRN)+"'";
				sql += ")";
				PrintlnLocalDbSql(sql, peerSqlFileIndex);
			}

			//medicine table
			PrintlnLocalDbSql(peerSqlFileIndex);
			PrintlnLocalDbSql("CREATE TABLE medication(patient_id varchar(30), medicine text, dosage text, start_date text, end_date text)", peerSqlFileIndex);
			for (int i=0; i<patients.size(); i++){
				String MRN = patients.get(i);
				sql = "insert into medication values (";
				sql += "'"+patients.get(i)+"'";
				String medicine = getMedicine();
				sql += ", '"+medicine+"'";
				sql += ", '"+getDosage(medicine)+"'";
				//String[] dates = getStartEndDate();
				String[] dates = getStartEndDate(visitDates.get(MRN));
				sql += ", '"+dates[0]+"'";
				sql += ", '"+dates[1]+"'";
				sql += ")";
				PrintlnLocalDbSql(sql, peerSqlFileIndex);
			}
		}

	}
	
	public void GenerateSampleSql() {
		
		loadDataDictionary();
		
		openOutputFile();

		generateLocalDBSQL();
			
		printMappingTable();
		
		closeOutputFile();
		
		System.out.println("\n----Success: Generate data to file already. Check current folder!");
		
	}

	private void printMappingTable(){
		
		System.out.println("---\nThis is the mapping table for each peer:");
		
		for (int peer=1; peer<=nPeer; peer++){
			
			int peerSqlFileIndex = peer-1;
			
			System.out.println("\n---mapping table of peer " + peer + "---");
			PrintlnServerSql("\n---mapping table stored at SERVER PEER " + peer + "---", peerSqlFileIndex);
			
			String peerName = getPeerName(peer);
			Vector<String> physicians = Physicians.get(peerName);
			
			PrintlnServerSql("create table Exported_DB.MAPPING_TABLE (local_term text, global_term text, table_name text, column_name text, user_name text)", peerSqlFileIndex);
			Vector<Vector<String>> mappingTable = MappingTables.get(getPeerName(peer));
			for (Vector<String> mapping: mappingTable){
				String line = "insert into Exported_DB.MAPPING_TABLE values ('";
				for (String s: mapping){
					line+=s+"','";
				}
				line += physicians.get(0)+"'";
				line+=")";
				PrintlnServerSql(line, peerSqlFileIndex);
			}
			
		}
		
		PrintlnBootstrapSql();
		PrintlnBootstrapSql("\n---semantic mapping stored at BOOTSTRAP------- ");
		PrintlnBootstrapSql("create table BootstrapMetaBestPeer.SEMANTIC_MAPPING (global_term text, table_name text, column_name text, semantics text)");
		for (Vector<String> semanticMapping : SemanticMappings) {
			String line = "insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('";
			for (String s : semanticMapping) {
				line += s + "','";
			}
			line=line.substring(0,line.length()-2);
			line += ")";
			PrintlnBootstrapSql(line);
		}
		
	}
	
	private String getLocation(int peer){
		return Locations.get(peer-1);
	}
	
	private String getPatientMRN(int peer){
		char lastChar = (char)random(65,90);
		return "S"+random(6,8)+""+peer+""+random(10,99)+""+random(101,999)+""+lastChar;
	}
	
	private String[] getName(){
		String name = getRandomElementInVector(PatientNames);
		name = name.trim();
		int spaceIndex = name.indexOf(" ");
		if (spaceIndex<0 ||spaceIndex>=name.length()){
			System.err.println("patient name: " + name +" cannot be processed!");
			System.err.println("program has used " + name +" instead.");
			name = "GOH HWEE CHUAN";
			spaceIndex = name.indexOf(" ");
		}
		String[] names= new String[2];
		names[0] = name.substring(0, spaceIndex);
		names[1] = name.substring(spaceIndex);
		names[1] = names[1].trim();
		return names;
	}

	private String getGender() {
		return random(0,1)==0 ? "male" : "female";
	}
	
	private int getAge(){
		return (int)random(20,40);
	}
	
	private String getDate(String sdate, String smonth, String syear){
		return syear+"-"+smonth+"-"+sdate;
	}
	
	private String getDateOfBirth(int age){
		long date = random(1,28);
		long month = random(1,12);
		long year = 2009 - age;
		
		String sdate = date<10 ? ("0"+date) : (""+date); 
		String smonth = month<10 ? ("0"+month) : (""+month); 
		String syear = year<10 ? ("0"+year) : (""+year);
		return getDate(sdate, smonth, syear);
	}
	
	private String getAddress(){
		return getRandomElementInVector(Addresses);
	}
	
	private String getNationality(){
		return "Singaporean";
	}
	
	private String[] getStartEndDate(String startDate){
		String[] dates = new String[2];
		dates[0] = startDate;
		dates[1]=getNextFewDays(startDate);
		return dates;
	}
	
	private String getMedicine(){
		return getRandomElementInVector(Medicines);
	}

	private String getDosage(String medicine){
		return getRandomElementInVector(Dosage.get(medicine));
	}
	
	Hashtable<String, Vector<String>> patientsAllergies = new Hashtable<String, Vector<String>>();
	
	private String getAllery(String MRN){
		Vector<String> allergies = patientsAllergies.get(MRN);
		if (allergies==null){
			allergies = new Vector<String>();
			patientsAllergies.put(MRN, allergies);
		}		
		String allergy;
		do {
			allergy = getRandomElementInVector(Allergies);
		} while (allergies.contains(allergy));
		allergies.add(allergy);
		
		return allergy;
	}
	
	Hashtable<String, Vector<String>> patientsTestNames = new Hashtable<String, Vector<String>>();
	
	private String getTestName(String MRN){
		Vector<String> testNames = patientsTestNames.get(MRN);
		if (testNames==null){
			testNames = new Vector<String>();
			patientsTestNames.put(MRN, testNames);
		}
		String test;
		do {
			test = getRandomElementInVector(Tests);
		} while (testNames.contains(test) || test.equals("dengue") || test.equals("H1N1"));		
		testNames.add(test);
		
		return test;
	}

	private String getMappingTestName(String testName, int peer){
		Vector<String> mapping = Terms.get(testName);
		String globalTerm = new String(testName);
		if (mapping!=null){
			testName = mapping.get(peer%mapping.size());		
		}
		addMapping(getPeerName(peer), testName, globalTerm, "medical_test", "test_name");	
		return testName;
	}
	
	private String getTestResult(String testName){
		return getRandomElementInVector(TestResults.get(testName));
	}
	
	private String getRandomElementInVector(Vector<String> vect){
		return vect.get((int)random(0,vect.size()-1));
	}
	
	private String getNormalDate(){
		long year = random(2006,2009);
		long month = year<2009 ? random(1,12): random(1,8);
		long date = random(1,20);

		String sdate = date<10 ? ("0"+date) : (""+date); 
		String smonth = month<10 ? ("0"+month) : (""+month); 
		String syear = year<10 ? ("0"+year) : (""+year);
		return getDate(sdate, smonth, syear);
	}
	
	private String getNextFewDays(String startDate){
		String[] arr = startDate.split("-");
		int olddate = Integer.parseInt(arr[2]);
		int newdate = olddate + (int)random(4,8);
		arr[2]= newdate<10 ? ("0"+newdate) : (""+newdate);
		return getDate(arr[2],arr[1],arr[0]);
	}

	private String getDateInMonth(long month){
		long date = random(1,28);
		long year = 2009;
		
		String sdate = date<10 ? ("0"+date) : (""+date); 
		String smonth = month<10 ? ("0"+month) : (""+month); 
		String syear = year<10 ? ("0"+year) : (""+year);
		return getDate(sdate, smonth, syear);
	}

    private long random( int min, int max ) {
        return Math.round( Math.random() * ( max-min) ) + min;
    }
    
	private void PrintlnLocalDbSql(String sql, int peerSqlFileIndex){
		System.out.println(sql);
		localdbsql_outputs.get(peerSqlFileIndex).println(sql);
	}

	private void PrintlnLocalDbSql(int peerSqlFileIndex){
		System.out.println();
		localdbsql_outputs.get(peerSqlFileIndex).println();
	}

	private void PrintlnServerSql(String sql, int peerSqlFileIndex){
		System.out.println(sql);
		serversql_outputs.get(peerSqlFileIndex).println(sql);
	}

	private void PrintlnServerSql(int peerSqlFileIndex){
		System.out.println();
		serversql_outputs.get(peerSqlFileIndex).println();
	}

	private void PrintlnBootstrapSql(String sql){
		System.out.println(sql);
		bootstrapsql_output.println(sql);
	}

	private void PrintlnBootstrapSql(){
		System.out.println();
		bootstrapsql_output.println();
	}

	private String getPeerName(int peer){
		return "peer"+peer;
	}
	
	private String getReason(int peer){
		String reason = getRandomElementInVector(Reasons);
		String globalTerm = new String(reason);
		Vector<String> reasonList = Terms.get(reason);
		if (reasonList != null){
			reason = reasonList.get(peer%reasonList.size());		
		}
		addMapping(getPeerName(peer), reason, globalTerm, "visit", "reason");
		return reason;
	}

	public static void main(String[] args) {
		GenerateSampleHealthCareDB app = new GenerateSampleHealthCareDB();
		app.GenerateSampleSql();
	}

}
