package boogieamp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import typechecker.TypeChecker;
import boogie.ProgramFactory;
import boogie.ast.location.ILocation;
import boogie.controlflow.BasicBlock;
import boogie.controlflow.CfgProcedure;
import boogie.controlflow.CfgVariable;
import boogie.controlflow.DefaultControlFlowFactory;
import boogie.controlflow.expression.*;
import boogie.controlflow.statement.CfgAssignStatement;
import boogie.controlflow.statement.CfgAssumeStatement;
import boogie.controlflow.statement.CfgHavocStatement;
import boogie.controlflow.statement.CfgStatement;
import boogie.enums.BinaryOperator;
import boogie.type.BoogieType;
import util.Util;


public class Boogieamp {
	public String FileName;
	//public Map<String,String> var;
	public CfgProcedure cfg;
	public String ProcedureName;
	public HashMap<String, CfgVariable> globalVars= new HashMap<String,CfgVariable>();
	public CodeBlock root;
	public ArrayList<CfgVariable> variables;
	public CfgAssumeStatement loopCondition;
	
	public String[] stringOfLoopCondition;
	public String Z3Code;
	private String prefix;
	
	public Boogieamp(String filename,String procedureName,int indexOfLoop)
	{
		this.FileName = filename;
		this.ProcedureName = procedureName;
		this.ParsingProgram(indexOfLoop);
	}
	public Boogieamp(String filename,int indexOfLoop)
	{
		this.FileName = filename;
		this.ProcedureName = "";
		this.ParsingProgram(indexOfLoop);
	}
	private void ParsingProgram(int indexOfLoop)
	{
		ProgramFactory pf;
		try {
			pf = new ProgramFactory(this.FileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		TypeChecker tc = new TypeChecker(pf.getASTRoot());
		
		DefaultControlFlowFactory cff = new DefaultControlFlowFactory(pf.getASTRoot(), tc);
		for(CfgProcedure element : cff.getProcedureCFGs())
		{
			String procedureName = element.getProcedureName();
			if(this.ProcedureName.equals(""))
			{
				this.ProcedureName = procedureName;
				this.cfg = element.clone();
				break;
			}
			else
			{
				if(element.getProcedureName().equals(this.ProcedureName))
				{
					this.cfg = element.clone();
					break;
				}
			}
		}
		this.globalVars.putAll(cff.getGlobalVars());
		this.variables = this.GetVariables();
		this.root = ParsingProgramStructure(indexOfLoop);
		
	}
	
	public String GeneratePythonCode(int numOfG,String[] template)
	{
		HashMap<CfgVariable,CfgExpression> substitute = new HashMap<CfgVariable,CfgExpression>();
		for(int i=0;i<this.variables.size();i++)
		{
			CfgVariable var = this.variables.get(i);
			CfgVariable var_ = new CfgVariable("x["+i+"]", var.getType(), var.isConstant(),
					var.isGlobal(), var.isUnique(), var.isComplete());
			CfgIdentifierExpression var_exp = new CfgIdentifierExpression(this.cfg.getLocation(), var_);
			substitute.put(var, (CfgExpression)var_exp);
		}
		HashMap<CfgVariable,String> python_part = this.root.ToPythonCode((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		HashMap<CfgVariable,String> Z3_part = this.root.ToZ3Code((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		String python_loop_condition = this.root.GetPythonLoopCondition((HashMap<CfgVariable,CfgExpression>)substitute.clone());//Util.ExpressionToPythonString(this.loopCondition.getCondition().substitute(substitute));
		
		String python_statement = "[";
		for(CfgVariable v : this.variables)
		{
			python_statement += python_part.get(v)+", ";
		}
		python_statement += "]";
		
		String Z3_loop_condition = this.root.GetZ3LoopCondition((HashMap<CfgVariable,CfgExpression>)substitute.clone());//Util.ExpressionToZ3String(this.loopCondition.getCondition().substitute(substitute));
		String Z3_statement = "[";
		for(CfgVariable v : this.variables)
		{
			Z3_statement += Z3_part.get(v)+", ";
		}
		Z3_statement += "]";
		boolean IsInt = true;
		for(CfgVariable v: this.variables)
		{
			if(v.getType() != BoogieType.intType)
			{
				IsInt = false;
				break;
			}
		}
		
		return GenerateCode(python_statement,python_loop_condition,Z3_statement,Z3_loop_condition,numOfG,IsInt,template);
	}
	
	private String GenerateCode(String python_statement, String python_loop_condition,
								String Z3_statement,     String Z3_loop_condition,
								int numOfG, boolean IsInt, String[] template)
	{
		/**
		 [lambda x: x[0] ** 2 + x[1] ** 2 <= 1,
         lambda x: [x[0] - x[1] ** 2 + 1, x[1] + x[0] ** 2 - 1],
         2,
         1,
         [[0, 0, 1],
          [0, 1, 1],
          [1, 0, 1]],
          #prime condition
         lambda x :[x[0] - x[1] ** 2 + 1, x[1] + x[0] ** 2 - 1],
         lambda x: x[0] ** 2 + x[1] ** 2 <= 1],
		 */
		int numOfVar = this.variables.size();
		String result = "[\n";
		result +="lambda x :" +python_loop_condition+",\n";
		result +="lambda x :" + python_statement+",\n";
		result += ""+numOfVar+",\n";
		result += ""+numOfG+",\n";
//		for(int i=0;i<numOfG;i++)
//		{
//			result+="[\n";
//			for(int j=0;j<=numOfVar;j++)
//			{
//				result+=" [";
//				for (int k=0;k<=numOfVar;k++)
//				{
//					if(j==k || k==numOfVar)
//						result +="1, ";
//					else
//						result +="0, ";
//				}
//				result += " ],\n";
//			}
//			result+="],\n";
//		}
		for(int i=0;i<numOfG;i++)
		{
			result += template[i]+",\n";
		}
		result +="lambda x :" + Z3_statement+",\n";
		result +="lambda x :" + Z3_loop_condition+",\n";
		if(IsInt)
		{
			result +="False,\n";
		}
		result +="]\n";
		return result;
		
		
	}
	
	public ArrayList<TemplateArray> generateTemplateArray() {
		ArrayList<TemplateArray> templateArray = new ArrayList<TemplateArray>();
		ArrayList<CfgExpression> arithArray = new ArrayList<CfgExpression>();
		HashMap<CfgVariable, Integer> varIntMap = new HashMap<CfgVariable, Integer>();
		int i = 0;
		for(CfgVariable v : this.root.variables) {
			varIntMap.put(v, i);
			i ++;
		}
		this.root.getTemplateArray(arithArray, templateArray, varIntMap);
		for(TemplateArray a : templateArray) {
			System.out.println(a.toString());
		}
		i = 0;
		for(CfgExpression e : arithArray) {
			System.out.println(i + ": " + e.toString());
			i++;
		}
		return templateArray;
	}
	
	private CodeBlock ParsingProgramStructure(int indexOfLoop)
	{
//		HashMap<CfgVariable,String> result = new HashMap<CfgVariable,String>();
//		for(CfgVariable v : this.variables)
//		{
//			result.put(v, "("+v+")");
//		}
		HashSet<BasicBlock> done = new HashSet<BasicBlock>();
		LinkedList<BasicBlock> todo = new LinkedList<BasicBlock>();
		BasicBlock loop = null;
		int count =0;
		todo.push(this.cfg.getRootNode());
		while (!todo.isEmpty()) {
			BasicBlock b = todo.pop();
			String[] label = b.getLabel().trim().split("#");
			if(label[label.length-1].trim().equals("loopentry"))
			{
				count++;
				//if(count == indexOfLoop)
				//{
					loop = b;
					this.prefix = b.getLabel();
					this.loopCondition = (CfgAssumeStatement) b.getStatements().get(0);
 					//break;
				//}
			}
			done.add(b);
			for (BasicBlock next : b.getSuccessors()) {
				if (!todo.contains(next) && !done.contains(next)) {
					todo.add(next);
				}
			}
		}
		if(loop != null)
		{
			this.done = new HashSet<BasicBlock>();
			CodeBlock loop_code = this.ParsingBlock(loop, "1","0",new LinkedList<CodeBlock>());
			//this.root = loop_code;
			return loop_code;
		}
		return null;
	}

	private HashSet<BasicBlock> done;
	private CodeBlock ParsingBlock(BasicBlock root,String index,String pre_index,LinkedList<CodeBlock> if_Block_For_Next)
	{
		if(root.getLabel().indexOf(prefix)==-1) return null;
		if(this.done.contains(root)) return null;
		String split_mark = "!";
		LinkedList<CfgStatement> assign_statement = root.getStatements();
		CodeBlock assign_block = this.ParsingStatement(assign_statement, index,pre_index,root.getLocationTag());
		
		this.done.add(root);
		if(root.getSuccessors().size()>1)// if & else
		{
			CodeBlock then_block = null,else_block = null;
			IfCodeBlock if_block = new IfCodeBlock(index+split_mark+"0",index,this.variables);
			if_Block_For_Next.push(if_block);
			for(BasicBlock v: root.getSuccessors())
			{
				if(this.done.contains(v)) continue;
				String[] tmp = v.getLabel().split("#");
				if(tmp[tmp.length-1].trim().equals("then"))
				{
					then_block = this.ParsingBlock(v, index+split_mark+"1",index,if_Block_For_Next);
					CfgAssumeStatement tmp_statement = (CfgAssumeStatement)(v.getStatements().getFirst());
					if(tmp_statement.getCondition() instanceof CfgWildcardExpression)
					{
						if_block.condition = tmp_statement.getCondition();
					}
					else
					{
						if_block.condition = tmp_statement.getCondition().clone();
					}
				}
				else if(tmp[tmp.length-1].trim().equals("else"))
				{
					else_block = this.ParsingBlock(v, index+split_mark+"1",index,if_Block_For_Next);
				}
				else
				{
					Util.PROGRAM_STATE = Util.PROGRAM_STATE_ERROR;
					break;
				}
			}
			if_block.then_block = then_block;
			if_block.else_block = else_block;
			assign_block.next = if_block;
//			then_block.next = if_block.next;
//			else_block.next = if_block.next;
			CodeBlock tmp_iter = else_block;
			while(tmp_iter.next!=null)
			{
				tmp_iter = tmp_iter.next;
			}
			tmp_iter.next = if_block.next;
			
			tmp_iter = then_block;
			while(tmp_iter.next!=null)
			{
				tmp_iter = tmp_iter.next;
			}
			tmp_iter.next = if_block.next;
		}
		else if(root.getSuccessors().size()==1)// join or assign
		{
			for(BasicBlock v: root.getSuccessors())
			{
				if(this.done.contains(v)) continue;
				String[] tmp = v.getLabel().split("#");
				
				if(tmp[tmp.length-1].trim().equals("join"))
				{
					CodeBlock tmp_codeblock= if_Block_For_Next.pop();
					String[] tmp_index = tmp_codeblock.index.split(split_mark);
					tmp_index[tmp_index.length-2] = ""+(Integer.parseInt(tmp_index[tmp_index.length-2])+1);
					String tmp_str = "";
					for(int i=0;i<tmp_index.length-1;i++)
					{
						tmp_str+=tmp_index[i]+split_mark;
					}
					tmp_str = tmp_str.substring(0, tmp_str.length()-split_mark.length());
					tmp_codeblock.next = this.ParsingBlock(v, tmp_str,tmp_codeblock.index,if_Block_For_Next);//Original Program
					
					
//					assign_block.next = this.ParsingBlock(v, tmp_str,index,if_Block_For_Next);
				}
				else
				{
					String[] tmp_index = index.split(split_mark);
					tmp_index[tmp_index.length-1] = ""+(Integer.parseInt(tmp_index[tmp_index.length-1])+1);
					String tmp_str = "";
					for(int i=0;i<tmp_index.length;i++)
					{
						tmp_str+=tmp_index[i]+split_mark;
					}
					tmp_str = tmp_str.substring(0, tmp_str.length()-split_mark.length());
					assign_block.next = this.ParsingBlock(v, tmp_str,index,if_Block_For_Next);
				}
			}
		}
		else
		{
			return assign_block;
		}
		return assign_block;
	}
	private CodeBlock ParsingStatement(LinkedList<CfgStatement> statement,String index,String pre_index,ILocation loc)
	{
		HashMap<CfgVariable,CfgExpression> result = new HashMap<CfgVariable,CfgExpression>();
		CfgExpression condition = new CfgBooleanLiteral(loc, BoogieType.boolType, true);
		for(CfgVariable v : this.variables)
		{
			result.put(v,new CfgIdentifierExpression(loc,v));
		}
		//LinkedList<CfgStatement> assignment = new LinkedList<CfgStatement>();
		int i=0;
		for(i=0;i<statement.size();i++)
		{
			CfgStatement cfg_statement = statement.get(i);
			if(cfg_statement instanceof CfgAssignStatement)
			{
				//assignment.add(cfg_statement);
				CfgAssignStatement tmp = (CfgAssignStatement)cfg_statement;
				//System.out.println(tmp);
				CfgIdentifierExpression[] left = tmp.getLeft();
				CfgExpression[] right = tmp.getRight();
				HashMap<CfgVariable,CfgExpression> tmp_result = (HashMap<CfgVariable,CfgExpression>)result.clone();
				for(int j=0;j<left.length;j++)
				{
					//System.out.println(left[j]);
					if(right[j].toString().equals( "__VERIFIER_nondet_int()"))
					{
						result.put(left[j].getVariable(), new CfgIntegerLiteral(right[j].getLocation(), BoogieType.intType, (long) 0));
					}
					else
						result.put(left[j].getVariable(), right[j].substitute(tmp_result));
					
				}
				
			}
			else if(cfg_statement instanceof CfgHavocStatement)
			{
				Util.PROGRAM_STATE = Util.CANNOTINTERPRETED;
				continue;
			}
			else if (cfg_statement instanceof CfgAssumeStatement)
			{
				CfgAssumeStatement tmp = (CfgAssumeStatement)cfg_statement;
				if(tmp.getCondition() instanceof CfgWildcardExpression)
				{
					condition = new CfgBinaryExpression(tmp.getLocation(), BoogieType.boolType, BinaryOperator.LOGICAND, condition, tmp.getCondition());
				}
				else
				{
					condition = new CfgBinaryExpression(tmp.getLocation(), BoogieType.boolType, BinaryOperator.LOGICAND, condition, tmp.getCondition().substitute(result));
				}
				//Util.PROGRAM_STATE = Util.CANNOTINTERPRETED;
				continue;
			}
			else 
			{
				continue;
			}
		}
//		HashMap<CfgVariable,CfgExpression> assignments = new HashMap<CfgVariable,CfgExpression>();
//		if(statement.size()>0)
//		{
//			assignments = this.GeneratePartAssignment(assignment, this.variables,statement.getFirst().getLocation());
//			
//		}
		AssignCodeBlock assign_block = new AssignCodeBlock(index,pre_index,this.variables,result,condition);
		//assign_block.next = this.ParsingStatement(statement, index+1);
		
		return assign_block;
	}
	private HashMap<CfgVariable,CfgExpression> GeneratePartAssignment(LinkedList<CfgStatement> assignment,ArrayList<CfgVariable> var,ILocation loc)
	{
		HashMap<CfgVariable,CfgExpression> result = new HashMap<CfgVariable,CfgExpression>();
		for(CfgVariable v : var)
		{
			result.put(v,new CfgIdentifierExpression(loc, v));
		}
		for(CfgStatement v : assignment)
		{
			if(v instanceof CfgAssignStatement)
			{
				CfgAssignStatement tmp = (CfgAssignStatement)v;
				CfgIdentifierExpression[] left = tmp.getLeft();
				CfgExpression[] right = tmp.getRight();
				
				for(int i=0;i<left.length;i++)
				{
					if(right[i].toString().equals( "__VERIFIER_nondet_int()"))
					{
						result.put(left[i].getVariable(), new CfgIntegerLiteral(right[i].getLocation(), BoogieType.intType, (long) 0));
					}
					else
						result.put(left[i].getVariable(), right[i].substitute(result));
					
				}
			}
			
		}
		return result;
	}
	private ArrayList<CfgVariable> GetVariables()
	{
		ArrayList<CfgVariable> var = new ArrayList<CfgVariable>();
		var.addAll(this.globalVars.values());
		CfgVariable[] tmp_in = this.cfg.getInParams();
		for(int i=0;i<tmp_in.length;i++)
		{
			var.add(tmp_in[i]);
		}
		CfgVariable[] tmp_out = this.cfg.getOutParams();
		for(int i=0;i<tmp_out.length;i++)
		{
			var.add(tmp_out[i]);
		}
		CfgVariable[] tmp_local = this.cfg.getLocalVars();
		for(int i=0;i<tmp_local.length;i++)
		{
			var.add(tmp_local[i]);
		}
		return var;
	}
	public static void main(String[] args)
	{
//		Boogieamp b = new Boogieamp("ProgramFile/test.bpl","F");
//		b.GenerateRequiedInformationOfProgram("a","body");
//		b.ToZ3File("test.smt2");
	}

}
