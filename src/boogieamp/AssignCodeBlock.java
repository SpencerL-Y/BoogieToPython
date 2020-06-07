package boogieamp; 

import java.util.ArrayList;
import java.util.HashMap;

import util.Util;

import boogie.controlflow.*;
import boogie.controlflow.expression.CfgBinaryExpression;
import boogie.controlflow.expression.CfgBooleanLiteral;
import boogie.controlflow.expression.CfgExpression;
import boogie.controlflow.expression.CfgIdentifierExpression;
import boogie.controlflow.expression.CfgWildcardExpression;
import boogie.enums.BinaryOperator;
import boogie.type.BoogieType;


public class AssignCodeBlock extends CodeBlock {
	public CfgVariable[] left;
	public CfgExpression[] right;
	public CfgExpression condition;
	

	public AssignCodeBlock(String index,String pre_index,ArrayList<CfgVariable> variables,HashMap<CfgVariable,CfgExpression> assign,CfgExpression condition) {
		super(index,pre_index,variables);
		// TODO Auto-generated constructor stub
		this.condition = condition;
		this.left = new CfgVariable[variables.size()];
		this.right = new CfgExpression[variables.size()];
		for(int i=0;i<variables.size();i++)
		{
			CfgVariable v = variables.get(i);
			if(assign.containsKey(v))
			{
				this.left[i] = v;
				this.right[i] = assign.get(v).clone();
			}
			else
			{
				this.left[i] = v;
				this.right[i] = new CfgIdentifierExpression(null, v);
			}
		}
	}
	
	@Override
	public HashMap<CfgVariable,String> ToZ3Code(HashMap<CfgVariable,CfgExpression> substitute) {
		// TODO Auto-generated method stub
		HashMap<CfgVariable,String> result = new HashMap<CfgVariable,String>();
		HashMap<CfgVariable,CfgExpression> current_substitute = new HashMap<CfgVariable,CfgExpression>();
		for(int i=0;i<this.left.length;i++)
		{
			
			current_substitute.put(left[i], this.right[i].substitute(substitute));
		}
		if(this.next==null)
		{
			for(CfgVariable v:this.variables)
			{
				result.put(v, Util.ExpressionToZ3String(current_substitute.get(v)));
						
			}
		}
		else
		{
			HashMap<CfgVariable,String> next = this.next.ToZ3Code(current_substitute);
			for(CfgVariable v: this.variables)
			{
				result.put(v, next.get(v));
			}
		}
 		return result;
	}

	@Override
	public HashMap<CfgVariable, String> ToPythonCode(HashMap<CfgVariable,CfgExpression> substitute) {
		// TODO Auto-generated method stub
		HashMap<CfgVariable,String> result = new HashMap<CfgVariable,String>();
		HashMap<CfgVariable,CfgExpression> current_substitute = new HashMap<CfgVariable,CfgExpression>();
		for(int i=0;i<this.left.length;i++)
		{
			
			current_substitute.put(left[i], this.right[i].substitute(substitute));
		}
		if(this.next==null)
		{
			for(CfgVariable v:this.variables)
			{
				result.put(v, Util.ExpressionToPythonString(current_substitute.get(v)));
						
			}
		}
		else
		{
			HashMap<CfgVariable,String> next = this.next.ToPythonCode(current_substitute);
			for(CfgVariable v: this.variables)
			{
				result.put(v, next.get(v));
			}
		}// we do not care the guard of the assignment code block
 		return result;
	}

	@Override
	public String GetPythonLoopCondition(HashMap<CfgVariable, CfgExpression> substitute) {
		// TODO Auto-generated method stub
		if(this.next==null)
		{
			if(this.condition instanceof CfgWildcardExpression)
			{
				return Util.ExpressionToPythonString(this.condition);
			}
			else
			{
				return Util.ExpressionToPythonString(this.condition.substitute(substitute));
			}
		}
		else
		{
			String con_ = "";
			HashMap<CfgVariable, CfgExpression> current_substitute = new HashMap<CfgVariable, CfgExpression>();
			if(this.condition instanceof CfgWildcardExpression)
			{
				con_ =  Util.ExpressionToPythonString(this.condition);
			}
			else
			{
				con_ =  Util.ExpressionToPythonString(this.condition.substitute(substitute));
			}
			for(int i=0;i<this.left.length;i++)
			{
				
				current_substitute.put(left[i], this.right[i].substitute(substitute));
			}
			String next = this.next.GetPythonLoopCondition(current_substitute);
			return "("+next+" and " + con_+ " )";
		}
 		
	}

	@Override
	public String GetZ3LoopCondition(HashMap<CfgVariable, CfgExpression> substitute) {
		// TODO Auto-generated method stub
		if(this.next==null)
		{
			if(this.condition instanceof CfgWildcardExpression)
			{
				return Util.ExpressionToZ3String(this.condition);
			}
			else
			{
				return Util.ExpressionToZ3String(this.condition.substitute(substitute));
			}
		}
		else
		{
			String con_ = "";
			HashMap<CfgVariable, CfgExpression> current_substitute = new HashMap<CfgVariable, CfgExpression>();
			if(this.condition instanceof CfgWildcardExpression)
			{
				con_ =  Util.ExpressionToZ3String(this.condition);
			}
			else
			{
				con_ =  Util.ExpressionToZ3String(this.condition.substitute(substitute));
			}
			for(int i=0;i<this.left.length;i++)
			{
				
				current_substitute.put(left[i], this.right[i].substitute(substitute));
			}
			String next = this.next.GetZ3LoopCondition(current_substitute);
			return "And("+next+", " + con_+ " )";
		}
		
		
	}

	@Override
	public void getTemplateArray(ArrayList<CfgExpression> templateList) {
		// TODO Auto-generated method stub
		if(this.next == null) {
			Util.extractNonLogicExpressions(this.condition, templateList);
		} else {
			Util.extractNonLogicExpressions(this.condition, templateList);
			this.next.getTemplateArray(templateList);
		}
	}


}
