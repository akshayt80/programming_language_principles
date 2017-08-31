package cop5556sp17;

import java.util.ArrayList;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv;

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	//command line arguments index, Slot number counter and local variable array
	int paramDecArgs = 0;
	int slotNumber = 1;
	ArrayList<VarAttrs> localVariables = new ArrayList<>();
	// Attributes of a variable used for storing in local variable array
	static class VarAttrs{
		Label startLabel;
		Label endLabel;
		int slotNumber;
		Dec dec;
	}
	// Properties of a binary chain to be passed from parent to child nodes
	static class BinaryChainOpInfo {
		Kind arrowKind;
		String position;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		//add the visitlocalvariable method for all variable in localVariables array
		for (VarAttrs varAttrs: localVariables){
			mv.visitLocalVariable(varAttrs.dec.getIdent().getText(), varAttrs.dec.getType().getJVMTypeDesc(), null, varAttrs.startLabel, varAttrs.endLabel, varAttrs.slotNumber);
		}
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		
		BinaryChainOpInfo bChainInfo = new BinaryChainOpInfo();
		bChainInfo.arrowKind = binaryChain.getArrow().kind;
		
		Chain chain = binaryChain.getE0();
		bChainInfo.position = "left";
		
		chain.visit(this, bChainInfo);
		if (chain.typeName == TypeName.URL) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		} else if (chain.typeName == TypeName.FILE){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		
		ChainElem chainElem = binaryChain.getE1();
		bChainInfo.position = "right";
		chainElem.visit(this, bChainInfo);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		if (binaryExpression.typeName == TypeName.INTEGER){
			
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
			
			switch (binaryExpression.getOp().kind) {
				case PLUS: mv.visitInsn(IADD);
					break;
				case MINUS: mv.visitInsn(ISUB);
					break;
				case TIMES: mv.visitInsn(IMUL);
					break;
				case DIV: mv.visitInsn(IDIV);
					break;
				case MOD: mv.visitInsn(IREM);
				default:
					break;
			}
		} else if (binaryExpression.typeName == TypeName.BOOLEAN){
			
			Label startLabel = new Label();
			Label endLabel = new Label();
			
			switch (binaryExpression.getOp().kind) {
				case AND: {
					binaryExpression.getE0().visit(this, arg);
					binaryExpression.getE1().visit(this, arg);
					mv.visitInsn(IAND);
				} break;
				case OR: {
					binaryExpression.getE0().visit(this, arg);
					binaryExpression.getE1().visit(this, arg);
					mv.visitInsn(IOR);
				} break;
				case EQUAL:
				case NOTEQUAL:
				case GT:
				case GE:
				case LT:
				case LE:{
					
					binaryExpression.getE0().visit(this, arg);
					binaryExpression.getE1().visit(this, arg);
					
					int op = 0;
					
					switch (binaryExpression.getOp().kind){
						case EQUAL: op = IF_ICMPEQ;
							break;
						case NOTEQUAL: op = IF_ICMPNE;
							break;
						case GT: op = IF_ICMPGT;
							break;
						case GE: op = IF_ICMPGE;
							break;
						case LT : op = IF_ICMPLT;
							break;
						case LE : op = IF_ICMPLE;
							break;
						default:
							break;
					}
					
					mv.visitJumpInsn(op, startLabel);
					mv.visitInsn(ICONST_0);
					mv.visitJumpInsn(GOTO, endLabel);
					mv.visitLabel(startLabel);
					mv.visitInsn(ICONST_1);
					mv.visitLabel(endLabel);
				
				} break;
				default:
					break;
			}
		} else if (binaryExpression.typeName == TypeName.IMAGE){
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
			String func_name = null;
			String func_desc = null;
			switch (binaryExpression.getOp().kind) {
				case PLUS: {
					func_name = "add";
					func_desc = PLPRuntimeImageOps.addSig;
				}break;
				case MINUS: {
					func_name = "sub";
					func_desc = PLPRuntimeImageOps.subSig;
				} break;
				case TIMES: {
					func_name = "mul";
					func_desc = PLPRuntimeImageOps.mulSig;
				} break;
				case DIV: {
					func_name = "div";
					func_desc = PLPRuntimeImageOps.divSig;
				} break;
				case MOD: {
					func_name = "mod";
					func_desc = PLPRuntimeImageOps.modSig;
				} break;
				default:
					break;
			}
			if ((binaryExpression.getE0().getType() == TypeName.INTEGER) && (binaryExpression.getE1().getType() == TypeName.IMAGE)) {
				CodeGenUtils.genPrint(DEVEL, mv, "SWAPPING integer and image");
				mv.visitInsn(SWAP);
			}
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, func_name, func_desc, false);
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		
		Label startLabel = new Label();
		Label endLabel = new Label();
		
		mv.visitLabel(startLabel);
		
		for (Dec dec : block.getDecs()) {
			// Assign all the properties to respective fields
			VarAttrs varAttrs = new VarAttrs();
			varAttrs.dec = dec;
			varAttrs.endLabel = endLabel;
			varAttrs.startLabel = startLabel;
			varAttrs.slotNumber = slotNumber;
			if (dec.getType() == TypeName.FRAME){
				mv.visitInsn(ACONST_NULL);
				mv.visitVarInsn(ASTORE, slotNumber);
			}
			dec.visit(this, arg);
			// Add to local variable array which will be used to visit local variables
			localVariables.add(varAttrs);
		}
		
		for (Statement statement : block.getStatements()) {
			statement.visit(this, arg);
			if (statement instanceof Chain){
				//Poping the value which have been put by some chain method
				mv.visitInsn(POP);
			}
		}
		
		mv.visitLabel(endLabel);
		
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//Load constant value
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		switch (constantExpression.getFirstToken().kind){
			case KW_SCREENWIDTH:mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
			 break;
			case KW_SCREENHEIGHT:mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
				break;
			default: break;
		}
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// assign slotnumber to slotnumber attribute of Dec which represents
		// index in local variable array
		declaration.slotNum = slotNumber++;
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, arg);
		
		String func_name = null;
		
		switch (filterOpChain.firstToken.kind) {
			case OP_BLUR:func_name = "blurOp"; 
				break;
			case OP_CONVOLVE: func_name = "convolveOp";
				break;
			case OP_GRAY: func_name = "grayOp";
				break;
			default:
				break;
		}
		
		BinaryChainOpInfo bChainInfo = (BinaryChainOpInfo) arg;
		
		if (bChainInfo.arrowKind == Kind.ARROW) {
			mv.visitInsn(ACONST_NULL);
		} else {
			mv.visitInsn(DUP);
			mv.visitInsn(SWAP);
		}
		// Call appropriate filter operation based on above condition
		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, func_name, PLPRuntimeFilterOps.opSig, false);
		
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);
		
		String func_name = null;
		String func_desc = null;
		
		switch (frameOpChain.getFirstToken().kind) {
			case KW_SHOW: {
				func_name = "showImage";
				func_desc = PLPRuntimeFrame.showImageDesc;
			} break;
			case KW_HIDE: {
				func_name = "hideImage";
				func_desc = PLPRuntimeFrame.hideImageDesc;
			} break;
			case KW_XLOC: {
				func_name = "getXVal";
				func_desc = PLPRuntimeFrame.getXValDesc;
			} break;
			case KW_YLOC: {
				func_name = "getYVal";
				func_desc = PLPRuntimeFrame.getYValDesc;
			} break;
			case KW_MOVE: {
				func_name = "moveFrame";
				func_desc = PLPRuntimeFrame.moveFrameDesc;
			} break;
			default:
				break;
		}
		
		mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, func_name, func_desc, false);
		
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		
		BinaryChainOpInfo bChainInfo = (BinaryChainOpInfo) arg;
		
		Dec dec = identChain.dec;
		
		if (bChainInfo.position == "left"){
			switch (dec.getType()){
				case INTEGER:
				case BOOLEAN:{
					if (dec instanceof ParamDec) {
						mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), identChain.dec.getType().getJVMTypeDesc());
					} else {
						mv.visitVarInsn(ILOAD, dec.slotNum);
					}
				} break;
				case IMAGE:
				case FRAME: mv.visitVarInsn(ALOAD, dec.slotNum);
					break;
				case FILE:
				case URL: mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), dec.getType().getJVMTypeDesc());
					break;
				default:
					break;
			}
		} else {
			switch (dec.getType()){
				case INTEGER: {
					mv.visitInsn(DUP);
					if (dec instanceof ParamDec){
						mv.visitFieldInsn(PUTSTATIC, className, identChain.getFirstToken().getText(), dec.getType().getJVMTypeDesc());
					} else {
						mv.visitVarInsn(ISTORE, dec.slotNum);
					}
				} break;
				case IMAGE: mv.visitInsn(DUP);mv.visitVarInsn(ASTORE, dec.slotNum);
					break;
				case FILE: {
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), dec.getType().getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
					//mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), dec.getType().getJVMTypeDesc());
				} break;
				case FRAME: {
					mv.visitVarInsn(ALOAD, dec.slotNum);
					//mv.visitInsn(ACONST_NULL);
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, dec.slotNum);
				} break;
				default:
					break;
			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// If the dec is an instance of ParamDec then it is a field otherwise a variable
		if (identExpression.dec instanceof ParamDec) {
			mv.visitFieldInsn(GETSTATIC, className, identExpression.getFirstToken().getText(), identExpression.dec.getType().getJVMTypeDesc());
		} else if (identExpression.typeName == TypeName.INTEGER || identExpression.typeName == TypeName.BOOLEAN) {
			mv.visitVarInsn(ILOAD, identExpression.dec.slotNum);
		} else {
			mv.visitVarInsn(ALOAD, identExpression.dec.slotNum);
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// If the dec is an instance of ParamDec then it is a field otherwise a variable
		if (identX.dec instanceof ParamDec){
		
			mv.visitFieldInsn(PUTSTATIC, className, identX.getFirstToken().getText(), identX.dec.getType().getJVMTypeDesc());
		
		} else if (identX.dec.getType() == TypeName.INTEGER || identX.dec.getType() == TypeName.BOOLEAN){
			
			mv.visitVarInsn(ISTORE, identX.dec.slotNum);
		
		} else if (identX.dec.getType() == TypeName.IMAGE){
		
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
			mv.visitVarInsn(ASTORE, identX.dec.slotNum);
		
		} else {
		
			mv.visitVarInsn(ASTORE, identX.dec.slotNum);
		
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		
		Label label = new Label();
		
		ifStatement.getE().visit(this, arg);
		//ifeq compares with 0 which will be there only if the condition is false
		mv.visitJumpInsn(IFEQ, label);
		
		ifStatement.getB().visit(this, arg);
		
		mv.visitLabel(label);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		
		Tuple tuple = imageOpChain.getArg();
		tuple.visit(this, arg);
		
		switch(imageOpChain.getFirstToken().kind) {
			case KW_SCALE:mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
				break;
			case OP_WIDTH: mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", PLPRuntimeImageOps.getWidthSig, false);
				break;
			case OP_HEIGHT: mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", PLPRuntimeImageOps.getHeightSig, false);
				break;
			default:
				break;
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//create a field
		fv = cw.visitField(ACC_STATIC, paramDec.getIdent().getText(), paramDec.getType().getJVMTypeDesc(), null, null);
		fv.visitEnd();
		
		
		switch(paramDec.getType()){
			case INTEGER: {
				//access the String[] args array
				mv.visitVarInsn(ALOAD, 1);
				//index of args array
				mv.visitIntInsn(BIPUSH, paramDecArgs++);
				//load the value at that index
				mv.visitInsn(AALOAD);
				//equivalent code for Integer.parseInt()
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				//Print for debugging
				CodeGenUtils.genPrint(DEVEL, mv, "\n paramdec: "+paramDec.getIdent().getText()+"=");
				CodeGenUtils.genPrintTOS(DEVEL, mv, TypeName.INTEGER);
			} break;
			case BOOLEAN: {
				//access the String[] args array
				mv.visitVarInsn(ALOAD, 1);
				//index of args array
				mv.visitIntInsn(BIPUSH, paramDecArgs++);
				//load the value at that index
				mv.visitInsn(AALOAD);
				//equivalent code for Boolean.parseBoolean()
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				//Print for debugging
				CodeGenUtils.genPrint(DEVEL, mv, "\n paramdec: "+paramDec.getIdent().getText()+"=");
				CodeGenUtils.genPrintTOS(DEVEL, mv, TypeName.BOOLEAN);
			}break;
			case URL: {
				//access the String[] args array
				mv.visitVarInsn(ALOAD, 1);
				//index of args array
				mv.visitIntInsn(BIPUSH, paramDecArgs++);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			} break;
			case FILE: {
				mv.visitTypeInsn(NEW, "java/io/File");
				//Duplicating for constructor
				mv.visitInsn(DUP);
				//access the String[] args array
				mv.visitVarInsn(ALOAD, 1);
				//index of args array
				mv.visitIntInsn(BIPUSH, paramDecArgs++);
				//load the value at that index
				mv.visitInsn(AALOAD);
				mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			}
			default: break;
		}
		//Assign the value to the field
		mv.visitFieldInsn(PUTSTATIC, className, paramDec.getIdent().getText(), paramDec.getType().getJVMTypeDesc());
		
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		if (sleepStatement.getE().typeName == TypeName.INTEGER) {
			mv.visitInsn(I2L);
		}
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for (Expression exp: tuple.getExprList()){
			exp.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		
		Label startLabel = new Label();
		Label endLabel = new Label();
		
		mv.visitLabel(startLabel);
		
		whileStatement.getE().visit(this, arg);
		//ifeq compares with 0 which will be there only if the condition is false
		mv.visitJumpInsn(IFEQ, endLabel);
		whileStatement.getB().visit(this, arg);
		//Goto will make the control jump to the specified label
		mv.visitJumpInsn(GOTO, startLabel);
		
		mv.visitLabel(endLabel);
		
		return null;
	}

}
