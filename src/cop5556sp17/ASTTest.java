package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2 + 4 + 3";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		be = (BinaryExpression) be.getE0();
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(2, be.getE0().getFirstToken().intVal());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(4, be.getE1().getFirstToken().intVal());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a * b + c";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		be = (BinaryExpression) be.getE0();
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals("a", be.getE0().getFirstToken().getText());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals("b", be.getE1().getFirstToken().getText());
		assertEquals(TIMES, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(a * b)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals("a", be.getE0().getFirstToken().getText());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals("b", be.getE1().getFirstToken().getText());
		assertEquals(TIMES, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2 * 1) + c";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		//assertEquals("a", be.getE0().getFirstToken().getText());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals("c", be.getE1().getFirstToken().getText());
		assertEquals(PLUS, be.getOp().kind);
		be = (BinaryExpression) be.getE0();
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals("2", be.getE0().getFirstToken().getText());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(1, be.getE1().getFirstToken().intVal());
		assertEquals(TIMES, be.getOp().kind);
		
	}
	
	@Test
	public void testBinaryExpr5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2 > 1 + c * d";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals("2", be.getE0().getFirstToken().getText());
		assertEquals(GT, be.getOp().kind);
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		be = (BinaryExpression) be.getE1();
		assertEquals("1", be.getE0().getFirstToken().getText());
		assertEquals(PLUS, be.getOp().kind);
		be = (BinaryExpression) be.getE1();
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals("c", be.getE0().getFirstToken().getText());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals("d", be.getE1().getFirstToken().getText());
		assertEquals(TIMES, be.getOp().kind);
		
	}

	@Test
	public void testBlockExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer a boolean b}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.block();
		assertEquals(Block.class, ast.getClass());
		Block bl = (Block) ast;
		ArrayList<Dec> dec = bl.getDecs();
		assertEquals(2, dec.size());
		assertEquals("integer", dec.get(0).firstToken.getText());
		assertEquals("a", dec.get(0).getIdent().getText());
		assertEquals("b", dec.get(1).getIdent().getText());
		assertEquals("boolean", dec.get(1).firstToken.getText());
	}
	
	@Test
	public void testBlockExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer a sleep 10;}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.block();
		assertEquals(Block.class, ast.getClass());
		Block bl = (Block) ast;
		ArrayList<Dec> dec = bl.getDecs();
		assertEquals(1, dec.size());
		assertEquals("integer", dec.get(0).firstToken.getText());
		assertEquals("a", dec.get(0).getIdent().getText());
		ArrayList<Statement> stmt = bl.getStatements();
		assertEquals(1, stmt.size());
		assertEquals(SleepStatement.class, stmt.get(0).getClass());
		assertEquals(OP_SLEEP, stmt.get(0).getFirstToken().kind);
		SleepStatement st = (SleepStatement) stmt.get(0);
		assertEquals(IntLitExpression.class, st.getE().getClass());
	}
	
	@Test
	public void testPrgmExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prgm0{integer a sleep 10;}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
		Program prg = (Program) ast;
		Block bl = prg.getB();
		ArrayList<Dec> dec = bl.getDecs();
		assertEquals(1, dec.size());
		assertEquals("integer", dec.get(0).firstToken.getText());
		assertEquals("a", dec.get(0).getIdent().getText());
		ArrayList<Statement> stmt = bl.getStatements();
		assertEquals(1, stmt.size());
		assertEquals(SleepStatement.class, stmt.get(0).getClass());
		assertEquals(OP_SLEEP, stmt.get(0).getFirstToken().kind);
		SleepStatement st = (SleepStatement) stmt.get(0);
		assertEquals(IntLitExpression.class, st.getE().getClass());
		assertEquals(0,	prg.getParams().size());
	}
	
	@Test
	public void testPrgmExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prgm1\ninteger a,boolean b,file f{integer a sleep 10;}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
		Program prg = (Program) ast;
		Block bl = prg.getB();
		ArrayList<Dec> dec = bl.getDecs();
		assertEquals(1, dec.size());
		assertEquals("integer", dec.get(0).firstToken.getText());
		assertEquals("a", dec.get(0).getIdent().getText());
		ArrayList<Statement> stmt = bl.getStatements();
		assertEquals(1, stmt.size());
		assertEquals(SleepStatement.class, stmt.get(0).getClass());
		assertEquals(OP_SLEEP, stmt.get(0).getFirstToken().kind);
		SleepStatement st = (SleepStatement) stmt.get(0);
		assertEquals(IntLitExpression.class, st.getE().getClass());
		assertEquals(3,	prg.getParams().size());
	}
	
	@Test
	public void testPramDecExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer a";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
		ParamDec pd = (ParamDec) ast;
		assertEquals("integer", pd.getFirstToken().getText());
		assertEquals("a", pd.getIdent().getText());
		assertEquals(KW_INTEGER, pd.firstToken.kind);
	}
	
	@Test
	public void testDecExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "frame a";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.dec();
		assertEquals(Dec.class, ast.getClass());
		Dec pd = (Dec) ast;
		assertEquals("frame", pd.getFirstToken().getText());
		assertEquals("a", pd.getIdent().getText());
		assertEquals(KW_FRAME, pd.firstToken.kind);
	}
	
	@Test
	public void testStatementExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep 10;";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		assertEquals("sleep", ast.getFirstToken().getText());
		SleepStatement slpStmt = (SleepStatement) ast;
		assertEquals(10, slpStmt.getE().getFirstToken().intVal());
		assertEquals(OP_SLEEP, slpStmt.getFirstToken().kind);
	}
	
	@Test
	public void testStatementExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep a * b + c;";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		SleepStatement slpStmt = (SleepStatement) ast;
		assertEquals(BinaryExpression.class, slpStmt.getE().getClass());
		BinaryExpression be = (BinaryExpression) slpStmt.getE();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		be = (BinaryExpression) be.getE0();
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals("a", be.getE0().getFirstToken().getText());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals("b", be.getE1().getFirstToken().getText());
		assertEquals(TIMES, be.getOp().kind);
	}
	
	@Test
	public void testStatementExpr2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while ((a * b + c) > 2){}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		WhileStatement wStmt = (WhileStatement) ast;
		assertEquals(BinaryExpression.class, wStmt.getE().getClass());
		BinaryExpression be = (BinaryExpression) wStmt.getE();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
		be = (BinaryExpression) be.getE0();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals("c", be.getE1().getFirstToken().getText());
		assertEquals(PLUS, be.getOp().kind);
		be = (BinaryExpression) be.getE0();
		assertEquals("a", be.getE0().getFirstToken().getText());
		assertEquals("b", be.getE1().getFirstToken().getText());
		assertEquals(TIMES, be.getOp().kind);
	}
	
	@Test
	public void testStatementExpr3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a <- 10 * 20;";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		assertEquals(AssignmentStatement.class, ast.getClass());
		AssignmentStatement aStmt = (AssignmentStatement) ast;
		assertEquals(IdentLValue.class, aStmt.getVar().getClass());
		assertEquals("a", aStmt.getVar().getText());
		assertEquals(BinaryExpression.class, aStmt.getE().getClass());
		BinaryExpression be = (BinaryExpression) aStmt.getE();
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals("20", be.getE1().getFirstToken().getText());
		assertEquals(TIMES, be.getOp().kind);
		assertEquals("10", be.getE0().getFirstToken().getText());
	}
	
	@Test
	public void testChainExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve(a,b,c) -> xloc(28,29)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
	}
	
	@Test
	public void testChainExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur(a*b)|-> gray(a)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
	}
	
	@Test
	public void testChainElemExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray(10,58)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chainElem();
		assertEquals(FilterOpChain.class, ast.getClass());
		FilterOpChain fop = (FilterOpChain) ast;
		Tuple tup = fop.getArg();
		List<Expression> args = tup.getExprList();
		assertEquals(2, args.size());
		assertEquals(IntLitExpression.class, args.get(0).getClass());
		assertEquals(58, args.get(1).getFirstToken().intVal());
	}
	
	@Test
	public void testChainElemExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "show(x)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chainElem();
		assertEquals(FrameOpChain.class, ast.getClass());
		FrameOpChain fop = (FrameOpChain) ast;
		Tuple tup = fop.getArg();
		List<Expression> args = tup.getExprList();
		assertEquals(1, args.size());
		assertEquals(IdentExpression.class, args.get(0).getClass());
		assertEquals("x", args.get(0).getFirstToken().getText());
	}
	
	@Test
	public void testChainElemExpr2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "height";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chainElem();
		assertEquals(ImageOpChain.class, ast.getClass());
		assertEquals(OP_HEIGHT, ast.getFirstToken().kind);
	}
	
	@Test
	public void testParseExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "parse {blur(a*b)|-> gray(a);}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass());
		List<Statement> stmts = ((Program) ast).getB().getStatements();
		assertEquals(1, stmts.size());
		assertEquals(BinaryChain.class, stmts.get(0).getClass());
		assertEquals(OP_BLUR, stmts.get(0).getFirstToken().kind);
	}
	
	@Test
	public void testFactorExpr() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(ConstantExpression.class, ast.getClass());
	}
	
}
