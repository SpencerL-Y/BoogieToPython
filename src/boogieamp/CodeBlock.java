package boogieamp;

import java.util.ArrayList;
import java.util.HashMap;

import boogie.ast.location.ILocation;
import boogie.controlflow.CfgVariable;
import boogie.controlflow.expression.CfgExpression;

public abstract class CodeBlock {
	public CodeBlock next ;
	public String index;
	public String pre_index;
	public ArrayList<CfgVariable> variables = new ArrayList<CfgVariable>();
	public ILocation loc;
	
	public CodeBlock(String index,String pre_index,ArrayList<CfgVariable> variables)
	{
		this.pre_index = pre_index;
		this.index = index;
		this.variables = variables;
		this.loc = null;
	}

	
	public abstract HashMap<CfgVariable,String> ToZ3Code(HashMap<CfgVariable,CfgExpression> substitute);
	public abstract HashMap<CfgVariable,String> ToPythonCode(HashMap<CfgVariable,CfgExpression> substitute);
	public abstract String GetPythonLoopCondition(HashMap<CfgVariable,CfgExpression> substitute);
	public abstract String GetZ3LoopCondition(HashMap<CfgVariable,CfgExpression> substitute);
	public abstract void getTemplateArray(ArrayList<CfgExpression> expList, ArrayList<TemplateArray> templateArrayList, HashMap<CfgVariable, Integer> varIntMap);
}
