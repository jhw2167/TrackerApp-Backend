
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
 

--Test table queries


-- DROP and create table vendors
DROP TABLE IF EXISTS vendors CASCADE;
CREATE TABLE vendors (
vendor VARCHAR(50) PRIMARY KEY,
jb_id VARCHAR(100) UNIQUE DEFAULT NULL,
cap1_id VARCHAR(100) UNIQUE DEFAULT NULL,
website VARCHAR(255) DEFAULT NULL,
notes VARCHAR(1024) DEFAULT NULL
);


-- DROP and create table pay_methods
DROP TABLE IF EXISTS pay_methods CASCADE;
CREATE TABLE pay_methods (
pay_method VARCHAR(50) PRIMARY KEY,
institution_type INSTITUTION_TYPE DEFAULT 'SIMPLE',
balance NUMERIC DEFAULT NULL,
credit_line NUMERIC DEFAULT NULL,
cash_back NUMERIC DEFAULT NULL,
description VARCHAR(1023) DEFAULT NULL
);


-- DROP and create table vendors
DROP TABLE IF EXISTS transactions CASCADE;
CREATE TABLE transactions (
	t_id INTEGER PRIMARY KEY,
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


-- Queries
SELECT * FROM SIMPLE_TRANSACTIONS;
SELECT * FROM TRANSACTIONS;

-- Default transaction 0 for referencing reimbursements
INSERT INTO TRANSACTIONS (t_id, PURCHASE_DATE, AMOUNT, VENDOR, category, bought_for, pay_method, pay_status, is_income, reimburses, posted_date, notes) 
					VALUES (0, 2000-01-01, 0, 		'none',		'none', 'PERSONAL', 'none',		'COMPLETE', FALSE, 		0,			2000-01-01, ''	 );

-- Clears
TRUNCATE transactions;

