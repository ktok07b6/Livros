package livros.compiler;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class Scanner
{
	char [] mSqlText;
	String mSqlStr;
	char mCurrentChar;
	int mIndex;
	int mScanStart;
	List mTokens = new ArrayList();

	public Scanner(String sql) {
		sql.replace("\t", " ");
		sql.replace("\r\n", " ");
		sql.replace("\n", " ");
		sql += " ";//this can make parse simpler
		mSqlText = sql.toCharArray();
		mSqlStr = sql;
		mIndex = 0;
	}

	private boolean isNonZeroDigit(char c) {
		return ('1' <= c && c <= '9');
	}

	private boolean isDigit(char c) {
		return ('0' <= c && c <= '9');
	}

	private boolean isAlpha(char c) {
		return ('a' <= c && c <= 'z' ||
				'A' <= c && c <= 'Z');
	}

	private boolean isIdentifierRest(char c) {
		return isAlpha(c) || isDigit(c) || c == '_';
	}

	private boolean isSpecialChar(char c) {
		return 
			c == '(' ||
			c == ')' ||
			c == '*' ||
			c == '+' ||
			c == ',' ||
			c == '-' ||
			c == '.' ||
			c == '/' ||
			c == ':' ||
			c == ';' ||
			c == '<' ||
			c == '>' ||
			c == '=' ||
			c == '_' ||
			c == '?' ||
			c == '|'
			;
	}

	private boolean isSpace(char c) {
		return c == ' ';
	}

	private void scanString() {
		String str = String.valueOf(mCurrentChar);
		while (forward()) {
			if (mCurrentChar == '\'') {
				//str += String.valueOf(mCurrentChar);
				str = str.substring(1, str.length());
				addToken(str, Token.STRING);
				return;
			} else if (mCurrentChar == '\\') { //escape
				forward();
				switch (mCurrentChar) {
				case '\'':	str += "'"; break;
				case 'n':	str += "\n"; break;
				default: //Todo error
				}
			} else {
				str += String.valueOf(mCurrentChar);
			}
		}
	}

	private void scanNumber() {
		do {
			if (!isDigit(mCurrentChar)) {
				mIndex--;
				addToken(mSqlStr.substring(mScanStart, mIndex), Token.NUMBER);
				return;
			}
		} while (forward());
	}

	private void scanIdentifier() {
		do {
			if (!isIdentifierRest(mCurrentChar)) {
				mIndex--;
				addToken(mSqlStr.substring(mScanStart, mIndex), Token.IDENTIFIER);
				return;
			}
		} while (forward());
	}

	private void scanDelimitedIdentifier() {
		if (forward()) {
			if (mCurrentChar == '"') {
				addToken(mSqlStr.substring(mScanStart, mIndex), Token.IDENTIFIER);
				return;
			} else if (!isAlpha(mCurrentChar)) {
				//error
				return;
			}
		}

		while (forward()) {
			if (mCurrentChar == '"') {
				addToken(mSqlStr.substring(mScanStart, mIndex), Token.IDENTIFIER);
				return;
			} else if (!isIdentifierRest(mCurrentChar)) {
				//error
				return;
			}
		}
	}

	private void scanSpecialChar() {
		switch (mCurrentChar) {
		case '\'':
			addToken("'", Token.QUOTE);
			break;
		case '(':
			addToken("(", Token.LPAREN);
			break;
		case ')':
			addToken(")", Token.RPAREN);
			break;
		case '*':
			addToken("*", Token.ASTERISK);
			break;
		case '+':
			addToken("+", Token.PLUS);
			break;
		case ',':
			addToken(",", Token.COMMA);
			break;
		case '-':
			addToken("-", Token.MINUS);
			break;
		case '.':
			if (nextChar() == '.') {
				forward();
				addToken("..", Token.DOUBLE_PERIOD);
			} else {
				addToken(".", Token.PERIOD);
			}
			break;
		case '/':
			addToken("/", Token.SOLIDUS);
			break;
		case ':':
			addToken(":", Token.COLON);
			break;
		case ';':
			addToken(";", Token.SEMICOLON);
			break;
		case '<':
			if (nextChar() == '=') {
				forward();
				addToken("<=", Token.LESS_EQ);
			} else if (nextChar() == '>') {
				forward();
				addToken("<>", Token.NOT_EQUAL);
			} else {
				addToken("<", Token.LESS);
			}
			break;
		case '>':
			if (nextChar() == '=') {
				forward();
				addToken(">=", Token.GREATER_EQ);
			} else {
				addToken(">", Token.GREATER);
			}
			break;
		case '=':
			addToken("=", Token.EQUAL);
			break;
		case '_':
			addToken("_", Token.UNDERSCORE);
			break;
		case '?':
			addToken("?", Token.QUESTION);
			break;
		case '|':
			if (nextChar() == '|') {
				forward();
				addToken("||", Token.CONCAT);
			} else {
				addToken("|", Token.BAR);
			}
			break;
		default:
			Debug.assertTrue(false);
		}
	}

	public List scan() {
		while (forward()) {
			mScanStart = mIndex-1;
			if (mCurrentChar == '\'') {
				scanString();
			} else if (isDigit(mCurrentChar)) {
				scanNumber();
			} else if (isAlpha(mCurrentChar)) {
				scanIdentifier();
			}else if (mCurrentChar == '"') {
				scanDelimitedIdentifier();
			} else if (isSpecialChar(mCurrentChar)) {
				scanSpecialChar();
			} else if (isSpace(mCurrentChar)){
 			} else {
				Log.d("scan error " + mCurrentChar);
			}
		}
		return mTokens;
	}

	boolean forward() {
		if (mIndex < mSqlText.length) { 
			mCurrentChar = mSqlText[mIndex];
			mIndex++;
			return true;
		} else {
			return false;
		}
	}

	char nextChar() {
		if (mIndex < mSqlText.length) { 
			return mSqlText[mIndex];
		} else {
			return ' ';
		}
	}

	void addToken(String s, int id) {
		mTokens.add(new Token(s, id, mScanStart, mIndex));
	}

}
