package livros.compiler;

import livros.Log;

public class Token
{
	public static final int PERCENT = 0;
	public static final int QUOTE = 1;
	public static final int LPAREN = 2;
	public static final int RPAREN = 3;
	public static final int ASTERISK = 4;
	public static final int PLUS = 5;
	public static final int COMMA = 6;
	public static final int MINUS = 7;
	public static final int PERIOD = 8;
	public static final int SOLIDUS = 9;
	public static final int COLON = 10;
	public static final int SEMICOLON = 11;
	public static final int LESS = 12;
	public static final int GREATER = 13;
	public static final int EQUAL = 14;
	public static final int QUESTION = 15;
	public static final int UNDERSCORE = 16;
	public static final int BAR = 17;
	public static final int NOT_EQUAL = 18;
	public static final int LESS_EQ = 19;
	public static final int GREATER_EQ = 20;
	public static final int CONCAT = 21;
	public static final int DOUBLE_PERIOD = 22;

	public static final int STRING = 23;
	public static final int NUMBER = 24;
	public static final int IDENTIFIER = 25;

	public static final int ABSOLUTE = 1000;
	public static final int ACTION = 1001; 
	public static final int ADD = 1002;
    public static final int ALL = 1003;
    public static final int ALLOCATE = 1004;
    public static final int ALTER = 1005;
    public static final int AND = 1006;
    public static final int ANY = 1007;
    public static final int ARE = 1008;
    public static final int AS = 1009;
    public static final int ASC = 1010;
    public static final int ASSERTION = 1011;
    public static final int AT = 1012;
    public static final int AUTHORIZATION = 1013;
    public static final int AVG = 1014;
    public static final int BEGIN = 1015;
    public static final int BETWEEN = 1016;
    public static final int BIT = 1017;
    public static final int BIT_LENGTH = 1018;
    public static final int BOTH = 1019;
    public static final int BY = 1020;
    public static final int CASCADE = 1021;
    public static final int CASCADED = 1022;
    public static final int CASE = 1023;
    public static final int CAST = 1024;
    public static final int CATALOG = 1025;
    public static final int CHAR = 1026;
    public static final int CHARACTER = 1027;
    public static final int CHARACTER_LENGTH = 1028;
    public static final int CHAR_LENGTH = 1029;
    public static final int CHECK = 1030;
    public static final int CLOSE = 1031;
    public static final int COALESCE = 1032;
    public static final int COLLATE = 1033;
    public static final int COLLATION = 1034;
    public static final int COLUMN = 1035;
    public static final int COMMIT = 1036;
    public static final int CONNECT = 1037;
    public static final int CONNECTION = 1038;
    public static final int CONSTRAINT = 1039;
    public static final int CONSTRAINTS = 1040;
    public static final int CONTINUE = 1041;
    public static final int CONVERT = 1042;
    public static final int CORRESPONDING = 1043;
    public static final int CREATE = 1044;
    public static final int CROSS = 1045;
    public static final int CURRENT = 1046;
    public static final int CURRENT_DATE = 1047;
    public static final int CURRENT_TIME = 1048;
    public static final int CURRENT_TIMESTAMP = 1049;
    public static final int CURRENT_USER = 1050;
    public static final int CURSOR = 1051;
    public static final int DATE = 1052;
    public static final int DAY = 1053;
    public static final int DEALLOCATE = 1054;
    public static final int DEC = 1055;
    public static final int DECIMAL = 1056;
    public static final int DECLARE = 1057;
    public static final int DEFAULT = 1058;
    public static final int DEFERRABLE = 1059;
    public static final int DEFERRED = 1060;
    public static final int DELETE = 1061;
    public static final int DESC = 1062;
    public static final int DESCRIBE = 1063;
    public static final int DESCRIPTOR = 1064;
    public static final int DIAGNOSTICS = 1065;
    public static final int DISCONNECT = 1066;
    public static final int DISTINCT = 1067;
    public static final int DOMAIN = 1068;
    public static final int DOUBLE = 1069;
    public static final int DROP = 1070;
    public static final int ELSE = 1071;
    public static final int END = 1072;
    public static final int END_EXEC = 1073;
    public static final int ESCAPE = 1074;
    public static final int EXCEPT = 1075;
    public static final int EXCEPTION = 1076;
    public static final int EXEC = 1077;
    public static final int EXECUTE = 1078;
    public static final int EXISTS = 1079;
    public static final int EXTERNAL = 1080;
    public static final int EXTRACT = 1081;
    public static final int FALSE = 1082;
    public static final int FETCH = 1083;
    public static final int FIRST = 1084;
    public static final int FLOAT = 1085;
    public static final int FOR = 1086;
    public static final int FOREIGN = 1087;
    public static final int FOUND = 1088;
    public static final int FROM = 1089;
    public static final int FULL = 1090;
    public static final int GET = 1091;
    public static final int GLOBAL = 1092;
    public static final int GO = 1093;
    public static final int GOTO = 1094;
    public static final int GRANT = 1095;
    public static final int GROUP = 1096;
    public static final int HAVING = 1097;
    public static final int HOUR = 1098;
    public static final int IDENTITY = 1099;
    public static final int IMMEDIATE = 1100;
    public static final int IN = 1101;
    public static final int INDICATOR = 1102;
    public static final int INITIALLY = 1103;
    public static final int INNER = 1104;
    public static final int INPUT = 1105;
    public static final int INSENSITIVE = 1106;
    public static final int INSERT = 1107;
    public static final int INT = 1108;
    public static final int INTEGER = 1109;
    public static final int INTERSECT = 1110;
    public static final int INTERVAL = 1111;
    public static final int INTO = 1112;
    public static final int IS = 1113;
    public static final int ISOLATION = 1114;
    public static final int JOIN = 1115;
    public static final int KEY = 1116;
    public static final int LANGUAGE = 1117;
    public static final int LAST = 1118;
    public static final int LEADING = 1119;
    public static final int LEFT = 1120;
    public static final int LEVEL = 1121;
    public static final int LIKE = 1122;
    public static final int LOCAL = 1123;
    public static final int LOWER = 1124;
    public static final int MATCH = 1125;
    public static final int MAX = 1126;
    public static final int MIN = 1127;
    public static final int MINUTE = 1128;
    public static final int MODULE = 1129;
    public static final int MONTH = 1130;
    public static final int NAMES = 1131;
    public static final int NATIONAL = 1132;
    public static final int NATURAL = 1133;
    public static final int NCHAR = 1134;
    public static final int NEXT = 1135;
    public static final int NO = 1136;
    public static final int NOT = 1137;
    public static final int NULL = 1138;
    public static final int NULLIF = 1139;
    public static final int NUMERIC = 1140;
    public static final int OCTET_LENGTH = 1141;
    public static final int OF = 1142;
    public static final int ON = 1143;
    public static final int ONLY = 1144;
    public static final int OPEN = 1145;
    public static final int OPTION = 1146;
    public static final int OR = 1147;
    public static final int ORDER = 1148;
    public static final int OUTER = 1149;
    public static final int OUTPUT = 1150;
    public static final int OVERLAPS = 1151;
    public static final int PAD = 1152;
    public static final int PARTIAL = 1153;
    public static final int POSITION = 1154;
    public static final int PRECISION = 1155;
    public static final int PREPARE = 1156;
    public static final int PRESERVE = 1157;
    public static final int PRIMARY = 1158;
    public static final int PRIOR = 1159;
    public static final int PRIVILEGES = 1160;
    public static final int PROCEDURE = 1161;
    public static final int PUBLIC = 1162;
    public static final int READ = 1163;
    public static final int REAL = 1164;
    public static final int REFERENCES = 1165;
    public static final int RELATIVE = 1166;
    public static final int RESTRICT = 1167;
    public static final int REVOKE = 1168;
    public static final int RIGHT = 1169;
    public static final int ROLLBACK = 1170;
    public static final int ROWS = 1171;
    public static final int SCHEMA = 1172;
    public static final int SCROLL = 1173;
    public static final int SECOND = 1174;
    public static final int SECTION = 1175;
    public static final int SELECT = 1176;
    public static final int SESSION = 1177;
    public static final int SESSION_USER = 1178;
    public static final int SET = 1179;
    public static final int SIZE = 1180;
    public static final int SMALLINT = 1181;
    public static final int SOME = 1182;
    public static final int SPACE = 1183;
    public static final int SQL = 1184;
    public static final int SQLCODE = 1185;
    public static final int SQLERROR = 1186;
    public static final int SQLSTATE = 1187;
    public static final int SUBSTRING = 1188;
    public static final int SUM = 1189;
    public static final int SYSTEM_USER = 1190;
    public static final int TABLE = 1191;
    public static final int TEMPORARY = 1192;
    public static final int THEN = 1193;
    public static final int TIME = 1194;
    public static final int TIMESTAMP = 1195;
    public static final int TIMEZONE_HOUR = 1196;
    public static final int TIMEZONE_MINUTE = 1197;
    public static final int TO = 1198;
    public static final int TRAILING = 1199;
    public static final int TRANSACTION = 1200;
    public static final int TRANSLATE = 1201;
    public static final int TRANSLATION = 1202;
    public static final int TRIM = 1203;
    public static final int TRUE = 1204;
    public static final int UNION = 1205;
    public static final int UNIQUE = 1206;
    public static final int UNKNOWN = 1207;
    public static final int UPDATE = 1208;
    public static final int UPPER = 1209;
    public static final int USAGE = 1210;
    public static final int USER = 1211;
    public static final int USING = 1212;
    public static final int VALUE = 1213;
    public static final int VALUES = 1214;
    public static final int VARCHAR = 1215;
    public static final int VARYING = 1216;
    public static final int VIEW = 1217;
    public static final int WHEN = 1218;
    public static final int WHENEVER = 1219;
    public static final int WHERE = 1220;
    public static final int WITH = 1221;
    public static final int WORK = 1222;
    public static final int WRITE = 1223;
    public static final int YEAR = 1224;
    public static final int ZONE = 1225;

	public static final String[] KEYWORD_STR = { 
		"ABSOLUTE", "ACTION", "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE",
		"AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "AVG",
		"BEGIN", "BETWEEN", "BIT", "BIT_LENGTH", "BOTH", "BY",
		"CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR", "CHARACTER", "CHARACTER_LENGTH",
		"CHAR_LENGTH", "CHECK", "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT",
		"CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING",
		"CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR",
		"DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT",
		"DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DESCRIPTOR", "DIAGNOSTICS",
		"DISCONNECT", "DISTINCT", "DOMAIN", "DOUBLE", "DROP",
		"ELSE", "END", "END-EXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXTERNAL", "EXTRACT",
		"FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL",
		"GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP",
		"HAVING", "HOUR",
		"IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INPUT", "INSENSITIVE",
		"INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION",
		"JOIN", "KEY",
		"LANGUAGE", "LAST", "LEADING", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOWER",
		"MATCH", "MAX", "MIN", "MINUTE", "MODULE", "MONTH",
		"NAMES", "NATIONAL", "NATURAL", "NCHAR", "NEXT", "NO", "NOT", "NULL", "NULLIF", "NUMERIC",
		"OCTET_LENGTH", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUTER", "OUTPUT", "OVERLAPS",
		"PAD", "PARTIAL", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC",
		"READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS",
		"SCHEMA", "SCROLL", "SECOND", "SECTION", "SELECT", "SESSION", "SESSION_USER", "SET",
		"SIZE", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTRING", "SUM", "SYSTEM_USER",
		"TABLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE",
		"TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRIM", "TRUE",
		"UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USAGE", "USER", "USING",
		"VALUE", "VALUES", "VARCHAR", "VARYING", "VIEW",
		"WHEN", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE",
		"YEAR",	"ZONE"
	};

	String mText;
	int mId;
	int mStart;
	int mEnd;

	public Token(String text, int id, int start, int end) {
		mText = text;
		mId = id;
		mStart = start;
		mEnd = end;
	}

	public boolean is(int id) {
		return mId == id;
	}

	public boolean is(String ident) {
		if (mId == IDENTIFIER) {
			String s = mText.toUpperCase();
			return s.equals(ident.toUpperCase());
		}
		return false;
	}
	public String toString() {
		return "\"" + mText + "\" " + mStart + " - " + mEnd + " type " + mId;
	}

	public String rawText() {
		return mText;
	}

	public boolean isKeyword() {
		String s = mText.toUpperCase();
		for (int i = 0; i < KEYWORD_STR.length; i++) {
			if (s.equals(KEYWORD_STR[i])) {
				Log.d("### " + s + " is KEYWORD");
				return true;
			}
		}
		return false;
	}
}
