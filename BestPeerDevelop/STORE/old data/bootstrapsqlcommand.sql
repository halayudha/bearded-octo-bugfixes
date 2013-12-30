-- First drop all existing databases for Bestpeer

drop database if exists BootstrapMetaBestPeer;

-- Create all needed databases

create database if not exists BootstrapMetaBestPeer;

-- Create all needed tables

-- this table store the name of global corporate db
create table BootstrapMetaBestPeer.CORPORATE_DB_NAME(db_name varchar(50) primary key, db_desc varchar(100))
--insert into BootstrapMetaBestPeer.CORPORATE_DB_NAME values('Corporate Database','Corporate Database as global view for all peers to collaborate')
insert into BootstrapMetaBestPeer.CORPORATE_DB_NAME values('Health Care Database','Corporate Database as global view for all peers to collaborate')

-- these table for storing index
create table BootstrapMetaBestPeer.table_index (ind varchar(20) not null, val text not null, PRIMARY KEY (ind))
create table BootstrapMetaBestPeer.range_index_string (table_name varchar(20) not null, column_name varchar(20) not null, val text not null, lower_bound text not null, upper_bound text not null)
create table BootstrapMetaBestPeer.range_index_number (table_name varchar(20) not null, column_name varchar(20) not null, val text not null, lower_bound float not null, upper_bound float not null)

-- this table LOCAL_ADMINS is for local addmin account that run server peer
create table BootstrapMetaBestPeer.LOCAL_ADMINS(admin_name varchar(50) primary key, admin_desc varchar(500), password varchar(20))
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u1','admin user','u1')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u2','admin user','u2')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u3','admin user','u3')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u4','admin user','u4')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u5','admin user','u5')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u6','admin user','u6')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u7','admin user','u7')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u8','admin user','u8')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u9','admin user','u9')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u10','admin user','u10')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u11','admin user','u11')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u12','admin user','u12')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u13','admin user','u13')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u14','admin user','u14')
insert into BootstrapMetaBestPeer.LOCAL_ADMINS values('u15','admin user','u15')

create table BootstrapMetaBestPeer.GLOBAL_SCHEMAS (schema_name varchar(50) primary key, schema_desc varchar(500), schema_type  varchar(20));
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('patient','patient description','table')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('physician','physician description','table')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('treat','treat description','table')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('allergy','allergy description','table')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('medical_test','medical_test description','table')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('medication','medication description','table')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMAS values('visit','visit description','table')

create table BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS (schema_name varchar(50), column_name varchar(50), column_desc  varchar(200), column_type  varchar(20));
--Add new column of patient
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','patient_id','patient_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','first_name','first_name description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','last_name','last_name description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','date_of_birth','date_of_birth description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','age','age description','int')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','gender','gender description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','address','address description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('patient','nationality','nationality description','text')
--Add new column of physicican 
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('physician','physician_id','physician_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('physician','first_name','first_name description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('physician','last_name','last_name description','text')
--Add new column of treat
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('treat','patient_id','patient_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('treat','physician_id','physician_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('treat','start_date','start_date description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('treat','end_date','end_date description','text')
--Add new column of allergy
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('allergy','patient_id','patient_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('allergy','allergy_name','allergy_name description','text')
--Add new column of test

insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medical_test','patient_id','patient_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medical_test','test_name','test_name description: isMappingColumn','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medical_test','value','value description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medical_test','test_date','test_date description','text')
--Add new column of medication 
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medication','patient_id','patient_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medication','medicine','medicine description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medication','dosage','dosage description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medication','start_date','start_date description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('medication','end_date','end_date description','text')
--Add new column of visit 
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('visit','patient_id','patient_id description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('visit','visit_date','visit_date description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('visit','location','location description','text')
insert into BootstrapMetaBestPeer.GLOBAL_SCHEMA_COLUMNS values('visit','reason','reason description: isMappingColumn','text')

--
create table BootstrapMetaBestPeer.ROLES(role_name varchar(50) primary key, role_desc varchar(500))
insert into BootstrapMetaBestPeer.ROLES values('public','this role represents for the public in corporate network')
insert into BootstrapMetaBestPeer.ROLES values('admin','peer local admin')
insert into BootstrapMetaBestPeer.ROLES values('patient','patient role')
insert into BootstrapMetaBestPeer.ROLES values('physician','physician role')
insert into BootstrapMetaBestPeer.ROLES values('authority','authority role')
--
create table BootstrapMetaBestPeer.ROLE_HIERARCHY(super_role_name varchar(50), sub_role_name varchar(50))

--
create table BootstrapMetaBestPeer.PRIVILEGES(privilege_id varchar(20), privilege_name varchar(20), privilege_desc varchar(50));
insert into BootstrapMetaBestPeer.PRIVILEGES values('SELECT','SELECT','select value in table or view');

--
create table BootstrapMetaBestPeer.ROLE_PERMISSIONS(role_name varchar(50), privilege_id varchar(20), object varchar(100), permission_type varchar(50), FOREIGN KEY (role_name) REFERENCES ROLES(role_name) ON DELETE CASCADE ON UPDATE CASCADE);
-- permission of admin
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','patient','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','physician','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','treat','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','allergy','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','medical_test','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','medication','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('admin','SELECT','visit','whole table')
--permission of patient
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.WHERE patient_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.first_name','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.last_name','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.date_of_birth','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.age','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.gender','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.address','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','patient.nationality','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','physician','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','treat.WHERE patient_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','treat.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','treat.physician_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','treat.start_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','treat.end_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','allergy.WHERE patient_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','allergy.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','allergy.allergy_name','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medical_test.WHERE patient_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medical_test.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medical_test.test_name','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medical_test.value','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medical_test.test_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medication.WHERE patient_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medication.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medication.medicine','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medication.dosage','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medication.start_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','medication.end_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','visit.WHERE patient_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','visit.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','visit.visit_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','visit.location','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('patient','SELECT','visit.reason','column level')

--permission for physician
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','patient','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','physician','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','allergy','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','medical_test','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','medication','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','treat.WHERE physician_id=me','row level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','treat.patient_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','treat.physician_id','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','treat.start_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','treat.end_date','column level')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('physician','SELECT','visit','whole table')

--role permission for authority
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','patient','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','physician','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','treat','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','allergy','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','medical_test','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','medication','whole table')
insert into BootstrapMetaBestPeer.ROLE_PERMISSIONS values('authority','SELECT','visit','whole table')

--
create table BootstrapMetaBestPeer.USERS(user_name varchar(50) primary key, user_desc varchar(500), password varchar(20))
insert into BootstrapMetaBestPeer.USERS values('u1','description of user 1','u1')
insert into BootstrapMetaBestPeer.USERS values('u2','description of user 2','u2')
insert into BootstrapMetaBestPeer.USERS values('u3','description of user 3','u3')
insert into BootstrapMetaBestPeer.USERS values('u4','description of user 4','u4')
insert into BootstrapMetaBestPeer.USERS values('u5','description of user 5','u5')
insert into BootstrapMetaBestPeer.USERS values('u6','description of user 6','u6')
insert into BootstrapMetaBestPeer.USERS values('u7','description of user 7','u7')
insert into BootstrapMetaBestPeer.USERS values('u8','description of user 8','u8')
insert into BootstrapMetaBestPeer.USERS values('u9','description of user 9','u9')
insert into BootstrapMetaBestPeer.USERS values('u10','description of user 10','u10')
insert into BootstrapMetaBestPeer.USERS values('u11','description of user 11','u11')
insert into BootstrapMetaBestPeer.USERS values('u12','description of user 12','u12')
insert into BootstrapMetaBestPeer.USERS values('u13','description of user 13','u13')
insert into BootstrapMetaBestPeer.USERS values('u14','description of user 14','u14')
insert into BootstrapMetaBestPeer.USERS values('u15','description of user 15','u15')
insert into BootstrapMetaBestPeer.USERS values('guest','description of user guest','guest')
insert into BootstrapMetaBestPeer.USERS values('S8284186E','description of user S8284186E','S8284186E')
insert into BootstrapMetaBestPeer.USERS values('S8285084H','description of user S8285084H','S8285084H')
insert into BootstrapMetaBestPeer.USERS values('PH1000001','description of user PH1000001','PH1000001')
insert into BootstrapMetaBestPeer.USERS values('PH1000002','description of user PH1000002','PH1000002')
insert into BootstrapMetaBestPeer.USERS values('AU1000001','description of user AU1000001','AU1000001')

--
create table BootstrapMetaBestPeer.USER_PERMISSIONS(user_name varchar(50), privilege_id varchar(20), object varchar(100), permission_type varchar(50), FOREIGN KEY (user_name) REFERENCES USERS(user_name) ON DELETE CASCADE ON UPDATE CASCADE);

--
create table BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT(user_name varchar(50), role_name varchar(50))
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u1','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u2','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u3','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u4','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u5','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u6','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u7','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u8','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u9','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u10','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u11','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u12','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u13','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u14','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('u15','admin')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('guest','public')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('S8284186E','patient')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('S8285084H','patient')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('PH1000001','physician')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('PH1000002','physician')
insert into BootstrapMetaBestPeer.USER_ROLE_ASSIGNMENT values('AU1000001','authority')

-- semantic table




---semantic mapping stored at BOOTSTRAP------- 



---semantic mapping stored at BOOTSTRAP------- 



---semantic mapping stored at BOOTSTRAP------- 



---semantic mapping stored at BOOTSTRAP------- 



---semantic mapping stored at BOOTSTRAP------- 
create table BootstrapMetaBestPeer.SEMANTIC_MAPPING (global_term text, table_name text, column_name text, semantics text)
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('respiratory problem','visit','reason','respiratory problem')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('headache','visit','reason','cephalalgia;cephalea;cephalgia;headache')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('stomach problem','visit','reason','stomach problem')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('heart problem','visit','reason','heart problem')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('sore throat','visit','reason','sore throat')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('fever','visit','reason','fever')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('cough','visit','reason','cough')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('backache','visit','reason','backache')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('total iron binding capacity','medical_test','test_name','TIBC;total iron binding capacity')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('mean corpuscular volume','medical_test','test_name','MCV;mean corpuscular volume')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('mean corpuscular hemoglobin','medical_test','test_name','MCH;mean corpuscular hemoglobin')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('heart rate','medical_test','test_name','heart rate')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('red cell distribution width','medical_test','test_name','RDW;red cell distribution width')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('MCHC','medical_test','test_name','MCHC')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('arterial blood gas','medical_test','test_name','ABG;arterial blood gas')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('swine influenza','medical_test','test_name','H1N1;swine flu;swine influenza')
insert into BootstrapMetaBestPeer.SEMANTIC_MAPPING values ('dengue','medical_test','test_name','dengue')
