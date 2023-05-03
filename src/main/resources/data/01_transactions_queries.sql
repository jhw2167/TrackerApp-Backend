/*
 * Queries for TransactionRepo
 */

-- CREATE VIEW FUNCTION

CREATE OR REPLACE FUNCTION create_user_transactions_view(user_view_name TEXT, user_id TEXT) RETURNS BOOLEAN AS $$
BEGIN
  EXECUTE format('CREATE OR REPLACE VIEW %s AS
                  SELECT t.*, p.pay_method
                  FROM transactions t
                  JOIN pay_methods p ON t.pm_id = p.pm_id
                  WHERE t.user_id = %L',
                 user_view_name,
                 user_id);
  RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

DROP FUNCTION create_user_transactions_view(user_view_name TEXT, user_id TEXT)

-- DROP VIEW FUNCTION

CREATE OR REPLACE FUNCTION drop_user_transactions_view(user_view_name TEXT) RETURNS BOOLEAN AS $$
BEGIN
  EXECUTE format('DROP VIEW %s',
                 user_view_name);
  RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

DROP FUNCTION drop_user_transactions_view(user_view_name TEXT)

-- TEST

SELECT * FROM create_user_transactions_view('TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM', 
'20230303JACKHENRYWELSH@GMAIL.COM');

SELECT drop_user_transactions_view('TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM');


SELECT * FROM FINANCES.TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM;


-- 0. Select all transactions by TRUEID

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM T WHERE TRUE_ID = ?;

----

-- 1. Select all transactions by User Id
SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t
WHERE t.user_id = '20230303JACKHENRYWELSH@GMAIL.COM';

DROP FUNCTION find_all_by_user_id;

CREATE OR REPLACE FUNCTION find_all_by_user_id(user_view_name TEXT) 
	RETURNS TABLE (
		t_id 			BIGINT,
		amount 			NUMERIC(10, 2),
		bought_for 		VARCHAR(50),
		category		VARCHAR(50),
		is_income 		BOOLEAN,
		notes 			VARCHAR(1024),
		pay_status 		VARCHAR(50),
		posted_date 	VARCHAR(12),
		purchase_date 	VARCHAR(12),
		timestamp_ 		VARCHAR(1024),
	vendor 			VARCHAR(50),
	true_id			BIGINT,
	reimburses 		BIGINT,
	pm_id			BIGINT,
	user_id 		VARCHAR(1024),
	pay_method 		VARCHAR(50))
	
	AS $$
BEGIN
  RETURN QUERY EXECUTE format('SELECT *
                  FROM %s',
                 user_view_name);
END;
$$ LANGUAGE plpgsql;

SELECT * FROM find_all_by_user_id('TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM');


----

-- 2. Select all transactions by User Id, order by date

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t
ORDER BY T.PURCHASE_DATE DESC;


----


-- 3. Select all transactions by User Id, BETWEEN SPECIFIED DATES

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t 
WHERE t.purchase_date >= '2022-11-21' AND t.purchase_date < '2022-11-22' ORDER BY t.purchase_date DESC;

----

-- 4. Select all with MATCHING PURCHASE DATE

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t 
WHERE t.purchase_date='2022-12-30' ORDER BY t.purchase_date DESC;

----


-- 5. COUNT all with MATCHING PURCHASE DATE

SELECT COUNT(*) FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t 
WHERE t.purchase_date='2022-12-30';

----


-- 6a. SELECT all with BETWEEN START AND END TID

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t  
ORDER BY t.t_id DESC LIMIT 5 OFFSET 0;


----


-- 7. SELECT all with LIKE VENDOR NAME

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t  
WHERE t.vendor LIKE UPPER('MCDONALDS')

----


-- 8/9. INCOME-EXPENSE AGGREGATE QUERY

SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY('TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM',
		'2022-12-01', '2023-01-01', true);

DROP VIEW USER_TRANSACTIONS 

SELECT * FROM USER_TRANSACTIONS T 
	
DROP FUNCTION build_income_expense_summary(character varying,character varying,character varying,boolean);


CREATE OR REPLACE FUNCTION BUILD_INCOME_EXPENSE_SUMMARY(
	IN user_view_name varchar,
	IN 	start_date varchar,
	IN end_date varchar,
	IN 	income boolean
)    
RETURNS TABLE ( aggregateCol VARCHAR, value NUMERIC, categories TEXT) AS $$
BEGIN
	
	-- CREATE VIEW

EXECUTE format('CREATE VIEW user_transactions AS (
	SELECT * FROM %1$s T   
	WHERE T.purchase_date >= %2$L 
	AND T.purchase_date < %3$L)', user_view_name, start_date, end_date);

	-- RUN QUERY
	RETURN QUERY ( SELECT c1 as aggregateCol, SUM(sum1) / COUNT(BOUGHT_FOR) as value,
				STRING_AGG(BOUGHT_FOR, '/') as categories
	FROM
	( SELECT CATEGORY AS c1, SUM(amount) AS sum1
	FROM user_transactions t1
	WHERE t1.IS_INCOME=income
	GROUP BY t1.CATEGORY ) AS a
	INNER JOIN
	(
	SELECT DISTINCT t2.CATEGORY AS c2, t2.BOUGHT_FOR
	FROM user_transactions t2
	WHERE t2.IS_INCOME=income
	) b ON a.c1=b.c2 GROUP BY c1 ORDER BY value DESC);

	
	-- DROP VIEW
	DROP VIEW user_transactions;

RETURN;
END;
$$language plpgsql

----











----


-- 10. SELECT all CATEGORIES GROUP BY CATEGORIES

SELECT T.category FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t 
GROUP BY category;

----


-- 11. SELECT all pay method GROUP BY pay method

SELECT t.pay_method FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t 
GROUP BY pay_method;

----


-- 12. SELECT BOUGHT FOR GROUP BY BOUGHT FOR 

SELECT t.bought_for FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t
GROUP BY bought_for;

----


-- 13. SELECT all pay status GROUP BY pay method

SELECT t.pay_status FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t
GROUP BY pay_status;


----

-- 14. SELECT where true id and userId

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t 
WHERE T.true_Id=684404157;


----

-- 15. SELECT where tid and userId

SELECT * FROM TRANSACTIONS_VIEW_20230303JACKHENRYWELSHGMAILCOM t
WHERE T.t_id=20221121008;

----

--DELETE 
DELETE FROM TRANSACTIONS T WHERE T.t_id=20221121009;
DELETE FROM TRANSACTIONS T WHERE T.t_id=202211210010;



--SELECT 
SELECT * FROM TRANSACTIONS T WHERE T.t_id=20221121009;
SELECT * FROM TRANSACTIONS T WHERE T.t_id=20221121010;

SELECT * FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=684404159;
DELETE FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=684404158;

SELECT * FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=684404158;
DELETE FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=684404158;


---------------- PAY METHOD ------------------

-- 01. SELECT * where userId and tid

SELECT * FROM 
(
	(SELECT PMK.PM_ID AS key_pm_id, USER_ID FROM PAY_METHOD_KEYS PMK 
	WHERE USER_ID ='20230303JACKHENRYWELSH@GMAIL.COM') AS A
	JOIN 
	PAY_METHODS PM 
	ON A.key_pm_id = PM.PM_ID 
) AS M
WHERE M.PM_ID=535871220; 

SELECT * FROM PAY_METHODS PM 
SELECT * FROM PAY_METHOD_KEYS PMK

SELECT * FROM TRANSACTIONS T 


----

---------------- PAY METHOD ------------------
-- 01. SELECT userId from trueId



----





