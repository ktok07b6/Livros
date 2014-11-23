package livros.compiler;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.List;

public class Parser
{
	List mTokens;
	int mIndex;
	public Parser(List tokens) {
		mTokens = tokens;
		mIndex = 0;
	}

	private String currentParsing() {
		if (!eof()) {
			Token t = (Token)mTokens.get(mIndex);
			return t.rawText();
		} else {
			return "";
		}
	}

	public AST parse() {
		try {
			SelectStatement selectStm = selectStatement();
			if (selectStm != null) {
				return selectStm;
			}

			InsertStatement insertStm = insertStatement();
			if (insertStm != null) {
				return insertStm;
			}

			UpdateStatement updateStm = updateStatement();
			if (updateStm != null) {
				return updateStm;
			}

			DeleteStatement deleteStm = deleteStatement();
			if (deleteStm != null) {
				return deleteStm;
			}

			TableDefinition tableDef = tableDefinition();
			if (tableDef != null) {
				return tableDef;
			}

			DropTableStatement dropTable = dropTableStatement();
			if (dropTable != null) {
				return dropTable;
			}

			CommitStatement commit = commitStatement();
			if (commit != null) {
				return commit;
			}
		} catch (ParseException ex) {
			Log.e(ex.toString());
			if (!eof()) {
				Token t = (Token)mTokens.get(mIndex);
				Log.e(t.toString());
			}
			ex.printStackTrace();
		}

		return null;
	}

	boolean eof() {
		return mIndex >= mTokens.size();
	}

	boolean keyword(String s) {
		if (eof()) {
			return false;
		}
		Token t = (Token)mTokens.get(mIndex);
		
		if (t.is(s)) {
			mIndex++;
			return true;
		} else {
			return false;
		}
	}

	boolean symbol(int id) {
		if (eof()) {
			return false;
		}
		Token t = (Token)mTokens.get(mIndex);
		if (t.is(id)) {
			mIndex++;
			return true;
		} else {
			return false;
		}
	}

	Integer number() {
		if (eof()) {
			return null;
		}
		Token t = (Token)mTokens.get(mIndex);
		if (t.is(Token.NUMBER)) {
			mIndex++;
			return new Integer(t.rawText());
		} else {
			return null;
		}
	}

	String string() {
		if (eof()) {
			return null;
		}

		Token t = (Token)mTokens.get(mIndex);
		if (t.is(Token.STRING)) {
			mIndex++;
			return t.rawText();
		} else
			return null;
	}

	Identifier identifier(int kind) {
		if (eof()) {
			return null;
		}
		
		Token t = (Token)mTokens.get(mIndex);
		/*
		if (t.isKeyword()) {
			return null;
		}
		*/
		if (t.is(Token.IDENTIFIER)) {
			mIndex++;
			return new Identifier(kind, t.rawText());
		} else
			return null;
	}

	/*
	  <table definition> ::= CREATE TABLE <table name> <table element list> 
	*/
	TableDefinition tableDefinition() throws ParseException {
		if (!(keyword("CREATE") && keyword("TABLE"))) {
			return null;
		}

		Identifier name = identifier(Identifier.TABLE_NAME);
		if (name == null) {
			throw new ParseException("table name is expected.");
		}
		List elems = tableElementList();
		if (elems == null) {
			throw new ParseException("table element list is expected.");
		}
		return new TableDefinition(name, elems);
	}

	/*
	  <table element list> ::= <left paren> <table element> [ { <comma> <table element> }... ] <right paren>
	*/
	List tableElementList() throws ParseException {
		if (!symbol(Token.LPAREN)) {
			throw new ParseException("'(' is expected.");
		}

		List li = new ArrayList();
		while (true) {
			TableElement te = tableElement();
			if (te == null) {
				throw new ParseException("missing table element.");
			}
			li.add(te);

			if (!symbol(Token.COMMA)) {
				break;
			}
		}

		if (!symbol(Token.RPAREN)) {
			throw new ParseException("')' is expected.");
		}
		return li;
	}

	/*
	  <table element> ::=
	  <column name> <data type> [ <default clause> ] [ <column constraint>... ]
	*/
	TableElement tableElement() throws ParseException {
		Identifier name = identifier(Identifier.COLUMN_NAME);
		if (name == null) {
			return null;
		}

		DataType dtype = dataType();
		if (dtype == null) {
			throw new ParseException("missing data type.");
		}

		DefaultClause def = defaultClause();

		List li = new ArrayList();
		while (true) {
			Constraint c = constraint();
			if (c == null) {
				break;
			}
			li.add(c);
		}

		return new TableElement(name, dtype, def, li);
	}

	/*
	  <data type> ::= <numeric type> | <character string type>

	  <numeric type> ::= INTEGER | INT

	  <character string type> ::=
	  CHARACTER [ <char length> ]
	  |	CHAR [ <char length> ]
	  |	VARCHAR [ <char length> ]
	*/
	DataType dataType() throws ParseException {
		if (keyword("INT") || keyword("INTEGER")) {
			return new DataType(DataType.INTEGER_TYPE, 0);
		}

		if (keyword("CHARACTER") || keyword("CHAR")) {
			Integer n = charLength();
			if (n != null) {
				return new DataType(DataType.CHAR_TYPE, n.intValue());
			} else {
				return new DataType(DataType.CHAR_TYPE, DataType.DEFAULT_CHAR_LENGTH);
			}
		}

		if (keyword("VARCHAR")) {
			Integer n = charLength();
			if (n != null) {
				return new DataType(DataType.VARCHAR_TYPE, n.intValue());
			} else {
				return new DataType(DataType.CHAR_TYPE, DataType.DEFAULT_CHAR_LENGTH);
			}
		}

		return null;
	}

	/*
	 <char length> := <left paren> <length> <right paren>
	*/
	Integer charLength() throws ParseException {
		if (!symbol(Token.LPAREN)) {
			throw new ParseException("'(' is expected.");
		}

		Integer n = number();
		if (n == null) {
			throw new ParseException("number is expected.");
		}
		if (!symbol(Token.RPAREN)) {
			throw new ParseException("')' is expected.");
		}
		return n;
	}

	/*
	  <default clause> ::= DEFAULT <default option>

	  <default option> ::= <literal> | NULL
	*/
	DefaultClause defaultClause() throws ParseException {
		if (!keyword("DEFAULT")) {
			return null;
		}
		if (keyword("NULL")) {
			return new DefaultClause(null);
		}
		ASTExpr lit = literal();
		if (lit == null) {
			throw new ParseException("literal is expected.");
		}
		return new DefaultClause(lit);
	}

	/*
	  <column constraint> ::=
	  NOT NULL
	  |	<unique specification>
	  |	<references specification>
	  |	<check constraint definition>

	  <unique specification> ::= UNIQUE | PRIMARY KEY

	  <references specification> ::=
	  REFERENCES <table name> <left paren> <column name list> <right paren>

	  <check constraint definition> ::= CHECK <left paren> <search condition> <right paren>
	*/
	Constraint constraint() throws ParseException {
		if (keyword("NOT")) {
			if (!keyword("NULL")) {
				throw new ParseException("'NULL' keyword is expected.");
			}
			return new NotNullConstraint();
		}

		if (keyword("UNIQUE")) {
			return new UniqueConstraint(false);
		}

		if (keyword("PRIMARY")) {
			if (!keyword("KEY")) {
				throw new ParseException("'KEY' keyword is expected.");
			}
			return new UniqueConstraint(true);
		}

		if (keyword("REFERENCES")) {
			Identifier tabname = identifier(Identifier.TABLE_NAME);
			if (tabname == null) {
				throw new ParseException("table name is expected.");
			}
			if (!symbol(Token.LPAREN)) {
				throw new ParseException("'(' is expected.");
			}
			Identifier column = identifier(Identifier.COLUMN_NAME);
			if (!symbol(Token.RPAREN)) {
				throw new ParseException("')' is expected");
			}

			/*
			List columns = new ArrayList();
			while (true) {
				Identifier column = identifier(Identifier.COLUMN_NAME);
				if (column == null) {
					throw new ParseException("column name is expected.");
				}
				columns.add(column);
				if (!symbol(Token.COMMA)) {
					if (symbol(Token.RPAREN)) {
						break;
					}
					throw new ParseException("')' is expected");
				}
			}
			*/
			return new ReferenceConstraint(tabname, column);
		}
		return null;
	}

	/*
	  <literal> ::= <signed numeric literal> | <character string literal>
	*/
	ASTExpr literal() {
		Integer n = integer();
		if (n != null) {
			return new IntegerExpr(n.intValue());
		}

		String s = string();
		if (s != null) {
			return new StringExpr(s);
		}
		
		return null;
	}

	/*
	  <signed numeric literal> ::= [ <sign> ] <unsigned numeric literal>
	*/
	Integer integer() {
		int tag = mIndex;
		if (symbol(Token.PLUS)) {
			Integer n = number();
			if (n != null) {
				return new Integer(n.intValue());
			}
		} else if (symbol(Token.MINUS)) {
			Integer n = number();
			if (n != null) {
				return new Integer(-n.intValue());
			}
		}
		Integer n = number();
		if (n != null) {
			return new Integer(n.intValue());
		}
		mIndex = tag;
		return null;
	}

	SelectStatement selectStatement() throws ParseException {
		int tag = mIndex;
		QueryExpr query = querySpecification();
		if (query == null) {
			return null;
		}
		//TODO: OrderByClause orderBy = orderByClause();
		//return new SelectStatement(query, orderBy);
		return new SelectStatement(query, null);
	}

	/*
	  <insert statement> ::= INSERT INTO <table name> <insert columns and source>

	  <insert columns and source> ::=
	  [ <left paren> <insert column list> <right paren> ] <query expression>
	  |	DEFAULT VALUES
	 */
	InsertStatement insertStatement() throws ParseException {
		int tag = mIndex;
		if (!keyword("INSERT")) {
			return null;
		}
		if (!keyword("INTO")) {
			mIndex = tag;
			return null;
		}

		Identifier name = identifier(Identifier.TABLE_NAME);
		if (name == null) {
			throw new ParseException("table name is expected.");
		}
		
		List columns = new ArrayList();
		if (symbol(Token.LPAREN)) {
			while (true) {
				Identifier column = identifier(Identifier.COLUMN_NAME);
				if (column == null) {
					throw new ParseException("column name is expected.");
				}
				columns.add(column);
				if (!symbol(Token.COMMA)) {
					if (symbol(Token.RPAREN)) {
						break;
					}
					throw new ParseException("')' is expected");
				}
			}
		}

		QueryExpr query = queryExp();
		if (query != null) {
			return new InsertStatement(name, columns, query);
		}

		if (keyword("DEFAULT") && keyword("VALUES")) {
			return new InsertStatement(name, columns, null);
		}

		throw new ParseException("missing insert value source");
	}

	/*
	  <table value constructor> ::= 
	  VALUES <left paren> <table value constructor list> <right paren>

	  <table value constructor list> ::= 
	  <row value constructor> [ { <comma> <row value constructor> }... ]
	 */
	ValueConstructor tableValueConstructor() throws ParseException {
		if (!keyword("VALUES")) {
			return null;
		}
		if (!symbol(Token.LPAREN)) {
			throw new ParseException("'(' is expected");
		}

		List values = new ArrayList();
		while (true) {
			ASTExpr val = rowValueCtor();
			if (val == null) {
				throw new ParseException("value constructor is expected");
			}
			values.add(val);
			if (!symbol(Token.COMMA)) {
				if (symbol(Token.RPAREN)) {
					break;
				}
				throw new ParseException("')' is expected");
			}
		}
		return new ValueConstructor(values);
	}

	/*
	  <drop table statement> ::= DROP TABLE <table name> <drop behaviour>
	  <drop behaviour> ::= CASCADE | RESTRICT
	*/
	DropTableStatement dropTableStatement() throws ParseException {
		int tag = mIndex;
		if (!keyword("DROP")) {
			return null;
		}
		if (!keyword("TABLE")) {
			mIndex = tag;
			return null;
		}
		Identifier name = identifier(Identifier.TABLE_NAME);
		if (name == null) {
			throw new ParseException("table name is expected");
		}

		if (keyword("CASCADE")) {
			return new DropTableStatement(name, DropTableStatement.CASCADE);
		} else if (keyword("RESTRICT")) {
			return new DropTableStatement(name, DropTableStatement.RESTRICT);
		} else {
			return new DropTableStatement(name, DropTableStatement.CASCADE);
		}
	}

	/*
	  <delete statement: searched> ::= DELETE FROM <table name> [ WHERE <search condition> ]
	*/
	DeleteStatement deleteStatement() throws ParseException {
		int tag = mIndex;
		if (!keyword("DELETE")) {
			return null;
		}
		if (!keyword("FROM")) {
			mIndex = tag;
			return null;
		}

		Identifier name = identifier(Identifier.TABLE_NAME);
		if (name == null) {
			throw new ParseException("table name is expected");
		}

		if (!keyword("WHERE")) {
			throw new ParseException("where clause is expected");
		}

		ASTExpr cond = searchCondition();
		if (cond == null) {
			throw new ParseException("search condition is expected");
		}
		return new DeleteStatement(name, cond);
	}

	/*
	  <update statement: searched> ::=
	  UPDATE <table name> SET <set clause list> [ WHERE <search condition> ]
	*/
	UpdateStatement updateStatement() throws ParseException {
		if (!keyword("UPDATE")) {
			return null;
		}

		Identifier name = identifier(Identifier.TABLE_NAME);
		if (name == null) {
			throw new ParseException("table name is expected");
		}

		if (!keyword("SET")) {
			throw new ParseException("'SET' keyword is expected");
		}

		List setList = setClauseList();
		Debug.assertTrue(setList != null);

		ASTExpr cond = null;
		if (keyword("WHERE")) {
			cond = searchCondition();
			if (cond == null) {
				throw new ParseException("search condition is expected");
			}
		}

		return new UpdateStatement(name, setList, cond);
	}

	/*
	  <set clause list> ::= <set clause> [ { <comma> <set clause> } ... ]
	*/
	List setClauseList() throws ParseException {
		List sets = new ArrayList();
		while (true) {
			SetClause set = setClause();
			if (set == null) {
				throw new ParseException("set clause is expected");
			}
			sets.add(set);

			if (!symbol(Token.COMMA)) {
				break;
			}
		}
		return sets;
	}

	/*
	  <set clause> ::= <object column> <equals operator> <update source>
	  <object column> ::= <column name>
	*/
	SetClause setClause() throws ParseException {
		Identifier col = identifier(Identifier.COLUMN_NAME);
		if (col == null) {
			return null;
		}
		if (!symbol(Token.EQUAL)) {
			throw new ParseException("'=' is expected");
		}
		ASTExpr source = updateSource();
		if (source == null) {
			throw new ParseException("update source is expected");
		}
		return new SetClause(col, source);
	}

	/*
	  <update source> ::= <value expression> | <null specification> | DEFAULT
	*/
	ASTExpr updateSource() throws ParseException {
		ASTExpr e = valueExp();
		if (e != null) {
			return e;
		}
		if (keyword("NULL")) {
			return ASTExpr.nullExpr();
		}
		if (keyword("DEFAULT")) {
			return ASTExpr.defaultExpr();
		}
		return null;
	}

	/*
	  <search condition> ::=
	  <boolean term>
	  |   <search condition> OR <boolean term>
	*/
	ASTExpr searchCondition() throws ParseException {
		ASTExpr e = booleanTerm();
		e = searchConditionRest(e);
		return e;
	}

	ASTExpr searchConditionRest(ASTExpr lhs) throws ParseException {
		if (keyword("OR")) {
			ASTExpr rhs = booleanTerm();
			if (rhs == null) {
				throw new ParseException("missing expression");
			}
			return searchConditionRest(new BinaryExpr(BinaryExpr.LOGICAL_OR, lhs, rhs));
		}
		return lhs;
	}

	/*
	  <boolean term> ::=
	  <boolean factor>
	  |   <boolean term> AND <boolean factor>
	*/
	ASTExpr booleanTerm() throws ParseException {
		ASTExpr e = booleanFactor();
		e = booleanTermRest(e);
		return e;
	}

	ASTExpr booleanTermRest(ASTExpr lhs) throws ParseException {
		if (keyword("AND")) {
			ASTExpr rhs = booleanTerm();
			if (rhs == null) {
				throw new ParseException("missing expression");
			}
			return booleanTermRest(new BinaryExpr(BinaryExpr.LOGICAL_AND, lhs, rhs));
		}
		return lhs;
	}

	/*
	  <boolean factor> ::= [ NOT ] <boolean test>
	*/
	ASTExpr booleanFactor() throws ParseException {
		int tag = mIndex;
		boolean not = keyword("NOT");
		ASTExpr e = booleanTest();
		if (e == null) {
			mIndex = tag;
			return null;
		}
		return not ? new UnaryExpr(UnaryExpr.LOGICAL_NOT, e) : e;
	}

	/*
	  <boolean test> ::= <boolean primary> [ IS [ NOT ] <truth value> ]
	*/
	ASTExpr booleanTest() throws ParseException {
		ASTExpr e = booleanPrimary();
		if (e == null) {
			return null;
		}
		if (keyword("IS")) {
			boolean not = keyword("NOT");
			int op = not ? BinaryExpr.IS_NOT : BinaryExpr.IS;
 			if (keyword("TRUE")) {
				return new BinaryExpr(op, e, ASTExpr.trueExpr());
			} else if (keyword("FALSE")) {
				return new BinaryExpr(op, e, ASTExpr.falseExpr());
			} else if (keyword("UNKNOWN")) {
				return new BinaryExpr(op, e, ASTExpr.unknownExpr());
			}

		}
		return e;
	}

	/*
	  <boolean primary> ::= <predicate> | <left paren> <search condition> <right paren>
	*/
	ASTExpr booleanPrimary() throws ParseException {
		ASTExpr e = predicate();
		if (e != null) {
			return e;
		}
		if (symbol(Token.LPAREN)) {
			ASTExpr cond = searchCondition();
			if (cond == null) {
				throw new ParseException("search condition is expected");
			}
			if (!symbol(Token.RPAREN)) {
				throw new ParseException("')' is expected");
			}
			return cond;
		}
		return null;
	}

	/*
	  <predicate> ::=
	  <comparison predicate>
	  |   <like predicate>
	*/
	ASTExpr predicate() throws ParseException {
		ASTExpr e = comparisonPredicate();
		if (e != null) {
			return e;
		}
		e = likePredicate();
		if (e != null) {
			return e;
		}
		return null;
	}

	/*
	  <comparison predicate> ::= <row value constructor> <comp op> <row value constructor>
	*/
	ASTExpr comparisonPredicate() throws ParseException {
		int tag = mIndex;
		ASTExpr lhs = rowValueCtor();
		if (lhs == null) {
			return null;
		}
		int op = compOp();
		if (op < 0) {
			mIndex = tag;
			return null;
		}
		ASTExpr rhs = rowValueCtor();
		if (rhs == null) {
			throw new ParseException("missing expression");
		}
		return new BinaryExpr(op, lhs, rhs);
	}

	int compOp() {
		if (symbol(Token.EQUAL)) {
			return BinaryExpr.EQ;
		}
		if (symbol(Token.NOT_EQUAL)) {
			return BinaryExpr.NE;
		}
		if (symbol(Token.LESS)) {
			return BinaryExpr.LT;
		}
		if (symbol(Token.LESS_EQ)) {
			return BinaryExpr.LE;
		}
		if (symbol(Token.GREATER)) {
			return BinaryExpr.GT;
		}
		if (symbol(Token.GREATER_EQ)) {
			return BinaryExpr.GE;
		}
		if (keyword("IS")) {
			if (keyword("NOT")) {
				return BinaryExpr.IS_NOT;
			} else {
				return BinaryExpr.IS;
			}
		}
		return -1;
	}

	/*
	  <row value constructor> ::=
	  <row value constructor element>
	  #	|   <left paren> <row value constructor list> <right paren>
	  |   <subquery>
	*/
	ASTExpr rowValueCtor() throws ParseException {
		ASTExpr e = rowValueCtorElement();
		if (e != null) {
			return e;
		}
		e = subquery();
		if (e != null) {
			return e;
		}
		return null;
	}

	/*
	  <row value constructor element> ::=
	  <value expression>
	  |   <null specification>
	  |   <default specification>
	*/
	ASTExpr rowValueCtorElement() throws ParseException {
		if (keyword("DEFAULT")) {
			return ASTExpr.defaultExpr();
		}
		if (keyword("NULL")) {
			return ASTExpr.nullExpr();
		}
		ASTExpr e = valueExp();
		return e;
	}

	/*
	  <value expression> ::=
	  <numeric value expression>
	  |   <character value expression>
	*/
	ASTExpr valueExp() throws ParseException {
		ASTExpr e = numericValueExp();
		if (e != null) {
			return e;
		}
		e = charValueExp();
		if (e != null) {
			return e;
		}
		return null;
	}

	/*
	  <numeric value expression> ::=
	  <term>
	  |   <numeric value expression> <plus sign> <term>
	  |   <numeric value expression> <minus sign> <term>
	*/
	ASTExpr numericValueExp() throws ParseException {
		ASTExpr e = term();
		e = numericValueExpRest(e);
		if (e != null) {
			return e;
		}
		return null;
	}

	ASTExpr numericValueExpRest(ASTExpr lhs) throws ParseException {
		if (symbol(Token.PLUS)) {
			ASTExpr rhs = term();
			if (rhs == null) {
				throw new ParseException("missing expression");
			}
			return numericValueExpRest(new BinaryExpr(BinaryExpr.ADD, lhs, rhs));
		}
		if (symbol(Token.MINUS)) {
			ASTExpr rhs = term();
			if (rhs == null) {
				throw new ParseException("missing expression");
			}
			return numericValueExpRest(new BinaryExpr(BinaryExpr.SUB, lhs, rhs));
		}
		return lhs;
	}

	/*
	  <term> ::=
	  <factor>
	  |   <term> <asterisk> <factor>
	  |   <term> <solidus> <factor>
	*/
	ASTExpr term() throws ParseException {
		ASTExpr e = factor();
		e = termRest(e);
		if (e != null) {
			return e;
		}
		return null;
	}

	ASTExpr termRest(ASTExpr lhs) throws ParseException {
		if (symbol(Token.ASTERISK)) {
			ASTExpr rhs = factor();
			if (rhs == null) {
				throw new ParseException("missing expression");
			}
			return termRest(new BinaryExpr(BinaryExpr.MUL, lhs, rhs));
		}
		if (symbol(Token.SOLIDUS)) {
			ASTExpr rhs = term();
			if (rhs == null) {
				throw new ParseException("missing expression");
			}
			return termRest(new BinaryExpr(BinaryExpr.DIV, lhs, rhs));
		}
		return lhs;
	}

	/*
	  <factor> ::= [ <sign> ] <value expression primary>
	*/
	ASTExpr factor() throws ParseException {
		boolean negate = false;
		if (symbol(Token.PLUS)) {
		} else if (symbol(Token.MINUS)) {
			negate = true;
		}
		ASTExpr e = valueExpPrimary();
		if (e == null) {
			throw new ParseException("missing expression");
		}
		return negate ? new UnaryExpr(UnaryExpr.MINUS, e) : e;
 	}

	/*
	  <value expression primary> ::=
	  <subquery>
	  |   <left paren> <value expression> <right paren>
	  |   <set function specification>
	  |   <column reference>
      |   <number>
      |   <character String literal>
	*/
	ASTExpr valueExpPrimary() throws ParseException {
		ASTExpr e;
		e = subquery();
		if (e != null) {
			return e;
		}

		if (symbol(Token.LPAREN)) {
			e = valueExp();
			if (e == null) {
				throw new ParseException("missing expression");
			}
			if (!symbol(Token.RPAREN)) {
				throw new ParseException("'(' is expected");
			}
			return e;
		}

		e = setFunction();
		if (e != null) {
			return e;
		}

		e = columnReference();
		if (e != null) {
			return e;
		}

 		Integer i = number();
		if (i != null) {
			return new IntegerExpr(i.intValue());
		}

 		String s = string();
		if (s != null) {
			return new StringExpr(s);
		}


		return null;
	}
	
	/*
	  <column reference> ::= [ <qualifier> <period> ] <column name>
	*/
	ColumnReference columnReference() throws ParseException {
		Identifier name = identifier(Identifier.COLUMN_NAME_REF);
		if (name == null) {
			return null;
		}
		if (symbol(Token.PERIOD)) {
			name.mKind = Identifier.TABLE_NAME;
			Identifier name2 = identifier(Identifier.COLUMN_NAME);
			if (name2 == null) {
				throw new ParseException("identifier is expected");
			}
			return new ColumnReference(name, name2);
		} else {
			return new ColumnReference(null, name);
		}
	}

	/*
	  <set function specification> ::=
	  COUNT <left paren> <asterisk> <right paren>
	  |   <general set function>

	  <general set function> ::=
	  <set function type> <left paren> [ <set quantifier> ] <value expression> <right paren>

	  <set function type> ::= AVG | MAX | MIN | SUM | COUNT

	  <set quantifier> ::= DISTINCT | ALL
	*/
	FuncExpr setFunction() throws ParseException {
		int funcType = -1;
		if (keyword("AVG")) {
			funcType = FuncExpr.AVG;
		} else if (keyword("MAX")) {
			funcType = FuncExpr.MAX;
		} else if (keyword("MIN")) {
			funcType = FuncExpr.MIN;
		} else if (keyword("SUM")) {
			funcType = FuncExpr.SUM;
		} else if (keyword("COUNT")) {
			funcType = FuncExpr.COUNT;
		}

		if (funcType == -1) {
			return null;
		}

		if (!symbol(Token.LPAREN)) {
			throw new ParseException("'(' is expected");
		}
		if (symbol(Token.ASTERISK)) {
			if (funcType == FuncExpr.COUNT) {
				return new FuncExpr(FuncExpr.COUNT, null);
			}
			throw new ParseException("'*' can be used with COUNT set functnion");
		} 

		int quantifier = -1;
		if (keyword("DISTINCT")) {
			quantifier = SetQuantifier.DISTINCT;
		} else if (keyword("ALL")) {
			quantifier = SetQuantifier.ALL;
		}

		ASTExpr e = valueExp();
		if (e == null) {
			throw new ParseException("missing expression");
		}
		if (!symbol(Token.RPAREN)) {
			throw new ParseException("')' is expected");
		}
		return new FuncExpr(funcType, quantifier, e);
	}

	/*
	  <subquery> ::= <left paren> <query expression> <right paren>
	 */
	QueryExpr subquery() throws ParseException {
		int tag = mIndex;
		if (!symbol(Token.LPAREN)) {
			return null;
		}

		QueryExpr e = queryExp();
		if (e == null) {
			mIndex = tag;
			return null;
		}
		if (!symbol(Token.RPAREN)) {
			throw new ParseException("')' is expected");
		}

		return e;
	}

	/*
	  <query expression> ::= <simple table>
	*/
	QueryExpr queryExp() throws ParseException {
		QueryExpr e = simpleTable();
		if (e == null) {
			return null;
		}
		return e;
	}

	/*
	  <simple table> ::=
	  <query specification>
	  |	<table value constructor>
	 */
	QueryExpr simpleTable() throws ParseException {
		QuerySpecification q = querySpecification();
		if (q != null) {
			return q;
		}
		ValueConstructor v = tableValueConstructor();
		if (v != null) {
			return v;
		}
		return null;
	}

	/*
	  <query specification> ::=
	  SELECT [ <set quantifier> ] <select list> <tabl expression>
	  <set quantifier> ::= DISTINCT | ALL
	*/
	QuerySpecification querySpecification() throws ParseException {
		if (!keyword("SELECT")) {
			return null;
		}

		int quantifier = -1;
		if (keyword("DISTINCT")) {
			quantifier = SetQuantifier.DISTINCT;
		} else if (keyword("ALL")) {
			quantifier = SetQuantifier.ALL;
		}

		boolean selectAll = symbol(Token.ASTERISK);
		List selList = null;
		if (!selectAll) {
			selList = selectList();
			Debug.assertTrue(selList != null);
		}
		
		FromClause from = fromClause();
		if (from == null) {
			throw new ParseException("from clause is expected");
		}

		WhereClause where = whereClause();
		GroupByClause groupBy = groupByClause();

		if (selectAll) {
			return new QuerySpecification(quantifier, from, where, groupBy);
		} else {
			return new QuerySpecification(quantifier, selList, from, where, groupBy);
		}
	}

	/*
	  <select list> ::=
	  <asterisk>
	  |	<select column> [ { <comma> <select column> }... ]
	*/
	List selectList() throws ParseException {
		List li = new ArrayList();
		while (true) {
			SelectColumn col = selectColumn();
			if (col == null) {
				throw new ParseException("select column is expected");
			}
			li.add(col);

			if (!symbol(Token.COMMA)) {
				break;
			}
		}
		return li;
	}

	/*
	  <select column> ::= 
	  <qualifier> <period> <asterisk>
	  | <value expression> [ <as clause> ]

	  <as clause> ::= [ AS ] <column name>
	*/
	SelectColumn selectColumn() throws ParseException {
		int tag = mIndex;

		Identifier tableName = identifier(Identifier.TABLE_NAME);
		if (tableName != null) {
			if (symbol(Token.PERIOD) && symbol(Token.ASTERISK)) {
				return new SelectColumn(tableName);
			}
			mIndex = tag;
		}

		ASTExpr col = valueExp();
		if (col != null) {
			tag = mIndex;
			if (keyword("AS")) {
				Identifier asName = identifier(Identifier.COLUMN_ALIAS_DEF);
				if (asName == null) {
					throw new ParseException("identifier is expected");
				}
				return new SelectColumn(col, asName);
			} else {
				Identifier asName = identifier(Identifier.COLUMN_ALIAS_DEF);
				if (asName == null || asName.toString().toUpperCase().equals("FROM")) {
					mIndex = tag;
					return new SelectColumn(col, null);
				} else {
					return new SelectColumn(col, asName);
				}
			}
		}
		return null;
	}

	/*
	  <from clause> ::= FROM <table reference> [ { <comma> <table reference> }... ]
	*/
	FromClause fromClause() throws ParseException {
 		if (!keyword("FROM")) {
			return null;
		}

		List li = new ArrayList();
		while (true) {
			TableReference t = tableReference();
			if (t == null) {
				throw new ParseException("table reference is expected");
			}
			li.add(t);

			if (!symbol(Token.COMMA)) {
				break;
			}
		}
		return new FromClause(li);
	}

	/*
	  <table reference> ::=
	  <table name> [ <correlation specification> ]
	  |   <subquery> <correlation specification>
	*/
	TableReference tableReference() throws ParseException {
		Identifier tableName = identifier(Identifier.TABLE_NAME);
		if (tableName != null) {
			CorrelationSpecification correlation = correlationSpecification();
			return new TableReference(tableName, correlation);
		}

		QueryExpr query = subquery();
		if (query != null) { 
			CorrelationSpecification correlation = correlationSpecification();
			if (correlation != null) {
				throw new ParseException("correlation specification is expected");
			}
			return new TableReference(query, correlation);
		}

		return null;
	}

	/*
	  <correlation specification> ::=
	  [ AS ] <correlation name> [ <left paren> <column name list> <right paren> ]
	*/
	CorrelationSpecification correlationSpecification() throws ParseException {
		if (!keyword("AS")) {
			return null;
		}
		Identifier name = identifier(Identifier.TABLE_ALIAS_DEF);
		if (name == null) {
			throw new ParseException("correlation name is expected");
		}

		List columns = null;
		if (symbol(Token.LPAREN)) {
			columns = new ArrayList();

			while (true) {
				Identifier column = identifier(Identifier.COLUMN_NAME);
				if (column == null) {
					throw new ParseException("identifier is expected");
				}
				columns.add(column);
			
				if (!symbol(Token.COMMA)) {
					break;
				}
			}

			if (!symbol(Token.RPAREN)) {
				throw new ParseException("')' is expected");
			}
		}
		return new CorrelationSpecification(name, columns);
	}

	/*
	  <where clause> ::= WHERE <search condition>
	*/
	WhereClause whereClause() throws ParseException {
		if (!keyword("WHERE")) {
			return null;
		}
		ASTExpr cond = searchCondition();
		if (cond == null) {
			throw new ParseException("search condition is expected");
		}
		
		return new WhereClause(cond);
	}

	/*
	  <group by clause> ::= GROUP BY <grouping column reference list>
	*/
	GroupByClause groupByClause() throws ParseException {
		if (!keyword("GROUP")) {
			return null;
		}
		if (!keyword("BY")) {
			throw new ParseException("'BY' keyword is expected");
		}

		List cols = new ArrayList();
		while (true) {
			ASTExpr col = columnReference();
			if (col == null) {
				throw new ParseException("column reference is expected");
			}
			cols.add(col);

			if (!symbol(Token.COMMA)) {
				break;
			}
		}
		return new GroupByClause(cols);
	}
	
	/*
	  <character value expression> ::= 
		     <value expression primary>
		   | <character value expression> <concatenation operator> <value expression primary>
	*/
	ASTExpr charValueExp() throws ParseException {
		int tag = mIndex;
		ASTExpr e = valueExpPrimary();
		e = charValueExpRest(e);
		if (e == null) {
			mIndex = tag;
			return null;
		}
		return e;
	}
	
	ASTExpr charValueExpRest(ASTExpr lhs) throws ParseException {
		if (symbol(Token.CONCAT)) {
			ASTExpr rhs = valueExpPrimary();
			if (rhs == null) {
				throw new ParseException("expressioni is expected");
			}
			return charValueExpRest(new BinaryExpr(BinaryExpr.CONCAT, lhs, rhs));
		}
		return lhs;
	}

	/*
	  <like predicate> ::= <match value> [ NOT ] LIKE <pattern>
	  <match value> ::= <character value expression>
	  <pattern> ::= <character value expression>
	*/
	ASTExpr likePredicate() throws ParseException {
		int tag = mIndex;

		ASTExpr match = charValueExp();
		if (match == null) {
			return null;
		}

		boolean negate = keyword("NOT");

		if (!keyword("LIKE")) {
			mIndex = tag;
			return null;
		}

		ASTExpr pattern = charValueExp();
		if (pattern == null) {
			throw new ParseException("LIKE pattern is expected");
		}
		
		return new LikePredicate(match, pattern, negate);
	}

	CommitStatement commitStatement() throws ParseException {
		if (keyword("COMMIT")) {
			return new CommitStatement();
		} else {
			return null;
		}
	}
}
