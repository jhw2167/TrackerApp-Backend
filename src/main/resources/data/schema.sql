
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


-- Updates
UPDATE TRANSACTIONS
SET CATEGORY = 'Snacks'
WHERE CATEGORY = 'Snack';

UPDATE TRANSACTIONS
SET vendor = 'Walmart'
WHERE VENDOR = 'Wal Mart';

UPDATE TRANSACTIONS
SET CATEGORY = 'Returns'
WHERE CATEGORY = 'Retail' AND IS_INCOME = TRUE;

UPDATE TRANSACTIONS
SET vendor = 'Venmo', notes='Graces Reimburse'
WHERE VENDOR = 'Graces Reimburse';

UPDATE TRANSACTIONS
SET CATEGORY='Reimbursement'
WHERE VENDOR = 'Venmo' AND IS_INCOME = TRUE;

UPDATE TRANSACTIONS
SET IS_INCOME =TRUE 
WHERE CATEGORY = 'Income';

UPDATE TRANSACTIONS
SET CATEGORY = 'Lifestyle'
WHERE CATEGORY = 'Lifesyle';


UPDATE TRANSACTIONS
SET BOUGHT_FOR ='DATE'
WHERE BOUGHT_FOR = 'Date';

UPDATE TRANSACTIONS
SET BOUGHT_FOR ='FAMILY'
WHERE BOUGHT_FOR = 'Family';

UPDATE TRANSACTIONS
SET BOUGHT_FOR ='PERSONAL'
WHERE BOUGHT_FOR = 'Personal';

UPDATE TRANSACTIONS
SET BOUGHT_FOR ='GROUP'
WHERE BOUGHT_FOR = 'Group';


UPDATE TRANSACTIONS
SET PAY_METHOD ='JB'
WHERE T_ID = '211008002'; -- AND 1


UPDATE TRANSACTIONS
SET BOUGHT_FOR ='PERSONAL'
WHERE T_ID = '20211227004'; 


UPDATE TRANSACTIONS
SET CATEGORY = 'Domestic'
WHERE CATEGORY = 'Domistic';

UPDATE TRANSACTIONS
SET CATEGORY = 'Car'
WHERE CATEGORY = 'Gas';

UPDATE TRANSACTIONS
SET CATEGORY = 'Lifestyle'
WHERE CATEGORY = 'Amazon';


UPDATE TRANSACTIONS
SET CATEGORY = 'Dinner'
WHERE VENDOR = 'Jaxon Beer Garden';

UPDATE TRANSACTIONS
SET CATEGORY = 'Lifestyle'
WHERE VENDOR = 'HYTOOF';

UPDATE TRANSACTIONS
SET VENDOR = 'At&T Internet'
WHERE VENDOR = 'At&T';


UPDATE TRANSACTIONS
SET VENDOR = 'Del Friscos'
WHERE VENDOR LIKE 'Del Frisco%';


UPDATE TRANSACTIONS
SET VENDOR = 'Yumilicious'
WHERE VENDOR LIKE 'Yum%';


UPDATE TRANSACTIONS
SET VENDOR = 'Original Pancake House', CATEGORY='Breakfast'
WHERE VENDOR LIKE '%Pancake%';

UPDATE TRANSACTIONS
SET CATEGORY='Local Travel'
WHERE VENDOR LIKE 'Parking%';


UPDATE TRANSACTIONS
SET CATEGORY='Lunch'
WHERE VENDOR LIKE 'Press Waffles';


UPDATE TRANSACTIONS
SET CATEGORY='Dinner'
WHERE VENDOR LIKE 'Raising Canes';

UPDATE TRANSACTIONS
SET CATEGORY='Terry Blacks'
WHERE VENDOR LIKE 'Terry Black%';

UPDATE TRANSACTIONS
SET VENDOR='Shell'
WHERE VENDOR LIKE 'Shell Oil';



SELECT * FROM TRANSACTIONS T 
WHERE VENDOR LIKE '%Pancake%';

UPDATE TRANSACTIONS T
SET VENDOR = a.capVend
FROM (
		SELECT t_id, INITCAP(vendor) AS capVend FROM TRANSACTIONS
) a
WHERE a.T_ID = T.T_ID;

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

INSERT INTO VENDORS (cc_id, cc_name, vendor) VALUES ('test', 'test id', 'test vendor');


-- Select Useful Vendor information 

-- MUST BE TWO tables cc_id - name - cc | Name - amt - isIncome - Category

SELECT DISTINCT vendor FROM TRANSACTIONS T;

DROP VIEW inc_vends cascade;
DROP VIEW no_inc_vends;
DROP VIEW BASE_INC;
DROP VIEW cat_select;

CREATE VIEW base_inc AS SELECT DISTINCT vendor AS v, 0 AS amt, 0 AS no_inc, 0 AS inc FROM TRANSACTIONS T;

--Now we must choose most frequent category with vendor
CREATE VIEW cat_select AS SELECT * FROM (
	SELECT CATEGORY, vendor, IS_INCOME, cnt, 
		RANK() OVER (PARTITION BY VENDOR, IS_INCOME, CATEGORY 
					ORDER BY cnt DESC) AS rn
		FROM (
		SELECT VENDOR, CATEGORY, IS_INCOME, COUNT(*) AS cnt 
		FROM TRANSACTIONS T
		GROUP BY CATEGORY, VENDOR, IS_INCOME ) mainSubQ 
	) outerSubQ
	WHERE outerSubQ.rn = 1;
		-- we use rank and then select where rank = 1

SELECT * FROM cat_select;


CREATE VIEW inc_vends AS
(SELECT vendor AS vendor, PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY AMOUNT) AS amt, COUNT(*) AS inc FROM TRANSACTIONS T
	WHERE IS_INCOME=true
	GROUP BY VENDOR);

CREATE VIEW no_inc_vends AS SELECT vendor AS vendor, PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY AMOUNT) AS amt, COUNT(*) AS no_inc FROM TRANSACTIONS T
WHERE IS_INCOME=false
GROUP BY VENDOR;

SELECT * FROM inc_vends;
SELECT * FROM no_inc_vends;

-- Append all vendors to inc list 
SELECT * FROM inc_vends UNION (SELECT v, amt, 
inc FROM base_inc
WHERE v NOT IN (
	SELECT vendor 
	FROM inc_vends))
ORDER BY vendor;

-- Append all vendors to no_inc list 
SELECT * FROM no_inc_vends UNION (SELECT v, amt, 
no_inc FROM base_inc
WHERE v NOT IN (
	SELECT vendor 
	FROM no_inc_vends))
ORDER BY vendor;


DROP VIEW v3 CASCADE;
CREATE VIEW v3 AS SELECT v1.vendor, v1.amt AS inc, v2.amt AS spnt,
v1.inc AS cnt_inc, v2.no_inc AS cnt_no_inc
FROM (
	(SELECT * FROM inc_vends UNION 
	(SELECT v, amt, inc
	FROM base_inc
	WHERE v NOT IN (
		SELECT vendor 
		FROM inc_vends))) v1 
		
INNER JOIN (
-- Append all vendors to no_inc list 
SELECT * FROM no_inc_vends UNION (SELECT v, amt, 
no_inc FROM base_inc
WHERE v NOT IN (
	SELECT vendor 
	FROM no_inc_vends))) v2 ON v1.vendor=v2.vendor 

);

SELECT * FROM v3;

SELECT * --v3.vendor, category, (CASE WHEN cnt_inc > cnt_no_inc THEN inc ELSE spnt END) AS amount, (cnt_inc > cnt_no_inc) AS is_typically_inc
FROM v3 
INNER JOIN
cat_select AS cs ON v3.VENDOR=cs.vendor
WHERE (cnt_inc > cnt_no_inc AND is_income) OR (cnt_inc <= cnt_no_inc AND NOT is_income)
ORDER BY v3.VENDOR;

SELECT * FROM VENDORS V 
-- After this one its just select:
	-- vendor name, amount if cnt is more, words if count is more

-- We have:
	-- list of vendors and amount spent 
	-- list of vendors and amount received, via income

	-- add all distinct vendors to each list with def. values
	-- inner join on vendor
	-- SELECT inc or spnt based on which had higher count
	-- Get most frequently occuring string per vendor 



--- CREATE TRIGGER FOR ADJUSTING VENDORS TABLE ---

	-- first write function
DROP table test;
CREATE TABLE test(
myBool varchar(255));

INSERT INTO test VALUES ('test');
SELECT * FROM test;
SELECT * FROM VENDORS V ;
DELETE FROM test;

SELECT vendor FROM TRANSACTIONS T ORDER BY timestamp_ DESC LIMIT 1;

DROP FUNCTION IF EXISTS update_vendors CASCADE;
CREATE FUNCTION update_vendors()
	RETURNS trigger AS
	$$
	BEGIN 
		DROP VIEW IF EXISTS upd CASCADE;
		CREATE VIEW upd AS SELECT vendor AS val 
			FROM TRANSACTIONS T 
			ORDER BY timestamp_
			DESC LIMIT 200;
	
	-- DROP PREVIOUS VIEWS	
		DROP VIEW IF EXISTS upd_vendor CASCADE;
		DROP VIEW IF EXISTS BASE_INC cascade;
		DROP VIEW  IF EXISTS cat_select cascade;
		DROP VIEW  IF EXISTS inc_vends cascade;
		DROP VIEW  IF EXISTS no_inc_vends cascade;

	-- GET BASE VALUES
		CREATE VIEW base_inc AS SELECT DISTINCT vendor AS v, 0 AS amt, 0 AS no_inc, 0 AS inc
		FROM TRANSACTIONS T
		WHERE VENDOR = (SELECT val FROM upd);
	
	-- GET CATEGORIES 
	CREATE VIEW cat_select AS SELECT * FROM (
		SELECT CATEGORY, vendor, IS_INCOME, cnt, 
			RANK() OVER (PARTITION BY VENDOR, IS_INCOME 
						ORDER BY cnt DESC) AS rn
			FROM (
			SELECT VENDOR, CATEGORY, IS_INCOME, COUNT(*) AS cnt 
			FROM TRANSACTIONS T
			WHERE VENDOR = (SELECT val FROM upd)
			GROUP BY CATEGORY, VENDOR, IS_INCOME
			) mainSubQ 
		) outerSubQ
		WHERE outerSubQ.rn = 1;
	
		-- CREATE VIEW OF INCOME VENDORS AND NON-INCOME VENDORS 
		CREATE VIEW inc_vends AS
		(SELECT vendor AS vendor, PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY AMOUNT) AS amt, COUNT(*) AS inc
			FROM TRANSACTIONS T
			WHERE IS_INCOME=true
			GROUP BY VENDOR);
		
		CREATE VIEW no_inc_vends AS
		SELECT vendor AS vendor, PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY AMOUNT) AS amt, COUNT(*) AS no_inc
		FROM TRANSACTIONS T
		WHERE IS_INCOME=false
		GROUP BY VENDOR;
		
		-- CREATE VIEW OF MERGED VENDORS
		CREATE VIEW v3 AS SELECT v1.vendor, v1.amt AS inc, v2.amt AS spnt,
		v1.inc AS cnt_inc, v2.no_inc AS cnt_no_inc
		FROM (
			(SELECT * FROM inc_vends UNION 
			(SELECT v, amt, inc
			FROM base_inc
			WHERE v NOT IN (
				SELECT vendor 
				FROM inc_vends))) v1 
				
		INNER JOIN (
		-- Append all vendors to no_inc list 
		SELECT * FROM no_inc_vends UNION (SELECT v, amt, 
		no_inc FROM base_inc
		WHERE v NOT IN (
			SELECT vendor 
			FROM no_inc_vends))) v2 ON v1.vendor=v2.vendor 
		
		);
		
	-- SELECT RESULTS INTO VENDORS TABLE
	CREATE VIEW upd_vend AS (SELECT v3.vendor AS vendor, category,
			 (CASE WHEN cnt_inc > cnt_no_inc THEN inc ELSE spnt END) AS amount,
			 (cnt_inc > cnt_no_inc) AS is_typically_inc
		FROM v3 
		INNER JOIN
		cat_select AS cs ON v3.VENDOR=cs.vendor
		WHERE (cnt_inc > cnt_no_inc AND is_income) OR (cnt_inc <= cnt_no_inc AND NOT is_income)
		ORDER BY v3.VENDOR);
	
	IF EXISTS (SELECT * FROM VENDORS V WHERE VENDOR=(SELECT val FROM upd))
		THEN
			UPDATE VENDORS 
			SET vendor=upd_vend.vendor, 
				AMOUNT=upd_vend.amount,
				CATEGORY=upd_vend.category,
				IS_TYPICALLY_INCOME=upd_vend.is_typically_inc
			FROM upd_vend
			WHERE VENDORS.vendor=upd_vend.vendor;
		ELSE 
			INSERT INTO VENDORS (VENDOR, AMOUNT, CATEGORY, IS_TYPICALLY_INCOME)
			SELECT DISTINCT upd_vend.vendor, upd_vend.amount, upd_vend.category, upd_vend.is_typically_inc
			FROM upd_vend;
		END IF;
	
		RETURN NULL;
	END
	$$ LANGUAGE plpgsql;

SELECT * FROM UPD_vend
SELECT DISTINCT * FROM VENDORS V

DROP TRIGGER VEND_UPD_ON_TRANS_INS;
---------------- END TRIGGER ----------------

/*
	CREATE VIEW cat_select AS SELECT * FROM (
		SELECT CATEGORY, vendor, IS_INCOME, cnt, 
			RANK() OVER (PARTITION BY VENDOR, IS_INCOME 
						ORDER BY cnt DESC) AS rn
			FROM (
			SELECT VENDOR, CATEGORY, IS_INCOME, COUNT(*) AS cnt 
			FROM TRANSACTIONS T
			GROUP BY CATEGORY, VENDOR, IS_INCOME
			) mainSubQ 
		) outerSubQ
		WHERE outerSubQ.rn = 1;
	*/












