package boogieamp;

import java.util.ArrayList;

public class TemplateArray {
	private int length;
	private ArrayList<Integer> vec;
	
	public TemplateArray(int varNum) {
		this.length = varNum + 1;
		this.vec = new ArrayList<Integer>(this.length);
		for(Integer i : this.vec) {
			i = 0;
		}
	}
	
	public void setVarNum(int varNum, int value) {
		this.vec.set(varNum, value);
	}
	
	public void printVec() {
		for(Integer i : this.vec) {
			System.out.print(i + " ");
		}
		System.out.println();
	}
}
