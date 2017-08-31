package cop5556sp17;

import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain chain = binaryChain.getE0();
		chain.visit(this, arg);
		TypeName chainType = chain.typeName;
		
		ChainElem chainElem = binaryChain.getE1();
		chainElem.visit(this, arg);
		TypeName chainElemType = chainElem.typeName;
		
		Token op = binaryChain.getArrow();
		
		if (chainType == URL) {
			if (op.kind == ARROW) {
				if (chainElemType == IMAGE) {
					binaryChain.typeName = IMAGE;
				}
			}
		} else if (chainType == FILE) {
			if (op.kind == ARROW) {
				if (chainElemType == IMAGE) {
					binaryChain.typeName = IMAGE;
				}
			}
		} else if (chainType == FRAME) {
			if (op.kind == ARROW) {
				if (chainElem instanceof FrameOpChain) {
					switch (chainElem.firstToken.kind) {
						case KW_XLOC:
						case KW_YLOC: {
							binaryChain.typeName = INTEGER;
						}break;
						case KW_SHOW:
						case KW_HIDE:
						case KW_MOVE:{
							binaryChain.typeName = FRAME;
						}
						default:
							break;
					}
				}
			}
		} else if (chainType == IMAGE) {
			if (op.kind == ARROW) {
				if (chainElem instanceof ImageOpChain) {
					switch (chainElem.firstToken.kind) {
						case OP_WIDTH:
						case OP_HEIGHT: {
							binaryChain.typeName = INTEGER;
						}break;
						case KW_SCALE: {
							binaryChain.typeName = IMAGE;
						} break;
						default:
							break;
					}
				} else if (chainElem instanceof FilterOpChain) {
					switch (chainElem.firstToken.kind){
						case OP_GRAY:
						case OP_BLUR:
						case OP_CONVOLVE:{
							binaryChain.typeName = IMAGE;
						}break;
						default:
							break;
					}
				} else if (chainElem.typeName == FILE) {
					binaryChain.typeName = NONE;
				} else if (chainElem.typeName == FRAME) {
					binaryChain.typeName = FRAME;
				} else if ((chainElem instanceof IdentChain)&&(chainElem.typeName == IMAGE)) {
					binaryChain.typeName = IMAGE;
				}
			} else if (op.kind == BARARROW) {
				if (chainElem instanceof FilterOpChain) {
					switch (chainElem.firstToken.kind) {
						case OP_GRAY:
						case OP_BLUR:
						case OP_CONVOLVE:{
							binaryChain.typeName = IMAGE;
						}break;
						default:
							break;
					}
				}
			}
		} else if (chainType == INTEGER) {
			if (op.kind == ARROW) {
				if ((chainElem instanceof IdentChain)&&(chainElem.typeName == INTEGER)) {
					binaryChain.typeName = INTEGER;
				}
			}
		}
		
		if (binaryChain.typeName != null) {
			return binaryChain;
		}
		throw new TypeCheckException("Error occurred while visiting binary chain:" + binaryChain);
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Expression e0 = binaryExpression.getE0();
		e0.visit(this, arg);
		TypeName e0Type = e0.typeName;
		Expression e1 = binaryExpression.getE1();
		e1.visit(this, arg);
		TypeName e1Type = e1.typeName;
		Token op = binaryExpression.getOp();
		switch (op.kind) {
			case PLUS:
			case MINUS:{
				if (e0Type == INTEGER && e1Type == INTEGER){
					binaryExpression.typeName = INTEGER;
				} else if (e0Type == IMAGE && e1Type == IMAGE) {
					binaryExpression.typeName = IMAGE;
				}
			} break;
			case TIMES:
			case DIV:
			case MOD:{
				if (e0Type == INTEGER && e1Type == INTEGER) {
					binaryExpression.typeName = INTEGER;
				} else if (e0Type == INTEGER && e1Type == IMAGE) {
					binaryExpression.typeName = IMAGE;
				} 
				else if (e0Type == IMAGE && e1Type == INTEGER) {
					binaryExpression.typeName = IMAGE;
				} 
			} break;
			//case DIV:
			//case MOD:{
			//	if (e0Type == INTEGER && e1Type == INTEGER) {
			//		binaryExpression.typeName = INTEGER;
			//	}
			//} break;
			case LT:
			case GT:
			case LE:
			case GE:{
				if (e0Type == INTEGER && e1Type == INTEGER) {
					binaryExpression.typeName = BOOLEAN;
				} else if (e0Type == BOOLEAN && e1Type == BOOLEAN) {
					binaryExpression.typeName = BOOLEAN;
				}
			} break;
			case EQUAL:
			case AND:
			case OR:
			case NOTEQUAL: {
				if (e0Type == e1Type) {
					binaryExpression.typeName = BOOLEAN;
				}
			} break;
			default:
				break;
		}
		if (binaryExpression.typeName != null) {
			return binaryExpression;
		}
		throw new TypeCheckException("Error occurred while visiting binary expression");
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtab.enterScope();
		for (Dec dec: block.getDecs()) {
			dec.visit(this, arg);
		}
		for (Statement stmt: block.getStatements()) {
			stmt.visit(this, arg);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		booleanLitExpression.typeName = BOOLEAN;
		return booleanLitExpression;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, arg);
		if (tuple.getExprList().size() == 0){
			filterOpChain.typeName = IMAGE;
			return filterOpChain;
		}
		throw new TypeCheckException("tuple size more than 0 in filter op chain");
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		frameOpChain.kind = frameOpChain.firstToken.kind;
		Kind frameOpKind = frameOpChain.firstToken.kind;
		
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);
		
		String linePos = frameOpChain.firstToken.getLinePos().toString();
		switch (frameOpKind) {
			case KW_SHOW:
			case KW_HIDE:{
				if (tuple.getExprList().size() == 0) {
					frameOpChain.typeName = NONE;
					return frameOpChain;
				}
			}break;
			case KW_XLOC:
			case KW_YLOC: {
				if (tuple.getExprList().size() == 0) {
					frameOpChain.typeName = INTEGER;
					return frameOpChain;
				}
			}break;
			case KW_MOVE: {
				if (tuple.getExprList().size() == 2) {
					frameOpChain.typeName = NONE;
					return frameOpChain;
				}
			}break;
			default: {
				throw new TypeCheckException("frame op kind is not matching at:" + linePos);
			}
		}
		throw new TypeCheckException("Conditions not satisfied for frameop at:" + linePos);
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Token ident = identChain.getFirstToken();
		Dec dec = symtab.lookup(ident.getText());
		if (dec == null) {
			String linepos = ident.getLinePos().toString();
			throw new TypeCheckException("Declaration was not find for token:"+ ident.getText() + " at:" + linepos);
		}
		identChain.typeName = dec.getType();
		identChain.dec = dec;
		return identChain;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec dec = symtab.lookup(identExpression.firstToken.getText());
		if (dec != null) {
			identExpression.typeName = dec.getType();
			identExpression.dec = dec;
			return identExpression;
		}
		throw new TypeCheckException(identExpression + " not declared in current scope");
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression e = (Expression) ifStatement.getE();
		e.visit(this, arg);
		if (e.typeName == BOOLEAN){
			ifStatement.getB().visit(this, arg);
			return ifStatement;
		}
		throw new TypeCheckException("expression type is not boolean");
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.typeName = INTEGER;
		return intLitExpression;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression e = sleepStatement.getE();
		e.visit(this, arg);
		if (e.typeName == INTEGER) {
			return sleepStatement;
		}
		throw new TypeCheckException("Type of given expression is not integer at:" + sleepStatement.firstToken.getLinePos().toString());
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Expression e = whileStatement.getE();
		Block b = whileStatement.getB();
		e.visit(this, arg);
		if (whileStatement.getE().typeName == BOOLEAN){
			b.visit(this, arg);
			return whileStatement;
		}
		throw new TypeCheckException("expression type is not boolean");
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		boolean succeeded = symtab.insert(declaration.getIdent().getText(), declaration);
		if (!succeeded){
			String linepos = declaration.getIdent().getLinePos().toString();
			throw new TypeCheckException(declaration.getIdent().getText() + " already declared in current scope at:" + linepos);
		}
		return declaration;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ParamDec pd: program.getParams()) {
			pd.visit(this, arg);
		}
		program.getB().visit(this, arg);
		return program;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		IdentLValue lvalue = assignStatement.getVar();
		lvalue.visit(this, arg);
		Expression e = assignStatement.getE();
		e.visit(this, arg);
		if (lvalue.dec.getType() == e.typeName) {
			return assignStatement;
		} else {
			String linePos = assignStatement.firstToken.getLinePos().toString();
			throw new TypeCheckException("Type mismatch your are assigning:" + e.typeName + " to type:" + lvalue.dec.getType() + " at loc:" + linePos);
		}
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec dec = symtab.lookup(identX.firstToken.getText());
		if (dec != null) {
			identX.dec = dec;
			return identX;
		}
		throw new TypeCheckException(identX.firstToken.getText() + " not declared at:" + identX.getFirstToken().getLinePos().toString());
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {

		boolean succeeded = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if (!succeeded){
			String linepos = paramDec.getIdent().getLinePos().toString();
			throw new TypeCheckException(paramDec.getIdent().getText() + " already declared in current scope at:" + linepos);
		}
		return paramDec;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.typeName = INTEGER;
		return constantExpression;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		imageOpChain.kind = imageOpChain.firstToken.kind;
		Kind imageOpKind = imageOpChain.firstToken.kind;
		
		Tuple tuple = imageOpChain.getArg();
		tuple.visit(this, arg);
		
		String linePos = imageOpChain.firstToken.getLinePos().toString();
		switch (imageOpKind){
			case OP_WIDTH:
			case OP_HEIGHT: {
				if (tuple.getExprList().size() == 0) {
					imageOpChain.typeName = INTEGER;
					return imageOpChain;
				}
			}break;
			case KW_SCALE: {
				if (tuple.getExprList().size() == 1) {
					imageOpChain.typeName = IMAGE;
					return imageOpChain;
				}
			}
			default: throw new TypeCheckException("Image op is not following the grrammar properly at:" + linePos);
		}
		throw new TypeCheckException("None of conditions matched for image op chain at:" + linePos);
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for (Expression e : tuple.getExprList()){
			e.visit(this, arg);
			if (e.typeName != INTEGER){
				throw new TypeCheckException("Expected all the expressions to be of type INTEGER at:"+e.firstToken.getLinePos().toString());
			}
		}
		return tuple;
	}

}
