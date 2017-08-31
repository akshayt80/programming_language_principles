package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " (a * b) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(a*B*c*d)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (a*b*c < d*e*f) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactor4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactorError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(a*b";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactorError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(a**b)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactorError2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " <- ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testFactorError3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " ( ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.factor();
	}
	
	@Test
	public void testParamDec0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url a2z";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	public void testParamDecError0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "file";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testDec0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer a";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "boolean abc";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testDecError0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "image";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testDecError1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.dec();
	}
	
	@Test
	public void testElem0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "22 & 33";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	public void testElem1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(a*b)/(b*c)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	public void testElem2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(a*b)*(b*c)*(c*d)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	public void testElemError0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(a*b)%()";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.elem();
	}
	
	@Test
	public void testElemError1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(a*b)%b*";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.elem();
	}
	
	
	@Test
	public void testTerm0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "2 < 3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.term();
	}
	
	@Test
	public void testTermError() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "+ 3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.term();
	}
	
	@Test
	public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "sleep 10+20;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "while  (i>2)\n{\ninteger a\na<-20;\n}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "if(i==true){boolean x}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "gray -> abc;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testStatementError1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "sleep 10+20";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testStatementError2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "while(i>2){";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testStatementError3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "if(){integer a}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testStatementError4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray ->";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testChain0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "a->a->b->d";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	@Test
	public void testChain1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur(a*b)|->a";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	@Test
	public void testChain2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc -> gray -> xyz";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	@Test
	public void testChain3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc -> gray";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	
	@Test
	public void testChainError0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "a->a->b->";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chain();
	}
	
	@Test
	public void testChainError1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur(a*b)|->";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chain();
	}
	
	@Test
	public void testChainError2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur(a*b)|-";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chain();
	}
	
	
	
	@Test
	public void testChainElem0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "convolve(a,b,c)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
	}
	
	@Test
	public void testChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "abc";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
	}
	
	@Test
	public void testChainElem2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "convolve";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
	}
	
	@Test
	public void testChainElemError0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur(a,)";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chainElem();
	}
	
	@Test
	public void testChainElemError1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur(a,b,c";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chainElem();
	}
	
	@Test
	public void testChainElemError2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur(a*b*)";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chainElem();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "global integer a, boolean b {integer a\na<-20;}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "global file f {a<-20;}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog2 {integer a boolean b frame f}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testProgramError0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog2 {integer a boolean b frame}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.program();
	}
	
	@Test
	public void testProgramError1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog2 {integer a";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.program();
	}
	
	@Test
	public void testProgramError2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog2 {integer a };";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.program();
	}
	
	@Test
	public void testStatement0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "   a <- 2 + 3;   ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testStatementError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "   a <- 2 + 3   ";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.statement();
	} 
}
