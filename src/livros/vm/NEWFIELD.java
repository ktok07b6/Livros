package livros.vm;

import livros.db.Field;

public class NEWFIELD extends INST
{
	Field mField;

 	public NEWFIELD(Field field) {
		super();
		mField = field;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
