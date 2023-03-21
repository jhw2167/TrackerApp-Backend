
/*
	SQL Helper script for managin finances DB

*/

-- DROP and define schema finances
DROP SCHEMA IF EXISTS financesPractice CASCADE;
CREATE SCHEMA financesPractice;


-- create type RECIPIENT
/*
 * 	PERSONAL - Purchase made for myself
 *  FRIEND - "" friend
 *  FAMILY - "" Family
 *  DATE -  "" payment made for a date
 *  GROUP - "" for a group of people (likely expecting payment back)
 */
DROP TYPE IF EXISTS RECIPIENT CASCADE;
CREATE TYPE RECIPIENT AS ENUM ('PERSONAL', 'FRIEND', 'FAMILY', 'DATE', 'GROUP');

-- create type status of the transaction
/*
 * 	COVERED - this transaction will be reimbursed by another party
 *  OWED - I am owed money on this transaction
 *  PENDING - This transaction is not yet realeased
 *  COMPLETE - Most transactions; committed and realized at the point of contact
 *  OWED_PARTIAL - I am owed money on part of this transaction
 */
DROP TYPE IF EXISTS STATUS_TYPE CASCADE;
CREATE TYPE STATUS_TYPE AS ENUM ('COMPLETE', 'COVERED', 'OWED', 'PENDING', 'OWED_PARTIAL');


-- create type institution_type indicated what type the pay_method is
/*
 * CREDIT - this is a credit card account - balance indicates what I OWE
 * BANK - this is a bank account - balance indicates what I HAVE
 * APP - app like Venmo - balance indicates internal debit funds
 * SIMPLE - like cash or a credit card, likely no balance listed
 */
DROP TYPE IF EXISTS  INSTITUTION_TYPE CASCADE;
CREATE TYPE INSTITUTION_TYPE AS ENUM('SIMPLE', 'CREDIT', 'BANK', 'APP');
 

------------ DROP TABLES ------------
DROP TABLE SIMPLE_TRANSACTIONS 

SELECT * FROM VENDOR_NAMES VN 
DROP TABLE VENDOR_NAMES 

--Test table queries

--DROP TABLE IF EXISTS vendor CASCADE; !!!
CREATE TABLE vendors (
vendor VARCHAR(50),
amount NUMERIC(10, 2) NOT NULL DEFAULT '0',
category VARCHAR(50) NOT NULL DEFAULT 'Misc'
is_typically_income BOOLEAN DEFAULT FALSE,
website varchar(50) DEFAULT NULL,
notes VARCHAR(1024) DEFAULT NULL
);


SELECT * FROM VENDORS V

-- DROP and create table vendor_names
--DROP TABLE IF EXISTS vendor_names CASCADE; !!!
CREATE TABLE vendor_names (
cc_id VARCHAR(100) PRIMARY KEY,
vendor VARCHAR(50) REFERENCES vendors(vendor),
);

SELECT * FROM VENDOR_NAMES V 

-- DROP and create table pay_methods
DROP TABLE IF EXISTS pay_methods;

CREATE TABLE pay_methods (
pm_id NUMERIC PRIMARY KEY,
pay_method VARCHAR NOT NULL,
institution_type VARCHAR DEFAULT 'SIMPLE',
balance NUMERIC DEFAULT NULL,
credit_line NUMERIC DEFAULT NULL,
cash_back NUMERIC DEFAULT NULL,
description VARCHAR(1023) DEFAULT NULL
);


DROP TABLE IF EXISTS pay_method_keys;

CREATE TABLE pay_methods (
key_id NUMERIC PRIMARY KEY,
pm_id NUMERIC NOT NULL REFERENCES PAY_METHODS (pm_id),
user_id VARCHAR NOT NULL REFERENCES PAY_METHODS (user_id)
)

INSERT INTO PAY_METHODS (pm_id, pay_method, institution_type) VALUES (0, 'CASH', 'SIMPLE')
INSERT INTO PAY_METHOD_keys (key_id, pm_id, USER_ID) VALUES (0, 0, '20230303JACKHENRYWELSH@GMAIL.COM')

SELECT * FROM pay_method_keys pmk WHERE pm_id=0


-- DROP and create table vendors
DROP TABLE IF EXISTS transactions CASCADE;
CREATE TABLE transactions (
	true_id VARCHAR PRIMARY KEY,
	t_id INTEGER NOT NULL unique,
	purchase_date VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE,
	amount NUMERIC(10, 2) NOT NULL DEFAULT '0' CHECK (amount>=0),
	vendor VARCHAR(50) REFERENCES vendors(vendor),
	category VARCHAR(50) NOT NULL DEFAULT 'Misc',
	bought_for RECIPIENT NOT NULL DEFAULT 'PERSONAL',
	pay_method VARCHAR(50) REFERENCES pay_methods(pay_method) NOT NULL DEFAULT 'CASH',
	pay_status STATUS_TYPE NOT NULL DEFAULT 'COMPLETE', 
	is_income BOOLEAN DEFAULT FALSE,
	reimburses INTEGER REFERENCES transactions(t_id) DEFAULT 0,
	posted_date VARCHAR(12) DEFAULT CURRENT_DATE,
	notes VARCHAR(1024) DEFAULT NULL
);

ALTER TABLE TRANSACTIONS ADD COLUMN true_id VARCHAR NOT NULL DEFAULT '1'
ALTER TABLE transactions ADD constraint transactions_unique_t_id UNIQUE (t_id);
SELECT TRUE_ID FROM TRANSACTIONS T ORDER BY TRUE_ID 
SELECT TRUE_ID FROM TRANSACTION_KEYS T ORDER BY TRUE_ID 

SELECT * 
  FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
 WHERE TABLE_NAME = 'transactions'

SELECT * 
  FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
 WHERE TABLE_NAME = 'transaction_keys'

 
ALTER TABLE TRANSACTIONS DROP CONSTRAINT "transactions_reimburses_fkey";
ALTER TABLE TRANSACTION_KEYS DROP CONSTRAINT "fk12nif4vswq2lmt57akynhg6qk"; 
ALTER TABLE TRANSACTION_KEYS DROP CONSTRAINT "fklno3sgnl95vcvlbe0ooxtka9y";
ALTER TABLE TRANSACTIONS DROP CONSTRAINT "transactions_pkey";
ALTER TABLE TRANSACTION_KEYS DROP CONSTRAINT "transaction_keys_pkey";

TRUNCATE TABLE TRANSACTION_KEYS;


SELECT * FROM TRANSACTIONS T ORDER BY T_ID 
UPDATE TRANSACTIONS 
SET REIMBURSES=500730621 WHERE REIMBURSES=0;
 

 ALTER TABLE TRANSACTIONS ADD FOREIGN KEY 
(reimburses) REFERENCES transactions(true_id);


ALTER TABLE TRANSACTIONS
ADD PRIMARY KEY (true_id);

ALTER TABLE TRANSACTIONS
ADD PRIMARY KEY (t_id);

alter table transaction_keys add constraint FK12nif4vswq2lmt57akynhg6qk foreign key (true_id) references transactions

ALTER TABLE transactions ADD constraint transactions_unique_t_id UNIQUE (t_id);

ALTER TABLE TRANSACTIONS ALTER COLUMN reimburses SET DEFAULT 500730621   

ALTER TABLE TRANSACTIONS ALTER COLUMN true_id DROP DEFAULT;
ALTER TABLE TRANSACTIONS ALTER COLUMN true_id TYPE NUMERIC USING (true_id::NUMERIC);
ALTER TABLE TRANSACTION_KEYS ALTER COLUMN true_id TYPE NUMERIC USING (true_id::NUMERIC);
ALTER TABLE TRANSACTIONS ALTER COLUMN reimburses TYPE NUMERIC USING (reimburses::NUMERIC);

---

ALTER TABLE TRANSACTIONS DROP CONSTRAINT "transactions_pay_method_fkey";



-- User Accounts table
DROP TABLE IF EXISTS user_accounts
CREATE TABLE user_accounts (
u_id VARCHAR not null, auth_token VARCHAR,
created_date VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE,
deactivated_date VARCHAR(12), 
email VARCHAR UNIQUE NOT NULL,
last_login_date VARCHAR(12) NOT NULL DEFAULT CURRENT_DATE,
password VARCHAR NOT NULL,
primary key (u_id)
)

INSERT INTO USER_ACCOUNTS (user_id, email, password_) VALUES ('20230303JackHenryWelsh@gmail.com', 'JackHenryWelsh@gmail.com', 'password' )

ALTER TABLE USER_ACCOUNTS
RENAME COLUMN u_id TO user_id;

SELECT * FROM USER_ACCOUNTS; 
DROP TABLE USER_ACCOUNTS;

alter table transactions add column user_id VARCHAR not NULL DEFAULT '20230303JackHenryWelsh@gmail.com'
alter table transactions add constraint FKp4k31sc1tp36lh34mk6f8x3b3 foreign key (user_id) references user_accounts

alter table transactions DROP column user_id 

SELECT * FROM transactions
SELECT * FROM PAY_METHODS PM 
SELECT * FROM PAY_METHOD_KEYS PM 

UPDATE PAY_METHOD_keys PMK
SET key_id=-1 WHERE key_id=0;

SELECT * FROM user_accounts WHERE user_id=UPPER('20230303JackHenryWelsh@gmail.com');


-- Queries
SELECT * FROM SIMPLE_TRANSACTIONS;
SELECT * FROM PAY_METHODS PM ;
SELECT * FROM TRANSACTIONS;
SELECT DISTINCT PAY_METHOD FROM TRANSACTIONS;
SELECT * FROM TRANSACTIONS ORDER BY TIMESTAMP_ DESC;

SELECT * FROM TRANSACTIONS2;

SELECT t.category FROM TRANSACTIONS t GROUP BY CATEGORY;

SELECT * FROM transactions AS t 
WHERE t.purchase_date >= '2021-09-19' AND t.purchase_date < '2021-09-26'
ORDER BY t.purchase_date DESC;

SELECT * FROM transactions AS t ORDER BY t.t_id DESC LIMIT 10 OFFSET 5;

--AGG Queries
STRING_AGG( UNIQUE(CATEGORY), '-')

 


SELECT * FROM TRANSACTIONS T WHERE T_ID = '20211008001';
SELECT * FROM TRANSACTIONS T WHERE BOUGHT_FOR = 'Books';
SELECT VENDOR, SUM(amount) FROM TRANSACTIONS T WHERE CATEGORY = 'Lifestyle'
GROUP BY VENDOR;

SELECT SUM(amount) FROM TRANSACTIONS T WHERE CATEGORY = 'Lifestyle'
GROUP BY CATEGORY ;




-- Clears
DELETE FROM TRANSACTIONS;
DELETE FROM TRANSACTIONS2;
DELETE FROM TRANSACTIONS t WHERE t.t_id>210604001;
SELECT * FROM TRANSACTIONS;

-- Default transaction 0 for referencing reimbursements
INSERT INTO TRANSACTIONS (t_id, PURCHASE_DATE, AMOUNT, VENDOR, category, bought_for, pay_method, pay_status, is_income, reimburses, posted_date, notes) 
					VALUES (0, '2000-01-01', 0, 		'none',		'none', 'PERSONAL', 'none',		'COMPLETE', FALSE, 		0,			'2000-01-01', ''	 );


INSERT INTO TRANSACTIONS (t_id, PURCHASE_DATE, AMOUNT, VENDOR, category, bought_for, pay_method, pay_status, is_income, reimburses, posted_date, notes) 
					VALUES (1, 					'2000-01-02', 0, 	 'sample_v',	'cat', 'PERSONAL', 'none',		'COMPLETE', FALSE, 		0,			'2000-01-01', ''	 );

				
SELECT * FROM TRANSACTIONS T WHERE VENDOR LIKE 'The%'


INSERT INTO USER_ACCOUNTS (user_id, email, password_) VALUES ('20230303JackHenryWelsh@gmail.com', 'JackHenryWelsh@gmail.com', 'password' )

SELECT * FROM user_accounts

--Deletes
DELETE FROM TRANSACTIONS t WHERE t.t_id IN (20220327000)


--- ADD TIMESTAMP to TRANSACTIONS ---

CREATE TABLE TRANSACTIONS_22_3_14 AS (SELECT * FROM TRANSACTIONS);
SELECT * FROM TRANSACTIONS_22_3_14

ALTER TABLE TRANSACTIONS 
ADD timestamp_ TIMESTAMP DEFAULT NOW()


UPDATE TRANSACTIONS T
SET timestamp_ = T.PURCHASE_DATE::timestamp


---------------------------- VENDOR TABLE TOUR ----------------------------

DROP TABLE Vendor CASCADE;

SELECT * FROM VENDORS V;
SELECT * FROM VENDORS V WHERE VENDOR LIKE 'The%';

INSERT INTO VENDORS (cc_id, cc_name, vendor) 
VALUES ('test', 'test id', 'test vendor');

CREATE TABLE temp_vendors AS (SELECT * FROM VENDORS V)
SELECT * FROM temp_vendors

DELETE FROM VENDORS WHERE VENDOR LIKE 'The Jon'

---------------------------- VENDOR MAPPER TABLE TOUR ----------------------------

SELECT * FROM VENDOR_MAPPER
DROP TABLE VENDOR_MAPPER;

ALTER TABLE vendor_mapper v
ADD FOREIGN KEY (local) REFERENCES Persons(PersonID);

INSERT INTO VENDOR_MAPPER (cc_id, credit_card, vendor) 
VALUES ('test id', 'test cc', 'The Henry');

INSERT INTO VENDOR_MAPPER (cc_id, credit_card, vendor) 
VALUES ('test id', 'test 2', 'The Henry');

DELETE FROM FINANCESPRACTICE.vendor_mapper



SELECT DISTINCT Vendor FROM TRANSACTIONS T;


