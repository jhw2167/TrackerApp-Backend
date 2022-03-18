
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
SELECT * FROM TRANSACTIONS2;

SELECT t.category FROM TRANSACTIONS t GROUP BY CATEGORY;

SELECT * FROM transactions AS t 
WHERE t.purchase_date >= '2021-09-19' AND t.purchase_date < '2021-09-26'
ORDER BY t.purchase_date DESC;

SELECT * FROM transactions AS t ORDER BY t.t_id DESC LIMIT 10 OFFSET 5;

--AGG Queries
STRING_AGG( UNIQUE(CATEGORY), '-')

		-- INCOME SUMMARY
SELECT v1, SUM(sum1) / COUNT(cat), STRING_AGG(cat, '/') FROM
( SELECT vendor AS v1, SUM(amount) AS sum1 
FROM TRANSACTIONS t1
WHERE t1.IS_INCOME=TRUE AND 
t1.purchase_date >= '2021-01-31' AND
t1.purchase_date < '2022-03-01'
GROUP BY t1.VENDOR ) AS a
INNER JOIN 
(
SELECT DISTINCT t2.VENDOR AS v2, t2.category AS cat 
FROM TRANSACTIONS t2 
WHERE t2.IS_INCOME =TRUE
) b ON a.v1=b.v2 GROUP BY v1;


SELECT * FROM TRANSACTIONS t 
WHERE t.IS_INCOME=TRUE;



		-- Expense SUMMARY
SELECT c1, SUM(sum1) / COUNT(BOUGHT_FOR) AS value , STRING_AGG(BOUGHT_FOR, '/') FROM
( SELECT CATEGORY AS c1, SUM(amount) AS sum1 
FROM TRANSACTIONS t1
WHERE t1.IS_INCOME=false AND 
t1.purchase_date >= '2021-01-31' AND
t1.purchase_date < '2022-03-01'
GROUP BY t1.CATEGORY ) AS a
INNER JOIN 
(
SELECT DISTINCT t2.CATEGORY AS c2, t2.BOUGHT_FOR 
FROM TRANSACTIONS t2 
WHERE t2.IS_INCOME =false
) b ON a.c1=b.c2 GROUP BY c1 ORDER BY value DESC;
 


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


--Deletes
DELETE FROM TRANSACTIONS t WHERE t.t_id IN ()


--- ADD TIMESTAMP to TRANSACTIONS ---

CREATE TABLE TRANSACTIONS_22_3_14 AS (SELECT * FROM TRANSACTIONS);
SELECT * FROM TRANSACTIONS_22_3_14

ALTER TABLE TRANSACTIONS 
ADD timestamp_ TIMESTAMP DEFAULT NOW()

SELECT * FROM TRANSACTIONS T

UPDATE TRANSACTIONS T
SET timestamp_ = T.PURCHASE_DATE::timestamp


---------------------------- VENDOR TABLE TOUR ----------------------------

DROP TABLE Vendor CASCADE;

SELECT * FROM VENDORS V;

INSERT INTO VENDORS (cc_id, cc_name, vendor) 
VALUES ('test', 'test id', 'test vendor');
















