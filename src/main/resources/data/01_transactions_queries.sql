/*
 * Queries for TransactionRepo
 */


-- Select all transactions by User Id
SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS T 
	ON T.TRUE_ID = A.TRUE_ID 
);


-- Select all transactions by User Id, order by date

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS T 
	ON T.TRUE_ID = A.TRUE_ID
) ORDER BY T.PURCHASE_DATE DESC;


-- Select all transactions by User Id, between specified dates

SELECT * FROM 
(
	(SELECT * FROM TRANSACTION_KEYS TK 
	WHERE USER_ID ='20230303JackHenryWelsh@gmail.com') AS A
	JOIN 
	TRANSACTIONS B 
	ON B.TRUE_ID = A.TRUE_ID
) AS T 
WHERE t.purchase_date >= :start AND t.purchase_date < :end ORDER BY t.purchase_date DESC;
