package livros.compiler;

import livros.Log;
import java.util.List;

public class AST
{
	public Object accept(ASTVisitor v) throws Exception {
		Log.d(this + " accept is not implement");
		return null;
	}

	public String toStringList(List li, String delim) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < li.size(); i++) {
			Object o = li.get(i);
			builder.append(o.toString());
			if (i+1 < li.size()) {
				builder.append(delim);
			}
		}
		return builder.toString();
	}
}
