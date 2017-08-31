package cop5556sp17;

import cop5556sp17.Scanner.Kind;
//import cop5556sp17.Scanner.State;

import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.AST.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556sp17.Scanner.Token;

public class Parser{

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		ASTNode astnode = program();
		matchEOF();
		return astnode;
	}
	
	final List<Kind> arrowOp = Arrays.asList(new Kind[]{ARROW, BARARROW});
	final List<Kind> relOp = Arrays.asList(new Kind[]{LT, LE, GT, GE, EQUAL, NOTEQUAL});
	final List<Kind> weakOp = Arrays.asList(new Kind[]{PLUS, MINUS, OR});
	final List<Kind> strongOp = Arrays.asList(new Kind[]{TIMES, DIV, AND, MOD});
	final List<Kind> decStart = Arrays.asList(new Kind[]{KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME});
	final List<Kind> statementStart = Arrays.asList(new Kind[]{OP_SLEEP, KW_WHILE, KW_IF, IDENT, OP_BLUR, OP_GRAY, OP_CONVOLVE, KW_SHOW, KW_HIDE, KW_MOVE, KW_XLOC, KW_YLOC, OP_WIDTH, OP_HEIGHT, KW_SCALE});
	

	Expression expression() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Token ft = t;
		e0 = term();
		while (relOp.contains(t.kind)) {
			Token op = t;
			consume();
			e1 = term();
			e0 = new BinaryExpression(ft, e0, op, e1);
		}
		return e0;
	}

	Expression term() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Token ft = t;
		e0 = elem();
		while (weakOp.contains(t.kind)) {
			Token op = t;
			consume();
			e1 = elem();
			e0 = new BinaryExpression(ft, e0, op, e1);
		}
		return e0;
	}
	
	Expression elem() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Token ft = t;
		e0 = factor();
		while (strongOp.contains(t.kind)) {
			Token op = t;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(ft, e0, op, e1);
		}
		return e0;
	}

	Expression factor() throws SyntaxException {
		Kind kind = t.kind;
		Expression e = null;
		switch (kind) {
			case IDENT: {
				e = new IdentExpression(t);
				consume();
			} break;
			case INT_LIT:{
				e = new IntLitExpression(t);
				consume();
			} break;
			case KW_TRUE:
			case KW_FALSE:{
				e = new BooleanLitExpression(t);
				consume();
			}break;
			case KW_SCREENWIDTH:
			case KW_SCREENHEIGHT: {
				e = new ConstantExpression(t);
				consume();
			}break;
			case LPAREN: {
				consume();
				e = expression();
				match(RPAREN);
			}break;
			default:
				//you will want to provide a more useful error message
				throw new SyntaxException("Illegal factor at location:-"+ t.getLinePos().toString());
			}
		return e;
	}

	Block block() throws SyntaxException {
		//Todo:- Confirm what should be first token
		Token ft = t;
		match(LBRACE);
		Block b = null;
		ArrayList<Dec> decs = new ArrayList<Dec>();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		while (decStart.contains(t.kind) || statementStart.contains(t.kind)){
			if (decStart.contains(t.kind)){
				decs.add(dec());
			} else {
				statements.add(statement());
			}
		}
		match(Kind.RBRACE);
		b = new Block(ft, decs, statements);
		return b;
	}

	Program program() throws SyntaxException {
		Token ft = t;
		match(IDENT);
		Program prg = null;
		Block b = null;
		ArrayList<ParamDec> pd = new ArrayList<ParamDec>();
		if (t.isKind(LBRACE)){
			b = block();
		} else {
			pd.add(paramDec());
			while (t.isKind(COMMA)){
				consume();
				pd.add(paramDec());
			}
			b = block();
		}
		matchEOF();
		prg = new Program(ft, pd, b);
		return prg;
	}

	ParamDec paramDec() throws SyntaxException {
		Kind kind = t.kind;
		ParamDec pd = null;
		Token ft = t;
		switch (kind) {
			case KW_URL:
			case KW_FILE:
			case KW_INTEGER:
			case KW_BOOLEAN:{
				consume();
				Token t2 = t;
				match(IDENT);
				pd = new ParamDec(ft, t2);
			} break;
			default: {
				throw new SyntaxException("Illegal paramDec at:-" + t.getLinePos().toString());
			}
		}
		return pd;
	}

	Dec dec() throws SyntaxException {
		Kind kind = t.kind;
		Dec d = null;
		Token ft = t;
		switch (kind) {
			case KW_INTEGER:
			case KW_BOOLEAN:
			case KW_IMAGE:
			case KW_FRAME:{
				consume();
				Token t2 = t;
				match(IDENT);
				d = new Dec(ft, t2);
			} break;
			default: {
				throw new SyntaxException("Illegal Dec at:-" + t.getLinePos().toString());
			}
		}
		return d;
	}

	Statement statement() throws SyntaxException {
		Kind kind = t.kind;
		Statement stmt = null;
		Token ft = t;
		switch(kind){
			case OP_SLEEP: {
				consume();
				Expression e1 = expression();
				match(SEMI);
				stmt = new SleepStatement(ft, e1);
			} break;
			case KW_WHILE: {
				consume();
				match(LPAREN);
				Expression e = expression();
				match(RPAREN);
				Block b = block();
				stmt = new WhileStatement(ft, e, b);
			}break;
			case KW_IF:{
				consume();
				match(LPAREN);
				Expression e = expression();
				match(RPAREN);
				Block b = block();
				stmt = new IfStatement(ft, e, b);
			}break;
			case IDENT:{
				Token next_token = scanner.peek();
				if (next_token.isKind(ASSIGN)){
					//Todo:- Check the first token value to be used
					IdentLValue ilv = new IdentLValue(t);
					consume();
					match(ASSIGN);
					Expression e = expression();
					match(SEMI);
					stmt = new AssignmentStatement(ft, ilv, e);
				} else {
					Chain ch = chain();
					match(SEMI);
					stmt = ch;
				}
			}break;
			case OP_BLUR:
			case OP_GRAY:
			case OP_CONVOLVE:
			case KW_SHOW:
			case KW_HIDE:
			case KW_MOVE:
			case KW_XLOC:
			case KW_YLOC:
			case OP_WIDTH:
			case OP_HEIGHT:
			case KW_SCALE:{
				Chain ch = chain();
				match(Kind.SEMI);
				stmt = ch;
			}break;
			default: {
				throw new SyntaxException("Illegal Statement at:-" + t.getLinePos().toString());
			}
		}
		return stmt;
	}

	Chain chain() throws SyntaxException {
		Chain e0 = null;
		ChainElem e1 = null;
		Token ft = t;
		Token arrow = null;
		e0 = chainElem();
		if (arrowOp.contains(t.kind)){
			arrow = t;
			consume();
			e1 = chainElem();
			e0 = new BinaryChain(ft, e0, arrow, e1);
			while (arrowOp.contains(t.kind)){
				arrow = t;
				consume();
				e1 = chainElem();
				e0 = new BinaryChain(ft, e0, arrow, e1);
			}
			
		} else {
			throw new SyntaxException("Illegal chain due to: " + t.getText() + " of kind: " + t.kind + " at:-" + t.getLinePos().toString());
		}
		return e0;
	}

	ChainElem chainElem() throws SyntaxException {
		Kind kind = t.kind;
		ChainElem ce = null;
		Token ft = t;
		switch(kind){
			case IDENT:{
				ce = new IdentChain(ft);
				consume();
			}break;
			case OP_BLUR:
			case OP_GRAY:
			case OP_CONVOLVE:{
				consume();
				Tuple arg = arg();
				ce = new FilterOpChain(ft, arg);
			}break;
			case KW_SHOW:
			case KW_HIDE:
			case KW_MOVE:
			case KW_XLOC:
			case KW_YLOC:{
				consume();
				Tuple arg = arg();
				ce = new FrameOpChain(ft, arg);
			}break;
			case OP_WIDTH:
			case OP_HEIGHT:
			case KW_SCALE:{
				consume();
				Tuple arg = arg();
				ce = new ImageOpChain(ft, arg);
			}break;
			default:{
				throw new SyntaxException("Illegal chainElem at:-" + t.getLinePos().toString());
			}
		}
		return ce;
	}
	Tuple arg() throws SyntaxException {
		//Todo:- Confirm the firsttoken here
		Tuple tup = null;
		Token ft = t;
		ArrayList<Expression> exp = new ArrayList<Expression>();
		if (t.isKind(LPAREN)) {
			consume();
			exp.add(expression());
			while (t.isKind(COMMA)){
				consume();
				exp.add(expression());
			}
			match(RPAREN);
		}
		tup = new Tuple(ft, exp);
		return tup;
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF but got " + t.kind + " at:-" + t.getLinePos().toString());
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	@SuppressWarnings("unused")
	private Token match(Kind... kinds) throws SyntaxException {
		for (Kind k : kinds){
			if (t.kind == k){
				Token reqToken = t;
				consume();
				return reqToken;
			}
		}
		throw new SyntaxException("given token of kind:"+t.kind+" did not match to any of th required token");
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
