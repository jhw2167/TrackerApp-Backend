-- SQL file for triggers on finances DB


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

CREATE TRIGGER VEND_UPD_ON_TRANS_INS AFTER INSERT
ON transactions EXECUTE PROCEDURE update_vendors();

SELECT * FROM UPD_vend
SELECT * FROM VENDORS V
WHERE vendor='The Jon'	--43.97

DELETE FROM TRANSACTIONS 
WHERE vendor='The Jon'
AND AMOUNT ='6.95'

DROP TRIGGER VEND_UPD_ON_TRANS_INS;
---------------- END TRIGGER ----------------