/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testIntLitExpr0() throws Exception {
		String input = "integer a <- 20";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testIfStatement0() throws Exception {
		String input = "if(20<30){}";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		Statement stmt = parser.statement();
		stmt.visit(v, null);
	}
	
	@Test
	public void testIfStatementError0() throws Exception {
		String input = "if(20){}";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		Statement stmt = parser.statement();
		thrown.expect(TypeCheckException.class);
		stmt.visit(v, null);
	}
	
	@Test
	public void testWhileStatement0() throws Exception {
		String input = "while(20<30){}";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		Statement stmt = parser.statement();
		stmt.visit(v, null);
	}
	
	@Test
	public void testWhileStatementError0() throws Exception {
		String input = "while(20){}";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		Statement stmt = parser.statement();
		thrown.expect(TypeCheckException.class);
		stmt.visit(v, null);
	}
	
	@Test
	public void testFilterOpChain0() throws Exception {
		String input = "convolve";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		cElem.visit(v, null);
		assertEquals(cElem.typeName, TypeName.IMAGE);
	}
	
	@Test
	public void testFilterOpChainError0() throws Exception {
		String input = "convolve(0)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		thrown.expect(TypeCheckException.class);
		cElem.visit(v, null);
	}
	
	@Test
	public void testFrameOpChain0() throws Exception {
		String input = "show";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		cElem.visit(v, null);
		assertEquals(cElem.typeName, TypeName.NONE);
	}
	
	@Test
	public void testFrameOpChainError0() throws Exception {
		String input = "show(0)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		thrown.expect(TypeCheckException.class);
		cElem.visit(v, null);
	}
	
	@Test
	public void testFrameOpChain1() throws Exception {
		String input = "xloc";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		cElem.visit(v, null);
		assertEquals(cElem.typeName, TypeName.INTEGER);
	}
	
	@Test
	public void testFrameOpChainError1() throws Exception {
		String input = "yloc(0)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		thrown.expect(TypeCheckException.class);
		cElem.visit(v, null);
	}
	
	@Test
	public void testFrameOpChain2() throws Exception {
		String input = "move(2,5)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		cElem.visit(v, null);
		assertEquals(cElem.typeName, TypeName.NONE);
	}
	
	@Test
	public void testFrameOpChainError2() throws Exception {
		String input = "move(1)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		thrown.expect(TypeCheckException.class);
		cElem.visit(v, null);
	}
	
	@Test
	public void testImageOpChain0() throws Exception {
		String input = "width";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		cElem.visit(v, null);
		assertEquals(cElem.typeName, TypeName.INTEGER);
	}
	
	@Test
	public void testImageOpChainError0() throws Exception {
		String input = "height(1)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		thrown.expect(TypeCheckException.class);
		cElem.visit(v, null);
	}
	
	@Test
	public void testImageOpChain1() throws Exception {
		String input = "scale(5)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		cElem.visit(v, null);
		assertEquals(cElem.typeName, TypeName.IMAGE);
	}
	
	@Test
	public void testImageOpChainError1() throws Exception {
		String input = "scale(5,6)";
		Parser parser = new Parser(new Scanner(input).scan());
		TypeCheckVisitor v = new TypeCheckVisitor();
		ChainElem cElem = parser.chainElem();
		thrown.expect(TypeCheckException.class);
		cElem.visit(v, null);
	}
	
	@Test
	public void testAssignmentError0() throws Exception{
		String input = "p {\nboolean y \ny <- false;\ninteger y}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignmentError1() throws Exception{
		String input = "p integer x, integer j, boolean x{\nboolean y \ny <- false;\ninteger y}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignment0() throws Exception{
		String input = "p integer x, integer j{\nboolean y \ny <- false;\nboolean j}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignment1() throws Exception{
		String input = "p {y <- false;\nboolean j\nboolean y}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckException.class);
		program.visit(v, null);		
	}

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignmentBoolLit1() throws Exception{
		String input = "p {\nboolean y \ny <- false;\nwhile(10<20){\ninteger y\ny <- 40;}\ny<-true;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignmentBoolLit2() throws Exception{
		String input = "p {\nboolean y \ny <- false;\nwhile(10<20){\ninteger y\ny <- 40;}\ny <- true;\nif (true) {y <- false;} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ninteger y\ny <- 28;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignmentBoolLitError1() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignmentBoolLitError2() throws Exception{
		String input = "p {\nboolean y \ny <- false;\nwhile(10<20){\ninteger y\ny <- 40;}\ny<-38;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testAssignmentBoolLitError3() throws Exception{
		String input = "p {\nboolean y \ny <- false;\nwhile(10<20){\ninteger y\ny <- 40;}\ny <- true;\nif (true) {y <- 50;} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	
	@Test
	public void testBinaryChain0() throws Exception{
		String input = "p url xyz  {\nxyz->convolve;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = (Program) parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		BinaryChain bc = (BinaryChain) program.getB().getStatements().get(0);
		assertEquals(TypeName.IMAGE, bc.typeName);
	}
	
	@Test
	public void testBinaryChain1() throws Exception{
		String input = "p {\nframe xyz\nxyz->hide;}";
		//String input = "prog1  file file1, integer itx, boolean b1{ integer ii1 boolean bi1 \n image IMAGE1 frame fram1 sleep itx+ii1; while (b1){if(bi1)\n{sleep ii1+itx*2;}}\nfile1->blur |->gray;fram1 ->yloc;\n IMAGE1->blur->scale (ii1+1)|-> gray;\nii1 <- 12345+54321;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		BinaryChain bc = (BinaryChain) program.getB().getStatements().get(0);
		assertEquals(TypeName.FRAME, bc.typeName);
	}
	
	@Test
	public void testBinaryChain2() throws Exception{
		String input = "p {\nimage xyz\nxyz|->convolve;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		BinaryChain bc = (BinaryChain) program.getB().getStatements().get(0);
		assertEquals(TypeName.IMAGE, bc.typeName);
	}
	
	@Test
	public void testBinaryExpression0() throws Exception {
		String input = "p {\ninteger x\n x<-x+4;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testBinaryExpression1() throws Exception {
		String input = "p {\ninteger x\n x<-x-4;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	//Additional
	 @Test
	    public void testChainTable() throws Exception {
	     //String input = "chelsea file metcalf{image i\n tim -> i;}";//type image
	     String input = "tos url u,\ninteger x\n{integer y image i u -> i; i -> height; frame f i-> scale(x) -> f;}";//type image
	     Scanner scanner = new Scanner(input);
	     scanner.scan();
	     Parser parser = new Parser(scanner);
	     ASTNode program = parser.parse();
	     TypeCheckVisitor v = new TypeCheckVisitor();
	     //thrown.expect(TypeCheckVisitor.TypeCheckException.class);
	     program.visit(v, null);
	    }
}
