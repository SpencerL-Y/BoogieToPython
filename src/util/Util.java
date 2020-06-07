package util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import boogie.controlflow.expression.CfgArrayAccessExpression;
import boogie.controlflow.expression.CfgArrayStoreExpression;
import boogie.controlflow.expression.CfgBinaryExpression;
import boogie.controlflow.expression.CfgBitVectorAccessExpression;
import boogie.controlflow.expression.CfgBitvecLiteral;
import boogie.controlflow.expression.CfgBooleanLiteral;
import boogie.controlflow.expression.CfgExpression;
import boogie.controlflow.expression.CfgFunctionApplication;
import boogie.controlflow.expression.CfgIdentifierExpression;
import boogie.controlflow.expression.CfgIfThenElseExpression;
import boogie.controlflow.expression.CfgIntegerLiteral;
import boogie.controlflow.expression.CfgQuantifierExpression;
import boogie.controlflow.expression.CfgRealLiteral;
import boogie.controlflow.expression.CfgStringLiteral;
import boogie.controlflow.expression.CfgUnaryExpression;
import boogie.controlflow.expression.CfgWildcardExpression;
import boogie.enums.BinaryOperator;
import boogie.enums.UnaryOperator;
import boogieamp.Boogieamp;

public class Util {
	public static String SOURCE_FILE_PATH = "ProgramFile/"; // Source file path
	public static String BOOGIE_FILE_NAME = "test.bpl"; // Boogie file name
	public static String RESULT_FILE_PREFIX = "";// the prefix of result file of running CPAChecker

	
	public static int PROGRAM_STATE = 0;
	
	public final static int PROGRAM_STATE_SAFE = 0;
	public final static int PROGRAM_STATE_ERROR = 1;
	public final static int CANNOTINTERPRETED = 2;
	
	
	public static boolean NewDirectory(String path)
	{
		File file = null;
		try{
			file = new File(path);
			if(!file.exists())
			{
				return file.mkdirs();
			}
		}catch (Exception e){
			return false;
		}finally{
			file = null;
		}
		return true;
	}
	public static ArrayList<String> getFiles(String path) 
	{ 
		ArrayList<String> files = new ArrayList<String>(); 
		File file = new File(path); 
		File[] tempList = file.listFiles(); 
		for (int i = 0; i < tempList.length; i++) 
		{ 
			if (tempList[i].isFile()) 
			{ 
				files.add(tempList[i].toString()); 
			}
		}
		return files;
	}
		
	/**
	 * 
	 * @param command: the string that we want to run.
	 */
	public static ArrayList<String> Invoke(String command)
	{
		ArrayList<String> result = new ArrayList<String>();
		try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedInputStream bis = new BufferedInputStream(
                    process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) {
               // System.out.println(line);
                result.add(line);
                
               
            }

            process.waitFor();
            if (process.exitValue() != 0) {
               // System.out.println("error!");
                result.add("error");
            }

            bis.close();
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return result;
	}
	
	public static String ExtractBinaryArithmeticExpressionToString(CfgExpression exp) {
		String result = "";
		if(exp instanceof CfgBinaryExpression) {
			
			if((((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPEQ) || 
			  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPGEQ)||
			  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPGT) ||
			  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPLEQ)||
			  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPLT) ||
			  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPLT) ||
			  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPNEQ)) {
				    CfgExpression left = ((CfgBinaryExpression)exp).getLeftOp();
					CfgExpression right = ((CfgBinaryExpression)exp).getRightOp();
					System.out.println(left.toString() + " - " + right.toString());
					return left.toString() + " - " + right.toString();
			} else {
				CfgExpression left = ((CfgBinaryExpression)exp).getLeftOp();
				CfgExpression right = ((CfgBinaryExpression)exp).getRightOp();
				return Util.ExtractBinaryArithmeticExpressionToString(left) + " | " + Util.ExtractBinaryArithmeticExpressionToString(right);
			}

			
		}
		return result;
	}
	
	public static void extractNonLogicExpressions(CfgExpression exp, ArrayList<CfgExpression> list) {
		if(exp instanceof CfgBinaryExpression) {
			if(((CfgBinaryExpression)exp).getOperator() == BinaryOperator.LOGICAND ||
			   ((CfgBinaryExpression)exp).getOperator() == BinaryOperator.LOGICOR) {
			    CfgExpression left = ((CfgBinaryExpression)exp).getLeftOp();
				CfgExpression right = ((CfgBinaryExpression)exp).getRightOp();
				Util.extractNonLogicExpressions(left, list);
				Util.extractNonLogicExpressions(right, list);
			} else {
				if((((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPEQ) || 
				   (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPGEQ)||
				   (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPGT) ||
						  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPLEQ)||
						  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPLT) ||
						  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPLT) ||
						  (((CfgBinaryExpression)exp).getOperator() == BinaryOperator.COMPNEQ)) {
					for(CfgExpression e : list) {
						if(e.equals(exp)) {
							return;
						}
					}
					list.add(exp);
				}
			}
		}
	}
	
	public static void 
	
	
	
	public static String ExpressionToPythonString(CfgExpression exp)
	{
		String result ="";
		if(exp instanceof CfgArrayAccessExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgArrayStoreExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgBinaryExpression)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			
			if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHDIV) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" / ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHMINUS) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" - ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHMOD) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" % ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHMUL) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" * ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHPLUS) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" + ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.BITVECCONCAT) {
				sb.append("? "); //TODO
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPEQ) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" == ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPGEQ) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" >= ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPGT) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" > ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPLEQ) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" <= ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPLT) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" < ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPNEQ) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" != ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPPO) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" <: ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICAND) {
				sb.append("");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" and ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICIFF) {
				String left = Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp());
				String right = Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp());
				
				sb.append("( ");
				sb.append(left);
				sb.append(" and ");
				sb.append(right);
				sb.append(")");
				sb.append(" or ");
				sb.append("( not (");
				sb.append(left);
				sb.append(") and not(");
				sb.append(right);
				sb.append(")))");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICIMPLIES) {
				sb.append("(not ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(") or ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICOR) {
				sb.append("");	
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(" or ");
				sb.append(Util.ExpressionToPythonString(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");		
			} else {
				throw new RuntimeException("Unknown Operator");
			}
			result =  sb.toString();
		}
		else if(exp instanceof CfgBitvecLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgBitVectorAccessExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgBooleanLiteral)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(((CfgBooleanLiteral) exp).getValue() ? "True" : "False");
			result = sb.toString();
		}
		else if(exp instanceof CfgFunctionApplication)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgIdentifierExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgIfThenElseExpression)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("( ");
			sb.append(Util.ExpressionToPythonString(((CfgIfThenElseExpression) exp).getThenExpression()));
			sb.append(" if");
			sb.append(Util.ExpressionToPythonString(((CfgIfThenElseExpression) exp).getCondition()));
			sb.append(" else");
			sb.append(Util.ExpressionToPythonString(((CfgIfThenElseExpression) exp).getElseExpression()));
			sb.append(")");
			result =  sb.toString();
			
		}
		else if(exp instanceof CfgIntegerLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgQuantifierExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgRealLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgStringLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgUnaryExpression)
		{
			//result = exp.toString();
			CfgUnaryExpression u_exp = (CfgUnaryExpression)exp;
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			if (u_exp.getOperator() == UnaryOperator.ARITHNEGATIVE) {
				sb.append("-"); //TODO	
			} else 	if (u_exp.getOperator() == UnaryOperator.LOGICNEG) {
				sb.append("not"); //TODO
			} else 	if (u_exp.getOperator() == UnaryOperator.OLD) {
				sb.append("\\old"); //TODO
			} else {
				throw new RuntimeException("Unknown unary operator");
			}
			sb.append(Util.ExpressionToPythonString(u_exp.getExpression()));
			sb.append(")");
			result =  sb.toString();
		}
		else if(exp instanceof CfgWildcardExpression)
		{
			//result = exp.toString();
			result = "random.choice([True, False])";
		}
		else
		{
			
		}
		return result;
	}
	public static String ExpressionToZ3String(CfgExpression exp)
	{
		String result ="";
		if(exp instanceof CfgArrayAccessExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgArrayStoreExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgBinaryExpression)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			
			if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHDIV) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("/ ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHMINUS) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("- ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHMOD) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("% ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHMUL) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("* ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.ARITHPLUS) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("+ ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.BITVECCONCAT) {
				sb.append(" "); //TODO
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("? ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPEQ) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("== ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPGEQ) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(">= ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPGT) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("> ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPLEQ) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("<= ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPLT) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("< ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPNEQ) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("!= ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.COMPPO) {
				sb.append(" ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append("<: ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICAND) {
				sb.append("And( ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(", ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append("))");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICIFF) {
				//TODO
//				sb.append("<==> ");
//				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
//				sb.append(" ");
//				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
//				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICIMPLIES) {
				//TODO
//				sb.append("==> ");
//				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
//				sb.append(" ");
//				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
//				sb.append(")");
			} else if (((CfgBinaryExpression) exp).getOperator() == BinaryOperator.LOGICOR) {
				sb.append("Or(");	
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getLeftOp()));
				sb.append(", ");
				sb.append(Util.ExpressionToZ3String(((CfgBinaryExpression) exp).getRightOp()));
				sb.append("))");		
			} else {
				throw new RuntimeException("Unknown Operator");
			}
			result =  sb.toString();
		}
		else if(exp instanceof CfgBitvecLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgBitVectorAccessExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgBooleanLiteral)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(((CfgBooleanLiteral) exp).getValue() ? "True" : "False");
			result = sb.toString();
		}
		else if(exp instanceof CfgFunctionApplication)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgIdentifierExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgIfThenElseExpression)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("If( ");
			sb.append(Util.ExpressionToZ3String(((CfgIfThenElseExpression) exp).getCondition()));
			sb.append(", ");
			sb.append(Util.ExpressionToZ3String(((CfgIfThenElseExpression) exp).getThenExpression()));
			sb.append(", ");
			sb.append(Util.ExpressionToZ3String(((CfgIfThenElseExpression) exp).getElseExpression()));
			sb.append(")");
			result =  sb.toString();
			
		}
		else if(exp instanceof CfgIntegerLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgQuantifierExpression)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgRealLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgStringLiteral)
		{
			result = exp.toString();
		}
		else if(exp instanceof CfgUnaryExpression)
		{
			//result = exp.toString();
			CfgUnaryExpression u_exp = (CfgUnaryExpression)exp;
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			if (u_exp.getOperator() == UnaryOperator.ARITHNEGATIVE) {
				sb.append("-"); //TODO	
			} else 	if (u_exp.getOperator() == UnaryOperator.LOGICNEG) {
				sb.append("Not"); //TODO
			} else 	if (u_exp.getOperator() == UnaryOperator.OLD) {
				sb.append("\\old"); //TODO
			} else {
				throw new RuntimeException("Unknown unary operator");
			}
			sb.append(Util.ExpressionToPythonString(u_exp.getExpression()));
			sb.append(")");
			result =  sb.toString();
		}
		else if(exp instanceof CfgWildcardExpression)
		{
			//result = exp.toString();
			result = "random.choice([1, 0])";
		}
		else
		{
			
		}
		return result;
	}
	public static void WriteToFile(String filename,String content,boolean IsAdd)
	{
		File f = new File(filename);
		try {
			FileWriter fw = new FileWriter(f, IsAdd);//true,add
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//ONLY FOR TEST
	public static void main(String[] args)
	{
		System.out.println(Integer.MAX_VALUE);
}
}
