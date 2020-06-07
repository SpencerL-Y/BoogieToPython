package boogieamp;

import java.util.ArrayList;

public class TemplateArray {
	private int length;
	private ArrayList<Integer> vec;
	
	public TemplateArray(int varNum) {
		this.length = varNum + 1;
		this.vec = new ArrayList<Integer>();
		for(int i = 0; i < this.length; i++) {
			this.vec.add(0);
		}
	}
	
	public void setVarNum(int pos, int value) {

		
		if(pos < this.length) {
			this.vec.set(pos, value);
		} else {
			System.out.println("ERROR: TemplateArray index out of range");
			return;
		}
	}
	
	public String toString() {
		String result = "";
		result += "[";
		int index = 0;
		for(Integer i : this.vec) {
			if(index < this.length - 1) {
				result += (i + ", ");
			} else {
				result += i;
			}
			index ++;
		}
		result += "]";
		return result;
	}
}
