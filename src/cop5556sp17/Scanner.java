package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
	
	//hashmap to store all keywords and there enum kind
	HashMap<String, Kind> reservedWord = new HashMap<>();
	public void setReservedWord() {
		reservedWord.put("integer", Kind.KW_INTEGER);
		reservedWord.put("boolean", Kind.KW_BOOLEAN);
		reservedWord.put("image", Kind.KW_IMAGE);
		reservedWord.put("url", Kind.KW_URL);
		reservedWord.put("file", Kind.KW_FILE);
		reservedWord.put("frame", Kind.KW_FRAME);
		reservedWord.put("while", Kind.KW_WHILE);
		reservedWord.put("if", Kind.KW_IF);
		reservedWord.put("true", Kind.KW_TRUE);
		reservedWord.put("false", Kind.KW_FALSE);
		reservedWord.put("blur", Kind.OP_BLUR);
		reservedWord.put("gray", Kind.OP_GRAY);
		reservedWord.put("convolve", Kind.OP_CONVOLVE);
		reservedWord.put("screenheight", Kind.KW_SCREENHEIGHT);
		reservedWord.put("height", Kind.OP_HEIGHT);
		reservedWord.put("width", Kind.OP_WIDTH);
		reservedWord.put("screenwidth", Kind.KW_SCREENWIDTH);
		reservedWord.put("xloc", Kind.KW_XLOC);
		reservedWord.put("yloc", Kind.KW_YLOC);
		reservedWord.put("hide", Kind.KW_HIDE);
		reservedWord.put("show", Kind.KW_SHOW);
		reservedWord.put("move", Kind.KW_MOVE);
		reservedWord.put("sleep", Kind.OP_SLEEP);
		reservedWord.put("scale", Kind.KW_SCALE);
	}
	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			if ((this.kind == Kind.IDENT) || (this.kind == Kind.INT_LIT)) {
				return chars.substring(pos, pos + length);
			} else {
				return this.kind.getText();
			}
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			int line_number = java.util.Collections.binarySearch(lineNo, pos);
			if (line_number < 0){
				line_number = (line_number * -1) - 2;
			}
			int posInLine = pos - lineNo.get(line_number);
			return new LinePos(line_number, posInLine);
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			return Integer.parseInt(this.getText());
		}
		
		public boolean isKind(Kind kind) {
			return this.kind == kind;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}

		private Scanner getOuterType() {
			return Scanner.this;	
		}
		
	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		setReservedWord();
	}

	public int skipWhiteSpace(int pos, int length){
		while(pos < length){
			if (chars.charAt(pos) == '/') {
				if ((pos < (length-1)) && (chars.charAt(pos+1) == '*')){
					Boolean com_closed = false;
					pos = pos + 2;
					while((com_closed == false) && (pos < (length - 1))) {
						if (chars.charAt(pos) == '*' && chars.charAt(pos+1) == '/') {
							pos++;
							com_closed = true;
						} else if (chars.charAt(pos) == '\n') {
							lineNo.add(pos+1);
						}
						pos++;
					};
					if (com_closed == false) {
						pos = -1;
						break;
					}
				} else {
					break;
				}
			} else if (chars.charAt(pos) == '\n' ){
				if (chars.charAt(pos) == '\\' && chars.charAt(pos+1) == 'n'){
					pos++;
				}
				pos++;
				lineNo.add(pos);
			} else if (Character.isWhitespace(chars.charAt(pos))){
				pos++;
			} else{
				break;
			}
		}
		return pos;
	}
	
	public static enum State {
		START,
		IN_DIGIT,
		IN_IDENT,
		AFTER_EQ,
		AFTER_MIN,
		AFTER_OR,
		AFTER_OR_MIN,
		AFTER_NOT,
		AFTER_LT,
		AFTER_GT
	}
	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		int length = chars.length();
		State state = State.START;
		int startPos = 0;
		String sym = "";
		int ch;
		lineNo.add(0);
		if(length == 0){
			length = -1;
		}
		while(pos <= length){
			ch = pos< length ? chars.charAt(pos): -1;
			switch(state){
				case START:{
					pos = skipWhiteSpace(pos, length);
					if (pos == -1) {
						throw new IllegalCharException("Comment not closed appropriately before eof");
					}
					ch = pos < length ? chars.charAt(pos): -1;
					startPos = pos;
					switch(ch){
						case -1: {
							pos++;
						}  break;
						case ';': {
							tokens.add(new Token(Kind.SEMI, startPos, 1));
							pos++;
						} break;
						case ',': {
							tokens.add(new Token(Kind.COMMA, startPos, 1));
							pos++;
						} break;
						case '(': {
							tokens.add(new Token(Kind.LPAREN, startPos, 1));
							pos++;
						} break;
						case ')': {
							tokens.add(new Token(Kind.RPAREN, startPos, 1));
							pos++;
						} break;
						case '{': {
							tokens.add(new Token(Kind.LBRACE, startPos, 1));
							pos++;
						} break;
						case '}': {
							tokens.add(new Token(Kind.RBRACE, startPos, 1));
							pos++;
						} break;
						case '&': {
							tokens.add(new Token(Kind.AND, startPos, 1));
							pos++;
						} break;
						case '+': {
							tokens.add(new Token(Kind.PLUS, startPos, 1));
							pos++;
						} break;
						case '*': {
							tokens.add(new Token(Kind.TIMES, startPos, 1));
							pos++;
						} break;
						case '/': {
							tokens.add(new Token(Kind.DIV, startPos, 1));
							pos++;
						} break;
						case '%': {
							tokens.add(new Token(Kind.MOD, startPos, 1));
							pos++;
						} break;
						case '-': {
							pos++;
							state = State.AFTER_MIN;
						} break;
						case '|': {
							pos++;
							state = State.AFTER_OR;
						} break;
						case '=': {
							pos++;
							state = State.AFTER_EQ;
						} break;
						case '!': {
							pos++;
							state = State.AFTER_NOT;
						} break;
						case '<': {
							pos++;
							state = State.AFTER_LT;
						} break;
						case '>': {
							pos++;
							state = State.AFTER_GT;
						} break;
						default:
							if (Character.isJavaIdentifierStart(ch)) {
								pos++;
								state = State.IN_IDENT;
							} else if (Character.isDigit(ch)) {
								if (ch == '0'){
									tokens.add(new Token(Kind.INT_LIT, startPos, 1));
								}else{
									state = State.IN_DIGIT;
								}
								pos++;
							} else {
								throw new IllegalCharException("Not a legal symbol as per required language");
							}
					}
				}break;
				case IN_DIGIT:{
					if((pos<length) && (Character.isDigit(chars.charAt(pos)))){
						pos++;
					}else{
						try{
							sym = chars.substring(startPos, pos);
							if(reservedWord.containsKey(sym)){
								tokens.add(new Token(reservedWord.get(sym), startPos, pos - startPos));
							}else{
								Integer.parseInt(sym);
								tokens.add(new Token(Kind.INT_LIT, startPos, pos-startPos));
							}
						} catch (NumberFormatException e) {
							throw new IllegalNumberException("Provided number is out of range.");
						}
						state = State.START;
					}
				}break;
				case IN_IDENT:{
					if ((pos<length) && (Character.isJavaIdentifierPart(chars.charAt(pos)))){
						pos++;
					}else{
						sym = chars.substring(startPos, pos);
						if(reservedWord.containsKey(sym)){
							tokens.add(new Token(reservedWord.get(sym), startPos, pos - startPos));
						}else{
							tokens.add(new Token(Kind.IDENT, startPos, pos-startPos));
						}
						state = State.START;
					}
				}break;
				case AFTER_EQ:{
					if((pos<length) && (chars.charAt(pos) == '=')){//either pos or ch
						pos++;
						tokens.add(new Token(Kind.EQUAL, startPos, pos-startPos));
						state = State.START;
					}else{
						throw new IllegalCharException("Got single '=' but was expecting double '=='.");
					}
				}break;
				case AFTER_MIN:{
					if((pos<length) && (chars.charAt(pos) == '>')){
						pos++;
						tokens.add(new Token(Kind.ARROW, startPos, pos-startPos));
						state = State.START;
					}else{
						tokens.add(new Token(Kind.MINUS, startPos, 1));
						state = State.START;
					}
				} break;
				case AFTER_OR:{
					if((pos<length) && (chars.charAt(pos) == '-')){
						pos++;
						state = State.AFTER_OR_MIN;
					}else{
						tokens.add(new Token(Kind.OR, startPos, pos - startPos));
						state = State.START;
					}
				} break;
				case AFTER_OR_MIN: {
					if ((pos<length) && (chars.charAt(pos) == '>')) {
						pos++;
						tokens.add(new Token(Kind.BARARROW, startPos, pos-startPos));
						state = State.START;
					}else {
						tokens.add(new Token(Kind.OR, startPos, 1));
						tokens.add(new Token(Kind.MINUS, startPos+1, 1));
						state = State.START;
					}
				} break;
				case AFTER_NOT: {
					if ((pos<length) && (chars.charAt(pos) == '=')) {
						pos++;
						tokens.add(new Token(Kind.NOTEQUAL, startPos, pos-startPos));
						state = State.START;
					}else {
						tokens.add(new Token(Kind.NOT, startPos, 1));
						state = State.START;
					}
				} break;
				case AFTER_LT: {
					if ((pos<length) && (chars.charAt(pos) == '=')) {
						pos++;
						tokens.add(new Token(Kind.LE, startPos, pos-startPos));
						state = State.START;
					}else if ((pos<length) && (chars.charAt(pos) == '-')) {
						pos++;
						tokens.add(new Token(Kind.ASSIGN, startPos, pos-startPos));
						state = State.START;
					}else{
						tokens.add(new Token(Kind.LT, startPos, 1));
						state = State.START;
					}
				} break;
				case AFTER_GT: {
					if ((pos<length) && (chars.charAt(pos) == '=')) {
						pos++;
						tokens.add(new Token(Kind.GE, startPos, pos-startPos));
						state = State.START;
					}else {
						tokens.add(new Token(Kind.GT, startPos, pos-startPos));
						state = State.START;
					}
				} break;
				default: assert false;
			}
		}
		if (pos > length) {
			pos = length;
		}
		tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	ArrayList<Integer> lineNo = new ArrayList<Integer>();

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		return t.getLinePos();
	}


}
