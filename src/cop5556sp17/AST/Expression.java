package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public abstract class Expression extends ASTNode {
	public Type.TypeName typeName = null;
	protected Expression(Token firstToken) {
		super(firstToken);
	}
	
	public TypeName getType() {
		return typeName;
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
