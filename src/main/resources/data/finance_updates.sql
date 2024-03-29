-- SQL file for update queries (data adjustments) to finances table data and DBs

--- ADD FKS ---

-- Pay method

ALTER TABLE TRANSACTIONS 
ADD	COLUMN temp_pm varchar(50),
ADD	COLUMN temp_vend varchar(50);

SELECT * INTO TRANSACTIONS_23_1_21 FROM TRANSACTIONS ;

UPDATE TRANSACTIONS 
SET temp_pm=PAY_METHOD, temp_vend=VENDOR;

ALTER TABLE TRANSACTIONS 
DROP COLUMN pay_method, DROP COLUMN vendor CASCADE;

ALTER TABLE TRANSACTIONS 
ADD COLUMN pay_method VARCHAR(50) REFERENCES pay_methods(pay_method) NOT NULL DEFAULT 'CASH',
ADD COLUMN vendor VARCHAR(50) REFERENCES vendors(vendor);

-- Add excess vendors into VENDORS table
SELECT DISTINCT temp_vend FROM TRANSACTIONS T 

INSERT INTO VENDORS 
  (vendor)
SELECT * FROM (
	SELECT DISTINCT temp_vend FROM TRANSACTIONS
	EXCEPT
	SELECT v.VENDOR FROM VENDORS V
) AS foo;


UPDATE TRANSACTIONS 
SET PAY_METHOD=temp_pm, VENDOR=temp_vend;

SELECT * FROM TRANSACTIONS T 

ALTER TABLE TRANSACTIONS 
DROP COLUMN temp_pm, DROP COLUMN TEMP_vend CASCADE;



-- #########	#########	########	#########	########	#########	########

--- COPY DATA BETWEEN TABLES ---

INSERT INTO PAY_METHODS 
  (pay_method)
SELECT DISTINCT PAY_METHOD 
  FROM TRANSACTIONS; 

-- #########	#########	########	#########	########	#########	########

 

--- COPY DATA BETWEEN TABLES ---

INSERT INTO PAY_METHODS 
  (pay_method)
SELECT DISTINCT PAY_METHOD 
  FROM TRANSACTIONS; 

-- #########	#########	########	#########	########	#########	########
 
 
 
--- SEE ALL TYPES GROUPED ---
SELECT t.BOUGHT_FOR FROM TRANSACTIONS t
GROUP BY BOUGHT_FOR 

SELECT t.CATEGORY FROM TRANSACTIONS t
GROUP BY CATEGORY 

-- #########	#########	########	#########	########	#########	########


---------------- Pay Methods UPDATES ----------------

UPDATE USER_ACCOUNTS u
SET USER_ID=(UPPER(u.USER_ID));

UPDATE TRANSACTION_KEYS u
SET USER_ID=(UPPER(u.USER_ID))

UPDATE PAY_METHOD_KEYS u 
SET USER_ID=(UPPER(u.USER_ID))


ALTER TABLE TRANSACTION_KEYS DROP CONSTRAINT "fklno3sgnl95vcvlbe0ooxtka9y" 

-- #########	#########	########	#########	########	#########	########

---------------- Pay Methods UPDATES ----------------
SELECT * FROM TRANSACTIONS T WHERE 
PAY_METHOD = 'PERSONAL'

TRUNCATE PAY_METHODS 
WHERE PAY_METHOD LIKE '%BALANCE%' 
	OR LIKE 'JET%'
	OR LIKE 'PARENT%'
	OR LIKE 'PARENT%'

UPDATE TRANSACTIONS
SET PAY_METHOD = 'AMAZON'
WHERE pay_method LIKE 'AMAZON%';

SELECT * FROM PAY_METHODS PM 
SELECT DISTINCT PAY_METHOD FROM TRANSACTIONS T

-- #########	#########	########	#########	########	#########	########


---------------- VENDORS UPDATES ----------------

SELECT * FROM VENDORS V
DELETE FROM VENDORS 
WHERE vendor LIKE 'TRADER B%';


SELECT * FROM VENDORS V 
WHERE vendor LIKE 'TERRY%';

DELETE FROM VENDORS V
WHERE amount=33.05


-- #########	#########	########	#########	########	#########	########


---------------- TRANSACTIONS UPDATES ----------------


SELECT * FROM  ( (SELECT * FROM TRANSACTION_KEYS TK  WHERE USER_ID ='20230303JACKHENRYWELSH@GMAIL.COM') 
AS A JOIN  TRANSACTIONS T  ON T.TRUE_ID = A.TRUE_ID  AND T.t_id=20221121008)

---

SELECT * FROM PAY_METHODS PM 

---

SELECT * FROM TRANSACTIONS T ORDER BY TIMESTAMP_ DESC 
SELECT * FROM TRANSACTION_KEYS TK ORDER BY key_id DESC 
SELECT * FROM TRANSACTIONS T WHERE T.TRUE_ID=411299872;

---

SELECT COUNT (*) FROM TRANSACTION_KEYS TK
SELECT COUNT (*) FROM TRANSACTIONS TK

SELECT * FROM TRANSACTIONS T LEFT OUTER JOIN TRANSACTION_KEYS TK ON T.TRUE_ID=tk.true_id

SELECT * FROM TRANSACTIONS T ORDER BY TIMESTAMP_ DESC 
WHERE T.TRUE_ID NOT IN (SELECT TK.TRUE_ID FROM TRANSACTIONS TK)

SELECT * FROM TRANSACTIONS T WHERE T.t_id=20221121009;
SELECT * FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=411299872;

SELECT * FROM TRANSACTIONS T WHERE T.t_id=20221121010;
SELECT * FROM TRANSACTIONS T WHERE T.TRUE_ID=411299871;

SELECT * FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=411299870;
SELECT * FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=411299872;

SELECT * FROM TRANSACTION_KEYS T WHERE T.true_id=411299872 OR T.true_id=411299871;

DELETE FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=411299871;
DELETE FROM TRANSACTIONS T WHERE T.TRUE_ID=411299871;


DELETE FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=411299870;
DELETE FROM TRANSACTIONS T WHERE T.TRUE_ID=411299870;
DELETE FROM TRANSACTIONS T WHERE T.TRUE_ID=411299869; 


DELETE FROM TRANSACTION_KEYS T WHERE T.TRUE_ID=411299872;
DELETE FROM TRANSACTIONS T WHERE T.TRUE_ID=411299872;



DELETE FROM TRANSACTIONS T
WHERE AMOUNT =99999

SELECT * FROM TRANSACTIONS T 
WHERE vendor LIKE 'TRADER%';

UPDATE TRANSACTIONS 
SET vendor = 'TERRY BLACKS'
WHERE vendor LIKE 'TERRY BLACK%'; 


UPDATE TRANSACTIONS 
SET PAY_METHOD = 'AMERICAN',
CATEGORY = 'BREAKFAST' 
WHERE pay_method LIKE 'BREAK%';

UPDATE TRANSACTIONS 
SET amount = 97.38
WHERE T_ID = 20221206001;

SELECT * FROM TRANSACTIONS T 
WHERE vendor LIKE 'MAN%';


SELECT * FROM TRANSACTIONS T 
WHERE T_ID = 0;


UPDATE TRANSACTIONS 
SET vendor = 'SHELL'
WHERE vendor LIKE 'SHELL%'; 

UPDATE TRANSACTIONS
SET CATEGORY = 'ROADTRIPLA'
WHERE CATEGORY LIKE 'ROAD%';

UPDATE TRANSACTIONS
SET CATEGORY = 'GIFTS'
WHERE CATEGORY LIKE 'GIFT';

SELECT t.BOUGHT_FOR FROM TRANSACTIONS t
GROUP BY BOUGHT_FOR 

-- Update all transaction values to be upper case
SELECT * FROM TRANSACTIONS t
WHERE
vendor LIKE 'REV%' ORDER BY POSTED_DATE 
WHERE T_ID > '20220701000'; -- AND 1

SELECT * FROM TRANSACTIONS t WHERE t.vendor LIKE UPPER('i%');

SELECT * FROM TRANSACTIONS T 
WHERE vendor LIKE 'DA%';

UPDATE TRANSACTIONS 
SET vendor = UPPER(VENDOR);

UPDATE TRANSACTIONS t
SET amount = amount*2
WHERE
vendor LIKE 'REVATURE' AND
category LIKE 'INCOME';

UPDATE TRANSACTIONS t
SET amount = amount/2
WHERE
vendor LIKE 'REVATURE' AND
category LIKE 'INCOME';


UPDATE TRANSACTIONS 
SET CATEGORY = 'LOCAL TRAVEL'
WHERE vendor LIKE 'Lyft';

UPDATE TRANSACTIONS 
SET CATEGORY = 'DINNER'
WHERE VENDOR = 'JACK BOX';


UPDATE TRANSACTIONS 
SET CATEGORY = 'DINNER'
WHERE CATEGORY = 'DINNNER';

SELECT DISTINCT BOUGHT_FOR FROM TRANSACTIONS T
WHERE CATEGORY ='LIFESTYLE';

SELECT * FROM TRANSACTIONS T 
WHERE CATEGORY LIKE 'LIF%' AND BOUGHT_FOR LIKE 'D%';

UPDATE TRANSACTIONS 
SET PAY_METHOD = 'FAMILY CC'
WHERE PAY_METHOD LIKE 'PAREN%'

UPDATE TRANSACTIONS
SET CATEGORY = UPPER(CATEGORY),
BOUGHT_FOR = UPPER(BOUGHT_FOR),
VENDOR = UPPER(VENDOR),
PAY_METHOD = UPPER(PAY_METHOD);

UPDATE VENDORS
SET CATEGORY = UPPER(CATEGORY),
VENDOR = UPPER(VENDOR);

DELETE FROM VENDORS
WHERE vendor != UPPER(VENDOR);

SELECT * FROM VENDORS
CREATE TABLE VENDORS2 AS SELECT * FROM VENDORS V 
DELETE FROM VENDORS 

SELECT DISTINCT VENDOR FROM TRANSACTIONS T ORDER BY VENDOR

UPDATE TRANSACTIONS
SET vendor = 'WALMART'
WHERE VENDOR LIKE 'WALLMART';
SELECT DISTINCT VENDOR FROM TRANSACTIONS T ORDER BY VENDOR OFFSET 60


UPDATE TRANSACTIONS
SET vendor = 'ORIGINAL PANCAKE HOUSE'
WHERE VENDOR LIKE '%PANCAKE%';
SELECT DISTINCT VENDOR FROM TRANSACTIONS T ORDER BY VENDOR OFFSET 60

UPDATE TRANSACTIONS
SET vendor = 'MLINE TOWER'
WHERE VENDOR LIKE 'M LINE%';
SELECT DISTINCT VENDOR FROM TRANSACTIONS T ORDER BY VENDOR OFFSET 50

UPDATE TRANSACTIONS
SET vendor = 'EIGHTEEN EIGHT'
WHERE VENDOR LIKE 'EIGHTEEN EIGHT%';
SELECT DISTINCT VENDOR FROM TRANSACTIONS T ORDER BY VENDOR OFFSET 40


UPDATE TRANSACTIONS
SET vendor = 'MACYS'
WHERE VENDOR LIKE 'MACY%';

UPDATE TRANSACTIONS
SET vendor = 'EIGHTEEN EIGHT'
WHERE VENDOR LIKE 'EIGHTEEN EIGHT%';


UPDATE TRANSACTIONS
SET vendor = 'HOOTERS'
WHERE VENDOR LIKE 'HOOT%';
SELECT DISTINCT VENDOR FROM TRANSACTIONS T ORDER BY VENDOR OFFSET 40


UPDATE TRANSACTIONS
SET vendor = 'AT&T INTERNET'
WHERE VENDOR LIKE 'AT&T%';


UPDATE TRANSACTIONS 
SET CATEGORY='DINNER',
VENDOR='TERRY BLACKS'
WHERE CATEGORY LIKE 'TERRY%'

UPDATE TRANSACTIONS
SET CATEGORY = 'DINNER'
WHERE CATEGORY = 'DINNNER';

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

---------------- END TRANSACTIONS UPDATES ----------------
