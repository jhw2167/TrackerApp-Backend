/*
 * Queries for TransactionRepo
 */

-- 0. Select all transactions by TRUEID

SELECT * FROM TRANSACTIONS T WHERE TRUE_ID = ?;

----

-- 1. Select all transactions by User Id
SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS T 
	ON T.TRUE_ID = A.TRUE_ID 
);

----

-- 2. Select all transactions by User Id, order by date

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS T 
	ON T.TRUE_ID = A.TRUE_ID
) ORDER BY T.PURCHASE_DATE DESC;

----


-- 3. Select all transactions by User Id, BETWEEN SPECIFIED DATES

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
WHERE t.purchase_date >= '2022-12-15' AND t.purchase_date < '2022-12-30' ORDER BY t.purchase_date DESC;

----

-- 4. Select all with MATCHING PURCHASE DATE

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
WHERE t.purchase_date='2022-12-30' ORDER BY t.purchase_date DESC;

----


-- 5. COUNT all with MATCHING PURCHASE DATE

SELECT COUNT(*) FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
WHERE t.purchase_date='2022-12-30';

----


-- 6a. SELECT all with BETWEEN START AND END TID

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
ORDER BY t.t_id DESC LIMIT :limit OFFSET :offset;


----


-- 7. SELECT all with LIKE VENDOR NAME

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
WHERE t.vendor LIKE UPPER('MCDONALDS')

----


-- 8/9. INCOME-EXPENSE AGGREGATE QUERY

SELECT * FROM BUILD_INCOME_EXPENSE_SUMMARY('20230303JackHenryWelsh@gmail.com',
		'2022-12-01', '2023-01-01', true);

DROP VIEW USER_TRANSACTIONS 

SELECT * FROM USER_TRANSACTIONS T 
	
DROP FUNCTION build_income_expense_summary(character varying,character varying,character varying,boolean);


CREATE OR REPLACE FUNCTION BUILD_INCOME_EXPENSE_SUMMARY(
	IN user_account_id varchar,
	IN 	start_date varchar,
	IN end_date varchar,
	IN 	income boolean
)    
RETURNS TABLE ( aggregateCol VARCHAR, value NUMERIC, categories TEXT) AS $$
BEGIN
	
	-- CREATE VIEW

EXECUTE format('CREATE VIEW user_transactions AS (
	SELECT TK.USER_ID, T.*
	FROM TRANSACTION_KEYS TK
	INNER JOIN
	TRANSACTIONS T 
	ON TK.TRUE_ID = T.TRUE_ID
	AND TK.USER_ID= %1$L
	AND T.purchase_date >= %2$L 
	AND T.purchase_date < %3$L)', user_account_id, start_date, end_date);

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

SELECT T.category FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
GROUP BY category;

----


-- 11. SELECT all pay method GROUP BY pay method

SELECT T.pay_method FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
GROUP BY pay_method;

----


-- 12. SELECT BOUGHT FOR GROUP BY BOUGHT FOR 

SELECT T.bought_for FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
GROUP BY bought_for;

----


-- 13. SELECT all pay method GROUP BY pay method

SELECT T.pay_status FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
GROUP BY pay_status;


----




























