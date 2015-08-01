Livros
======

Livros is simple and fast RDBMS that is implemented in Java.

The Implementation is simple and has only minimal functionality. 
It will help a beginner learn how to work RDBMS.
Also, it has good performance by using B+ Tree based indexing method.



# How to

### build
```
$ ant
$ ant jar
```

### Run as interactive mode
```
$ java -jar livros.jar
```

#Supported SQL

### BNF
```
<SQL program> ::=
  <select statement>
  | <insert statement>
  | <update statement>
  | <delete statement>
  | <table definintion>
  | <drop table statememt>
  | <commit statememt>

<select statement> ::= <query specification>

<query specification> ::=
  SELECT <select list> <from clause> [ <where clause> ]

<select list> ::=
  <asterisk>
  | <select column> [ { <comma> <select column> } ... ]

<select column> ::=
  <value expression> [ <as clause> ]

<as clause> ::= [ AS ] <column name>

<from clause> ::= FROM <table name>

<where clause> ::= WHERE <search condition>

<search condition> ::=
  <boolean term>
  | <search condition> OR <boolean term>

<boolean term> ::=
  <boolean factor>
  | <boolean term> AND <boolean factor>

<boolean factor> ::= [ NOT ] <boolean test>

<boolean test> ::= <boolean primary> [ IS [ NOT ] <truth value> ]

<boolean primary> ::=
  <predicate>
  | <left paren> <search condition> <right paren>

<predicate> ::= <comparison predicate>

<comparison predicate> ::=
  <row value constructor> <comp op> <row value constructor>

<row value constructor> ::=
  <row value constructor element>

<row value constructor element> ::=
  <value expression>
  | <null specification>

<value expression> ::=
  <numeric value expression>

<numeric value expression> ::=
  <term>
  | <numeric value expression> <plus sign> <term>
  | <numeric value expression> <minus sign> <term>

<term> ::=
  <factor>
  | <term> <asterisk> <factor>
  | <term> <solidus> <factor>

<factor> ::= [ <sign> ] <value expression primary>

<value expression primary> ::=
  <left paren> <value expression> <right paren>
  | <column reference>
  | <number>
  | <character string literal>

<column reference> ::= <column name>

<insert statement> ::=
  INSERT INTO <table name> <insert columns and source>

<insert columns and source> ::=
  [ <left paren> <insert column list> <right paren> ] <table value constructor>

<insert column list> ::=
  <column name> [ {<comma> <column name} ... ]

<table value constructor> ::=
  VALUES <left paren> <table value constructor list> <right paren>

<table value constructor list> ::=
  <row value constructor> [ { <comma> <row value constructor> }...]

<update statement> ::=
  UPDATE <table name> SET <set clause list> [ WHERE <search condition>]
  
<set clause list> ::= <set clause> [ { <comma> <set clause> } ... ]

<set clause> ::= <column name> <equals operator> <update source>

<update source> ::= <value expression> | <null specification> | DEFAULT

<delete statement> ::= DELETE FROM <table name> [ WHERE <search condition> ]

<table definintion> ::= CREATE TABLE <table name> <table element list>

<table element list> ::=
  <left paren> <table element> [ { <comma> <table element> }... ] <right paren>
  
<table element> ::=
  <column name> <data type> [ <default clause> ] [ <column constraint>... ]
 
<data type> ::= <numeric type> | <character string type>

<numeric type> ::= INTEGER | INT

<character string type> ::=
  CHARACTER [ <char length> ]
  | CHAR [ <char length> ]
  | VARCHAR [ <char length> ]

<char length> := <left paren> <length> <right paren>

<default clause> ::= DEFAULT <default option>

<default option> ::= <literal> | NULL

<column constraint> ::=
  PRIMARY KEY
  | NOT NULL
  | UNIQUE
  | <references specification>

<references specification> ::=
  REFERENCES <table name> [ <left paren> <column name> <right paren> ]

<drop table statememt> ::= DROP TABLE <table name>

<commit statememt> ::= COMMIT

<column name> ::= <identifier>
```
