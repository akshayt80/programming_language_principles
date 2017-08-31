package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static cop5556sp17.Scanner.Kind;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void testIntValue() throws IllegalCharException, IllegalNumberException {
		String input = "1024";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(0, token.pos);
		String text = input;
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		assertEquals(1024, token.intVal());
	}

//TODO  more tests
	@Test
	public void testCommaConcat() throws IllegalCharException, IllegalNumberException {
		String input = ", ,,\n,";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		//Check first token
		assertEquals(Kind.COMMA, token.kind);
		assertEquals(0, token.pos);
		String text = Kind.COMMA.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//Check second token
		token = scanner.nextToken();
		assertEquals(Kind.COMMA, token.kind);
		assertEquals(2, token.pos);
		text = Kind.COMMA.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//Check third token
		token = scanner.nextToken();
		assertEquals(Kind.COMMA, token.kind);
		assertEquals(3, token.pos);
		text = Kind.COMMA.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//Check fourth token
		token = scanner.nextToken();
		assertEquals(Kind.COMMA, token.kind);
		assertEquals(5, token.pos);
		String pos = "LinePos [line=1, posInLine=0]";
		assertEquals(pos, token.getLinePos().toString());
		text = Kind.COMMA.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
	}
	@Test
	public void testIdent() throws IllegalCharException, IllegalNumberException{
		String input = "abc== integer";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		//Check first token
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(0, token.pos);
		String text = "abc";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check second token
		token = scanner.nextToken();
		assertEquals(Kind.EQUAL, token.kind);
		assertEquals(3, token.pos);
		text = Kind.EQUAL.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check third token
		token = scanner.nextToken();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(6, token.pos);
		text = "integer";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testComment() throws IllegalCharException, IllegalNumberException {
		String input = "a <- 23 + 48 /*performin sum*/!";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		//check first token
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(0, token.pos);
		String text = "a";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check second token
		token = scanner.nextToken();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(2, token.pos);
		text = Kind.ASSIGN.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check third token
		token = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(5, token.pos);
		text = "23";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check fourth token
		token = scanner.nextToken();
		assertEquals(Kind.PLUS, token.kind);
		assertEquals(8, token.pos);
		text = Kind.PLUS.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check fifth token
		token = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(10, token.pos);
		text = "48";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check sixth token
		token = scanner.nextToken();
		assertEquals(Kind.NOT, token.kind);
		assertEquals(30, token.pos);
		text = "!";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testIllegalCommentError() throws IllegalCharException, IllegalNumberException{
		String input = "000/* comm1*/abc/*error*";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();	
	}
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException {
		String input = "integer a <- 20;\ninteger b<-30;\n\ninteger sum <- a+b;\nif (sum>=a){\n    a <- sum;\n}\nshow(a);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		String text = "integer";
		//check first token
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check second token
		token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(8, token.pos);
		text = "a";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check third token
		token = scanner.nextToken();
		text = Kind.ASSIGN.text;
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(10, token.pos);
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check fourth token
		token = scanner.nextToken();
		text = "20";
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(13, token.pos);
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		assertEquals(20, token.intVal());
		//check fifth token
		token = scanner.nextToken();
		text = Kind.SEMI.text;
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(15, token.pos);
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testZeros() throws IllegalCharException, IllegalNumberException {
		String input = "000abc12;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		String text = "";
		//check first token
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(0, token.pos);
		text = token.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check second token
		token = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(1, token.pos);
		text = token.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check third token
		token = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(2, token.pos);
		text = token.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check fourth token
		token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(3, token.pos);
		text = "abc12";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check fifth token
		token = scanner.nextToken();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(8, token.pos);
		text = token.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check eof token
		token = scanner.nextToken();
		assertEquals(Kind.EOF, token.kind);
		assertEquals(9, token.pos);
		text = token.getText();
		assertEquals(0, token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testIllegalSymbols() throws IllegalCharException, IllegalNumberException {
		String input = "=";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		//Second illegal character
		input = "|-";
		scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		//third illegal character
		input = "\\";
		scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		//fourth illegal character
		input = "^";
		scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testCommentNewline() throws IllegalCharException, IllegalNumberException {
		String input = "a/*comm\n**/b\n;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		String text = "";
		//check first token
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(0, token.pos);
		text = "a";
		String pos = "LinePos [line=0, posInLine=0]";
		assertEquals(pos, token.getLinePos().toString());
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check second token
		token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(11, token.pos);
		text = "b";
		pos = "LinePos [line=1, posInLine=3]";
		assertEquals(pos, token.getLinePos().toString());
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check third token
		token = scanner.nextToken();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(13, token.pos);
		text = Kind.SEMI.text;
		pos = "LinePos [line=2, posInLine=0]";
		assertEquals(pos, token.getLinePos().toString());
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check eof token
		token = scanner.nextToken();
		assertEquals(Kind.EOF, token.kind);
		assertEquals(14, token.pos);
		text = token.getText();
		assertEquals(0, token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testSlashStar() throws IllegalCharException, IllegalNumberException {
		String input = "/*";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testSlash() throws IllegalCharException, IllegalNumberException {
		String input = "/";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//check token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.DIV, token.kind);
		assertEquals(0, token.pos);
		String text = Kind.DIV.text;
		String pos = "LinePos [line=0, posInLine=0]";
		assertEquals(pos, token.getLinePos().toString());
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//check eof token
		token = scanner.nextToken();
		assertEquals(Kind.EOF, token.kind);
		assertEquals(1, token.pos);
		text = token.getText();
		assertEquals(0, token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testTab() throws IllegalCharException, IllegalNumberException {
		String input = "\tabc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//check token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(1, token.pos);
		String text = "abc";
		String pos = "LinePos [line=0, posInLine=1]";
		assertEquals(pos, token.getLinePos().toString());
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
	}
	
	@Test
	public void testErrorCase() throws IllegalCharException, IllegalNumberException {
		String input = "|;|--->->-|->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//First token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.OR, token.kind);
		//Second token
		token = scanner.nextToken();
		assertEquals(Kind.SEMI, token.kind);
		//third
		token = scanner.nextToken();
		assertEquals(Kind.OR, token.kind);
		assertEquals(2, token.pos);
		//fourth
		token = scanner.nextToken();
		assertEquals(Kind.MINUS, token.kind);
		assertEquals(3, token.pos);
		//fifth
		token = scanner.nextToken();
		assertEquals(Kind.MINUS, token.kind);
		assertEquals(4, token.pos);
		//sixth
		token = scanner.nextToken();
		assertEquals(Kind.ARROW, token.kind);
		assertEquals(5, token.pos);
		//seventh
		token = scanner.nextToken();
		assertEquals(Kind.ARROW, token.kind);
		assertEquals(7, token.pos);
		//eighth
		token = scanner.nextToken();
		assertEquals(Kind.MINUS, token.kind);
		assertEquals(9, token.pos);
		//ninth
		token = scanner.nextToken();
		assertEquals(Kind.BARARROW, token.kind);
		assertEquals(10, token.pos);
	}
}
