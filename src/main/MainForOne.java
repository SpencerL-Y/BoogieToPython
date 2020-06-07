package main;

import java.util.ArrayList;

import boogieamp.Boogieamp;
import boogieamp.TemplateArray;
import util.Util;

public class MainForOne {
	public static void main(String[] args)
	{
		String f = args[0];
		int Num = Integer.parseInt(args[1]);
		String[] template = new String[Num];
		for(int i=0;i<Num;i++)
		{
			template[i] = args[2+i];
		}
		String out_file = args[2+Num];
		String info_file = args[3+Num];
		String examples="";
		examples += "from z3 import *\n";
		examples += "\n";
		examples += "L = ";
		try
		{
			// System.out.println(f);
			Util.PROGRAM_STATE = Util.PROGRAM_STATE_SAFE;
			Boogieamp program = new Boogieamp(f,1);
			if(Util.PROGRAM_STATE == Util.PROGRAM_STATE_ERROR)
			{
				System.out.println("TABOL_ERROR");
				Util.WriteToFile(info_file, ""+Util.PROGRAM_STATE_ERROR+" "+Num+"\n", false);
				return ;
			}
			if(Util.PROGRAM_STATE == Util.CANNOTINTERPRETED )
			{
				System.out.println(f+"\n Can not interpreter ");
				Util.WriteToFile(info_file, ""+Util.CANNOTINTERPRETED+" "+Num+"\n", false);
				return ;
			}
			//if(program.variables.size()>=100) return ;
			String code = program.GeneratePythonCode(Num,template);//template will be set in python
			//System.out.println(code);
			//examples += "#"+f+"\n";
			examples += code;
			examples += "T = ";
			ArrayList<TemplateArray> array = program.generateTemplateArray();
			examples += "[\n";
			int i = 0;
			for(TemplateArray a : array) {
				if(i < array.size() - 1) {
					examples += a.toString() + ", \n";
				} else {
					examples += a.toString();
				}
				i++;
			}
			examples += "]\n";
			Util.WriteToFile(info_file, ""+Util.PROGRAM_STATE_SAFE +" "+program.variables.size()+" "+Num+"\n", false);
		}
		catch(Exception e)
		{
			System.out.println(f);
			System.out.println(e);
		}
		
		
		
		
		//examples+="}\n";
		Util.WriteToFile(out_file, examples, false);
		return ;
	}
	public void ParsingBoogieFile(String f,String out_file,String info_file )
	{
		String examples="";
		examples += "from z3 import *\n";
		examples += "\n";
		examples += "L = ";
		try
		{
			// System.out.println(f);
			Util.PROGRAM_STATE = Util.PROGRAM_STATE_SAFE;
			Boogieamp program = new Boogieamp(f,1);
			if(Util.PROGRAM_STATE == Util.PROGRAM_STATE_ERROR)
			{
				System.out.println("TABOL_ERROR");
				Util.WriteToFile(info_file, ""+Util.PROGRAM_STATE_ERROR+" "+0+"\n", false);
				return ;
			}
			if(Util.PROGRAM_STATE == Util.CANNOTINTERPRETED )
			{
				System.out.println(f+"\n Can not interpreter ");
				Util.WriteToFile(info_file, ""+Util.CANNOTINTERPRETED+" "+0+"\n", false);
				return ;
			}
			//if(program.variables.size()>=100) return ;
			String code = program.GeneratePythonCode(0,null);//template will be set in python
			//System.out.println(code);
			//examples += "#"+f+"\n";
			examples += code;
			Util.WriteToFile(info_file, ""+Util.PROGRAM_STATE_SAFE +" "+program.variables.size()+" "+0+"\n", false);
		}
		catch(Exception e)
		{
			System.out.println(f);
			System.out.println(e);
		}
		
		//examples+="}\n";
		Util.WriteToFile(out_file, examples, false);
		return ;
	}
		
}
