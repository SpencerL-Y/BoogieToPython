package boogieamp;

import java.util.ArrayList;
import java.util.HashMap;

import util.Util;
import boogie.controlflow.CfgVariable;
import boogie.controlflow.expression.CfgExpression;
import boogie.controlflow.expression.CfgIdentifierExpression;

public class IfCodeBlock extends CodeBlock {
	public CodeBlock then_block;
	public CodeBlock else_block;
	public CfgExpression condition;

	public IfCodeBlock(String index,String pre_index,ArrayList<CfgVariable> variables) {
		super(index,pre_index,variables);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<CfgVariable,String> ToZ3Code(HashMap<CfgVariable,CfgExpression> substitute) {
		// TODO Auto-generated method stub
		HashMap<CfgVariable,String> result = new HashMap<CfgVariable,String>();
		HashMap<CfgVariable,String> then_block = this.then_block.ToZ3Code((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		HashMap<CfgVariable,String> else_block = this.else_block.ToZ3Code((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		
		for(CfgVariable v : this.variables)
		{
			result.put(v, "If("+ Util.ExpressionToZ3String(this.condition.substitute((HashMap<CfgVariable,CfgExpression>)substitute.clone()))
								+", "+then_block.get(v)+", "+else_block.get(v)+" )");
		}
		if(this.next!=null)
		{
			
		}
		return result;
	}

	@Override
	public HashMap<CfgVariable, String> ToPythonCode(HashMap<CfgVariable,CfgExpression> substitute) {
		// TODO Auto-generated method stub
		HashMap<CfgVariable,String> result = new HashMap<CfgVariable,String>();
		HashMap<CfgVariable,String> then_block = this.then_block.ToPythonCode((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		HashMap<CfgVariable,String> else_block = this.else_block.ToPythonCode((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		
		for(CfgVariable v : this.variables)
		{
			result.put(v, "("+then_block.get(v)+" if "+ Util.ExpressionToPythonString(this.condition.substitute((HashMap<CfgVariable,CfgExpression>)substitute.clone()))+" else "+else_block.get(v)+" )");
		}
		if(this.next!=null)
		{
			
		}
		return result;
	}

	@Override
	public String GetPythonLoopCondition(HashMap<CfgVariable, CfgExpression> substitute) {
		// TODO Auto-generated method stub
		String then_block = this.then_block.GetPythonLoopCondition((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		String else_block = this.else_block.GetPythonLoopCondition((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		return "("+ then_block +" if "+ Util.ExpressionToPythonString(this.condition.substitute((HashMap<CfgVariable,CfgExpression>)substitute.clone()))
				+ " else "+ else_block + " )";
		
		
	}

	@Override
	public String GetZ3LoopCondition(HashMap<CfgVariable, CfgExpression> substitute) {
		// TODO Auto-generated method stub
		String then_block = this.then_block.GetZ3LoopCondition((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		String else_block = this.else_block.GetZ3LoopCondition((HashMap<CfgVariable,CfgExpression>)substitute.clone());
		return "If("+ Util.ExpressionToZ3String(this.condition.substitute((HashMap<CfgVariable,CfgExpression>)substitute.clone()))+", " +then_block 
				+ ", "+ else_block + " )";
	}
	
	
	@Override
	public void getTemplateArray(ArrayList<CfgExpression> arithList) {
		this.then_block.getTemplateArray(arithList);
		this.else_block.getTemplateArray(arithList);
		Util.extractNonLogicExpressions(this.condition, arithList);
	}


}
