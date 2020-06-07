package main;

import java.util.ArrayList;
import java.util.HashMap;


import boogie.controlflow.CfgVariable;
import boogieamp.Boogieamp;
import util.Util;


public class Main {
	
	public static void main(String[] args)
	{
		ArrayList<String> files = Util.getFiles(Util.SOURCE_FILE_PATH);
		String examples="";
		Util.WriteToFile("Loops.py", examples, false);
		examples += "from z3 import *\n";
		examples += "\n\n";
		examples += "L = {\n";
		int count = 0;
		for(int i = 0; i <files.size();i++)
		{
			String f = files.get(i);
			//if(!f.equals("ProgramFile/test.bpl")) continue;
			if(f.matches("([\\s\\S]*)\\.bpl"))
			{
				try
				{
					System.out.println(f);
					Util.PROGRAM_STATE = Util.PROGRAM_STATE_SAFE;
					Boogieamp program = new Boogieamp(f,1);
					if(Util.PROGRAM_STATE == Util.PROGRAM_STATE_ERROR)
					{
						System.out.println("TABOL_ERROR");
						return ;
					}
					if(Util.PROGRAM_STATE == Util.CANNOTINTERPRETED )
					{
						System.out.println(f+"\n Can not interpreter ");
						count ++;
						System.out.println(count);
						continue;
					}
					if(program.variables.size()>=100) continue;
					String code = program.GeneratePythonCode(3);
					//System.out.println(code);
					examples += "#"+f+"\n";
					examples += ""+i+":\n";
					examples += code+",\n";
				}
				catch(Exception e)
				{
					count++;
					System.out.println(f);
					System.out.println(e);
					System.out.println(count);
				}
				
			}
			if(examples.length() >=1024)
			{
				Util.WriteToFile("Loops.py", examples, true);
				examples = "";
			}
		}
		examples+="}\n";
		Util.WriteToFile("Loops.py", examples, true);
		
		
	}

}
