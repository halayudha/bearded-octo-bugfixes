-- First drop all existing databases for Bestpeer

drop database if exists bestpeerdb;
drop database if exists bestpeerindexdb;
drop database if exists bestpeerschemamapping;
drop database if exists bestpeerservergs;
drop database if exists exported_db;
drop database if exists MetaBestPeer;

-- Create all needed databases

create database if not exists bestpeerdb;
create database if not exists bestpeerindexdb;
create database if not exists bestpeerschemamapping;
create database if not exists bestpeerservergs;
create database if not exists exported_db;
create database if not exists MetaBestPeer;

-- Create all needed tables

-- Create tables in bestpeerservergs 
--use bestpeerservergs;
--create table COMPANY(company_id varchar(30) primary key, company_name varchar(100), company_desc varchar(200))
--create table PRODUCTS(product_id varchar(30) primary key, product_name varchar(100), product_desc varchar(200), company_id varchar(30) )
--create table SALES(product_id varchar(30), quantity_sale int, benefit float, date_sale varchar(100))

-- Create tables in bestpeerindexdb
--use bestpeerindexdb;
create table bestpeerindexdb.table_index (ind varchar(20) not null, val text not null, PRIMARY KEY (ind))
create table bestpeerindexdb.column_index (ind varchar(20) not null, val text not null, PRIMARY KEY (ind)) 
create table bestpeerindexdb.data_index (ind varchar(20) not null, val text not null, PRIMARY KEY (ind)) 
create table bestpeerindexdb.local_index (ind varchar(20) not null, val text not null)
create table bestpeerindexdb.range_index_string (table_name varchar(20) not null, column_name varchar(20) not null, val text not null, lower_bound text not null, upper_bound text not null)
create table bestpeerindexdb.range_index_number (table_name varchar(20) not null, column_name varchar(20) not null, val text not null, lower_bound float not null, upper_bound float not null)

-- Create tables in BestPeerSchemaMapping;
--use BestPeerSchemaMapping;
CREATE TABLE BestPeerSchemaMapping.Matches (sourceDB VARCHAR(20) NOT NULL DEFAULT 'BestPeerServerLS' COMMENT 'the name of the source database', sourceTable VARCHAR(30) NOT NULL COMMENT 'the name of the source table', sourceVersion INT COMMENT 'the version of the source database, in case versioning is used', sourceColumn VARCHAR(50) NOT NULL COMMENT 'the name of the source column',	sourceType VARCHAR(30) NOT NULL COMMENT 'the type of the source column', targetDB VARCHAR(20) NOT NULL DEFAULT 'BestPeerServerGS' COMMENT 'the name of the target database', targetTable VARCHAR(30) NOT NULL COMMENT 'the name of the target table', targetVersion INT COMMENT 'the version of the target database, in case versioning is used', targetColumn VARCHAR(50) NOT NULL COMMENT 'the name of the target column', targetType VARCHAR(30) NOT NULL COMMENT 'the type of the target column', PRIMARY KEY (sourceDB,sourceTable,sourceVersion,sourceColumn) ) 
create table BestPeerSchemaMapping.joincondition ( table1 varchar(20), column1 varchar(20), table2 varchar(20), column2 varchar(20), jcondition varchar(10), primary key (table1, column1, table2, column2))
--Contruct mapping
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','allergy','1','patient_id','1','BestPeerServerGS','allergy','1','patient_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','allergy','1','allergy_name','1','BestPeerServerGS','allergy','1','allergy_name','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medication','1','patient_id','1','BestPeerServerGS','medication','1','patient_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medication','1','medicine','1','BestPeerServerGS','medication','1','medicine','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medication','1','dosage','1','BestPeerServerGS','medication','1','dosage','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medication','1','start_date','1','BestPeerServerGS','medication','1','start_date','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medication','1','end_date','1','BestPeerServerGS','medication','1','end_date','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','patient_id','1','BestPeerServerGS','patient','1','patient_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','first_name','1','BestPeerServerGS','patient','1','first_name','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','last_name','1','BestPeerServerGS','patient','1','last_name','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','date_of_birth','1','BestPeerServerGS','patient','1','date_of_birth','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','age','1','BestPeerServerGS','patient','1','age','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','gender','1','BestPeerServerGS','patient','1','gender','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','address','1','BestPeerServerGS','patient','1','address','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','patient','1','nationality','1','BestPeerServerGS','patient','1','nationality','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','physician','1','physician_id','1','BestPeerServerGS','physician','1','physician_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','physician','1','first_name','1','BestPeerServerGS','physician','1','first_name','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','physician','1','last_name','1','BestPeerServerGS','physician','1','last_name','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medical_test','1','patient_id','1','BestPeerServerGS','medical_test','1','patient_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medical_test','1','test_name','1','BestPeerServerGS','medical_test','1','test_name','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medical_test','1','value','1','BestPeerServerGS','medical_test','1','value','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','medical_test','1','test_date','1','BestPeerServerGS','medical_test','1','test_date','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','treat','1','patient_id','1','BestPeerServerGS','treat','1','patient_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','treat','1','physician_id','1','BestPeerServerGS','treat','1','physician_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','treat','1','start_date','1','BestPeerServerGS','treat','1','start_date','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','treat','1','end_date','1','BestPeerServerGS','treat','1','end_date','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','visit','1','patient_id','1','BestPeerServerGS','visit','1','patient_id','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','visit','1','visit_date','1','BestPeerServerGS','visit','1','visit_date','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','visit','1','location','1','BestPeerServerGS','visit','1','location','1')
INSERT INTO BestPeerSchemaMapping.Matches VALUES ('localdb','visit','1','reason','1','BestPeerServerGS','visit','1','reason','1')

-- Create tables in MetaBestPeer
--table for histogram
create table MetaBestPeer.GLOBAL_SCHEMAS_STAT (schema_name varchar(50) primary key, table_size int)
create table MetaBestPeer.GLOBAL_SCHEMA_COLUMNS_STAT (schema_name varchar(50), column_name varchar(50), n_distinct_value int )
create table MetaBestPeer.GLOBAL_SCHEMA_COLUMNS_HIST (schema_name varchar(50), column_name varchar(50), n_bin int, max  float, min  float, bin_width float, bin_values text)

-- this table store the name of global corporate db
create table MetaBestPeer.CORPORATE_DB_NAME(db_name varchar(50) primary key, db_desc varchar(100))
insert into MetaBestPeer.CORPORATE_DB_NAME values('Health Care Database','Corporate Database as global view for all peers to collaborate')

create table MetaBestPeer.GLOBAL_SCHEMAS (schema_name varchar(50) primary key, schema_desc varchar(500), schema_type  varchar(20));

create table MetaBestPeer.GLOBAL_SCHEMA_COLUMNS (schema_name varchar(50), column_name varchar(50), column_desc  varchar(200), column_type  varchar(20));

create table MetaBestPeer.LOCAL_EXPORTED_SCHEMAS (schema_name varchar(50) primary key, schema_desc varchar(500));

create table MetaBestPeer.LOCAL_EXPORTED_SCHEMA_COLUMNS (schema_name varchar(50), column_name varchar(50), column_desc  varchar(200));

--
create table MetaBestPeer.ROLES(role_name varchar(50) primary key, role_desc varchar(500))
insert into MetaBestPeer.ROLES values('public','this role represents for the public in corporate network')
insert into MetaBestPeer.ROLES values('admin','peer local admin')
--
create table MetaBestPeer.ROLE_HIERARCHY(super_role_name varchar(50), sub_role_name varchar(50))

--
create table MetaBestPeer.PRIVILEGES(privilege_id varchar(20), privilege_name varchar(20), privilege_desc varchar(50));
insert into MetaBestPeer.PRIVILEGES values('SELECT','SELECT','select value in table or view');

--
create table MetaBestPeer.ROLE_PERMISSIONS(role_name varchar(50), privilege_id varchar(20), object varchar(100), permission_type varchar(50), FOREIGN KEY (role_name) REFERENCES ROLES(role_name) ON DELETE CASCADE ON UPDATE CASCADE);

--
create table MetaBestPeer.USERS(user_name varchar(50) primary key, user_desc varchar(500), password varchar(20))
insert into MetaBestPeer.USERS values('u1','description of user 1','u1')
insert into MetaBestPeer.USERS values('u2','description of user 2','u2')
insert into MetaBestPeer.USERS values('u3','description of user 3','u3')
insert into MetaBestPeer.USERS values('u4','description of user 4','u4')
insert into MetaBestPeer.USERS values('u5','description of user 5','u5')
insert into MetaBestPeer.USERS values('u6','description of user 6','u6')
insert into MetaBestPeer.USERS values('u7','description of user 7','u7')
insert into MetaBestPeer.USERS values('u8','description of user 8','u8')
insert into MetaBestPeer.USERS values('u9','description of user 9','u9')
insert into MetaBestPeer.USERS values('u10','description of user 10','u10')
insert into MetaBestPeer.USERS values('u11','description of user 11','u11')
insert into MetaBestPeer.USERS values('u12','description of user 12','u12')
insert into MetaBestPeer.USERS values('u13','description of user 13','u13')
insert into MetaBestPeer.USERS values('u14','description of user 14','u14')
insert into MetaBestPeer.USERS values('u15','description of user 15','u15')
insert into MetaBestPeer.USERS values('guest','description of user guest','guest')
insert into MetaBestPeer.USERS values('S8284186E','description of user S8284186E','S8284186E')
insert into MetaBestPeer.USERS values('PH1000001','description of user PH1000001','PH1000001')
insert into MetaBestPeer.USERS values('PH1000002','description of user PH1000002','PH1000002')
insert into MetaBestPeer.USERS values('AU1000001','description of user AU1000001','AU1000001')

--
create table MetaBestPeer.USER_PERMISSIONS(user_name varchar(50), privilege_id varchar(20), object varchar(100), permission_type varchar(50), FOREIGN KEY (user_name) REFERENCES USERS(user_name) ON DELETE CASCADE ON UPDATE CASCADE);

--
create table MetaBestPeer.USER_ROLE_ASSIGNMENT(user_name varchar(50), role_name varchar(50))
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u1','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u2','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u3','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u4','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u5','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u6','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u7','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u8','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u9','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u10','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u11','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u12','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u13','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u14','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('u15','admin')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('guest','public')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('S8284186E','patient')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('PH1000001','physician')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('PH1000002','physician')
insert into MetaBestPeer.USER_ROLE_ASSIGNMENT values('AU1000001','authority')

create table Metabestpeer.REPORT (report_name text, role_name text, sql_string text, param text, description text, report_category text, create_by text)

insert into Metabestpeer.report values ('my information','patient','select * from patient', 'no_param', 'Display information of the patient', 'General', 'system built-in')
insert into Metabestpeer.report values ('medical test list','patient','select * from medical_test', 'no_param', 'Display the test list of the patient', 'Medical profile', 'system built-in')
insert into Metabestpeer.report values ('medication history','patient','select * from medication', 'no_param', 'Display the medication history of the patient', 'Medical profile', 'system built-in')
insert into Metabestpeer.report values ('visit history','patient','select * from visit', 'no_param', 'Display the visit history of the patient', 'Medical profile', 'system built-in')

insert into Metabestpeer.report values ('patient list','physician','select patient.patient_id, patient.first_name, patient.last_name, patient.date_of_birth, patient.age, patient.gender, patient.address from patient, treat where patient.patient_id = treat.patient_id', 'no_param', 'Display the list of patients under his treatment', 'General', 'system built-in')
insert into Metabestpeer.report values ('allergies of patient','physician','select patient.patient_id, allergy.allergy_name from patient, allergy  where patient.patient_id=$patient_id$ and patient.patient_id=allergy.patient_id', '-1|patient_id:', 'Display the allergies of a patient', 'Patient medical profile', 'system built-in')
insert into Metabestpeer.report values ('medication history','physician','select medication.patient_id, medication.medicine, medication.dosage, visit.location, medication.start_date, medication.end_date, visit.reason from visit, medication  where medication.patient_id=$patient_id$ and visit.patient_id=medication.patient_id and visit.visit_date=medication.start_date','-1|patient_id:', 'Display the medication history of a patient', 'Patient medical profile', 'system built-in')

insert into Metabestpeer.report values ('patient list','authority','select * from patient', 'no_param', 'Display the information of all patients', 'General', 'system built-in')
insert into Metabestpeer.report values ('allergy statistics', 'authority', 'select count(allergy.patient_id), allergy.allergy_name from allergy  group by allergy.allergy_name','no_param', 'Show the number of patients got allergies of each type', 'Analytical query', 'system built-in')
insert into Metabestpeer.report values ('medical test statistics', 'authority', 'select count(medical_test.patient_id), medical_test.test_name from medical_test  group by medical_test.test_name','no_param', 'Show the number of tests taken for each category of medical test', 'Analytical query', 'system built-in')
insert into Metabestpeer.report values ('dengue distribution', 'authority', 'select count(patient.patient_id), patient.address from patient, medical_test  where medical_test.test_name=\'dengue\' and medical_test.value=\'positive\' and medical_test.test_date>=\'2009-07-01\' and medical_test.test_date<=\'2009-07-30\' and patient.patient_id=medical_test.patient_id group by patient.address','no_param','Display the places of dengue outbreak', 'Analytical query', 'system built-in')
--insert into Metabestpeer.report values ('dengue distribution', 'authority', 'select patient.patient_id, patient.first_name, patient.age, patient.address, medical_test.test_name, medical_test.value, medical_test.test_date from patient, medical_test  where medical_test.test_name=\'dengue\' and medical_test.value=\'positive\' and medical_test.test_date>=\'2009-07-01\' and medical_test.test_date<=\'2009-07-30\' and patient.patient_id=medical_test.patient_id','no_param','Display information of dengue cases', 'Analytical query', 'system built-in')
--insert into Metabestpeer.report values ('all H1N1 patient information', 'authority', 'select patient.patient_id, patient.first_name, patient.age, medical_test.test_name, medical_test.value, medical_test.test_date from patient, medical_test  where medical_test.test_name=\'H1N1 flu\' and medical_test.value=\'positive\' and medical_test.test_date>=\'2009-07-01\' and medical_test.test_date<=\'2009-07-30\' and patient.patient_id=medical_test.patient_id','no_param','Display information of all H1N1 patients', 'Analytical query', 'system built-in')
insert into Metabestpeer.report values ('H1N1 statistics', 'authority', 'select patient.patient_id, patient.first_name, patient.age, patient.address, medical_test.test_name, medical_test.value, medical_test.test_date from patient, medical_test  where medical_test.test_name=\'H1N1 flu\' and medical_test.value=\'positive\' and medical_test.test_date>=$from_date$ and medical_test.test_date<=$to_date$ and patient.patient_id=medical_test.patient_id','-1|from_date:-1|to_date:','Display information of H1N1 patients in a period input by user', 'Analytical query', 'system built-in')
--insert into Metabestpeer.report values ('tests taken during a period','authority','select medical_test.patient_id, medical_test.test_name, medical_test.value, medical_test.test_date from medical_test  where medical_test.test_name=$test_name$ and medical_test.test_date>=$from_date$ and medical_test.test_date<=$to_date$', '-1|test_name:-1|from_date:-1|to_date:', 'Display the list of tests taken during a period', 'Analytical query', 'system built-in')


create table Exported_DB.SEMANTIC_MAPPING (global_term text, table_name text, column_name text, semantics text)

-- mapping table


---mapping table stored at SERVER PEER 1---


---mapping table stored at SERVER PEER 1---


---mapping table stored at SERVER PEER 1---


---mapping table stored at SERVER PEER 1---


---mapping table stored at SERVER PEER 1---


---mapping table stored at SERVER PEER 3---
create table Exported_DB.MAPPING_TABLE (local_term text, global_term text, table_name text, column_name text, user_name text)
insert into Exported_DB.MAPPING_TABLE values ('heart problem','heart problem','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('cough','cough','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('sore throat','sore throat','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('fever','fever','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('backache','backache','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('cephalgia','headache','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('stomach problem','stomach problem','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('respiratory problem','respiratory problem','visit','reason','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('MCV','mean corpuscular volume','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('heart rate','heart rate','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('RDW','red cell distribution width','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('MCHC','MCHC','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('TIBC','total iron binding capacity','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('MCH','mean corpuscular hemoglobin','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('ABG','arterial blood gas','medical_test','test_name','PH3000001')
insert into Exported_DB.MAPPING_TABLE values ('H1N1','H1N1','medical_test','test_name','PH3000001')
