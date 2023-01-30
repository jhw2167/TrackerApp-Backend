-- SQL file for triggers on finances DB


-- Select Useful Vendor information 

-- MUST BE TWO tables cc_id - name - cc | Name - amt - isIncome - Category

SELECT DISTINCT vendor FROM TRANSACTIONS T;

DROP VIEW IF exists cat_select;
DROP VIEW IF exists inc_vends cascade;
DROP VIEW IF exists no_inc_vends;
DROP VIEW IF exists BASE_INC;
DROP TABLE IF exists cs2 cascade;

CREATE VIEW base_inc AS SELECT DISTINCT vendor AS v, 0 AS amt, 0 AS no_inc, 0 AS inc FROM TRANSACTIONS T;

--Now we must choose most frequent category with vendor
CREATE VIEW cat_select AS SELECT * FROM (
	SELECT CATEGORY, vendor, IS_INCOME, cnt, 
		RANK() OVER (PARTITION BY VENDOR, IS_INCOME, CATEGORY 
					ORDER BY cnt DESC ) AS rn
		FROM (
		SELECT VENDOR, CATEGORY, IS_INCOME, COUNT(*) AS cnt 
		FROM TRANSACTIONS T
		GROUP BY CATEGORY, VENDOR, IS_INCOME) mainSubQ 
	) outerSubQ
	WHERE outerSubQ.rn = 1;
		-- we use rank and then select where rank = 1

SELECT * FROM cat_select;
SELECT * FROM vendors2 ORDER BY vendor; 

CREATE table cs2 AS SELECT cs.category, cs.vendor, cs.is_income, cs.cnt, cs.rn FROM
CAT_SELECT CS INNER JOIN VENDORs2 v2
ON (cs.category=UPPER(v2.category) AND cs.vendor=UPPER(v2.vendor)) ORDER BY vendor;

DROP VIEW CAT_SELECT;
CREATE VIEW CAT_SELECT AS SELECT * FROM cs2;
DELETE FROM CAT_SELECT WHERE 
vendor='TACO BELL' OR vendor='MCDONALDS' OR vendor='ONESTOPPARKING';
-- these particular vendors have redundancies, one must be deleted

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


DROP VIEW IF exists v3 CASCADE;
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
	FROM no_inc_vends))) v2 ON v1.vendor=v2.vendor);


SELECT * FROM v3;

SELECT * --v3.vendor, category, (CASE WHEN cnt_inc > cnt_no_inc THEN inc ELSE spnt END) AS amount, (cnt_inc > cnt_no_inc) AS is_typically_inc
FROM v3 
INNER JOIN
cat_select AS cs ON v3.VENDOR=cs.vendor
WHERE (cnt_inc > cnt_no_inc AND is_income) OR (cnt_inc <= cnt_no_inc AND NOT is_income)
ORDER BY v3.VENDOR;


CREATE VIEW v4 AS SELECT v3.vendor, category, CAST((CASE WHEN cnt_inc > cnt_no_inc THEN inc ELSE spnt END) AS NUMERIC) AS amount,
(cnt_inc > cnt_no_inc) AS is_typically_inc
FROM v3 
INNER JOIN
cat_select AS cs ON v3.VENDOR=cs.vendor
WHERE (cnt_inc > cnt_no_inc AND is_income) OR (cnt_inc <= cnt_no_inc AND NOT is_income)
ORDER BY v3.VENDOR;


INSERT INTO VENDORS (AMOUNT,CATEGORY,IS_TYPICALLY_INCOME,VENDOR)
SELECT AMOUNT, CATEGORY, IS_TYPICALLY_INC, VENDOR
FROM v4

SELECT * FROM v4 ORDER BY vendor 
-- After this one its just select:
	-- vendor name, amount if cnt is more, words if count is more

-- We have:
	-- list of vendors and amount spent 
	-- list of vendors and amount received, via income

	-- add all distinct vendors to each list with def. values
	-- inner join on vendor
	-- SELECT inc or spnt based on which had higher count
	-- Get most frequently occuring string per vendor 




INSERT INTO TRANSACTIONS (t_id, PURCHASE_DATE, AMOUNT, VENDOR, category, bought_for, pay_method, pay_status, is_income, reimburses, posted_date, notes) 
VALUES 					(1, '2022-03-15', 		0, 		'7 Eleven',		'none', 'PERSONAL', 'none',		'COMPLETE', FALSE, 		0,			'2000-01-01', ''	 );

DELETE FROM TRANSACTIONS WHERE T_ID =1;


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
	
		-- Convert Fresh Transaction values to capitals for consistency
		UPDATE TRANSACTIONS
		SET CATEGORY = UPPER(CATEGORY),
		BOUGHT_FOR = UPPER(BOUGHT_FOR),
		VENDOR = UPPER(VENDOR),
		PAY_METHOD = UPPER(PAY_METHOD)
		WHERE timestamp_ = (SELECT timestamp_ 
			FROM TRANSACTIONS T 
			ORDER BY timestamp_
			DESC LIMIT 1);	
	
		CREATE VIEW upd AS SELECT VENDOR AS val 
			FROM TRANSACTIONS T 
			ORDER BY timestamp_
			DESC LIMIT 1;

		
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
		WHERE outerSubQ.rn = 1
		LIMIT 1;
	
	
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
	
	--SELECT * FROM upd
	--SELECT * FROM VENDORS v WHERE v.VENDOR LIKE '7%'
	--SELECT * FROM CAT_SELECT CS 
	DO $inner$ BEGIN 
	IF EXISTS (SELECT * FROM VENDORS V INNER JOIN upd u ON v.vendor=u.val)
		THEN
			UPDATE VENDORS 
			SET  AMOUNT=upd_vend.amount,
				CATEGORY=upd_vend.category,
				IS_TYPICALLY_INCOME=upd_vend.is_typically_inc
			FROM upd_vend
			WHERE VENDORS.vendor=upd_vend.vendor;
		ELSE 
			INSERT INTO VENDORS (VENDOR, AMOUNT, CATEGORY, IS_TYPICALLY_INCOME)
			SELECT DISTINCT upd_vend.vendor, upd_vend.amount, upd_vend.category, upd_vend.is_typically_inc
			FROM upd_vend;
		END IF;
	END
	$inner$ language plpgsql;
		RETURN NULL;
	
	
	-- DROP VIEWS	
		DROP VIEW IF EXISTS BASE_INC cascade;
		DROP VIEW  IF EXISTS cat_select cascade;
		DROP VIEW  IF EXISTS no_inc_vends cascade;
		DROP VIEW  IF EXISTS inc_vends cascade;
		DROP VIEW IF EXISTS upd_vendor CASCADE;
		DROP VIEW IF EXISTS upd CASCADE;
		DROP VIEW  IF EXISTS v3 cascade;
	
	END
	$$ LANGUAGE plpgsql;

--Triggers run in alphabetical order, we want this one to run second
DROP TRIGGER IF EXISTS VEND_UPD_ON_TRANS_INS ON TRANSACTIONs;
CREATE TRIGGER VEND_UPD_ON_TRANS_INS AFTER INSERT
ON transactions EXECUTE PROCEDURE update_vendors();

SELECT * FROM TRANSACTIONS T ORDER BY TIMESTAMP_ DESC

SELECT * FROM UPD_vend
SELECT * FROM VENDORS V
WHERE vendor='SAMPLE_V'	--43.97

DELETE FROM TRANSACTIONS 
WHERE vendor='SAMPLE_V';
AND AMOUNT ='6.95'

DROP TRIGGER VEND_UPD_ON_TRANS_INS ON TRANSACTIONs;
---------------- END TRIGGER ----------------


-- Function and Trigger for capitalizing values as they enter DB
DROP FUNCTION IF EXISTS capitalize_trans CASCADE;
CREATE FUNCTION capitalize_trans() RETURNS void AS
	$$
	BEGIN 
		
UPDATE TRANSACTIONS
SET CATEGORY = UPPER(CATEGORY),
BOUGHT_FOR = UPPER(BOUGHT_FOR),
VENDOR = UPPER(VENDOR),
PAY_METHOD = UPPER(PAY_METHOD)
WHERE timestamp_ = (SELECT timestamp_ 
			FROM TRANSACTIONS T 
			ORDER BY timestamp_
			DESC LIMIT 1);	
	END
	$$ LANGUAGE plpgsql;

-- END FUNCTION

-- Decided to add function to existing trigger










