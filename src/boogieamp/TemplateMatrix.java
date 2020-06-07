package boogieamp;

import java.util.ArrayList;

public class TemplateMatrix {
	private ArrayList<TemplateArray> matrix;
	private int dimension;
	
	public TemplateMatrix(int dimension) {
		this.dimension = dimension;
		this.matrix = new ArrayList<TemplateArray>(this.dimension);
		for(int i = 0; i < this.dimension; i++) {
			TemplateArray tempArray = new TemplateArray(i - 1);
			for(int j = 0; j < this.dimension; j ++) {
				tempArray.setVarNum(j, 0);
			}
			this.matrix.add(tempArray);
		}
	}
	
	public void setValue(int rowNum, int posNum, int val) {
		if(rowNum < this.dimension) {
			this.matrix.get(rowNum).setVarNum(posNum, val);
		} else {
			System.out.println("ERROR: TemplateMatrix row index out of range");
		}
	}
	
	public String toString() {
		String result = "";
		result += "[ ";
		for(int i = 0; i < this.dimension; i++) {
			if(i < this.dimension - 1) {
				result += this.matrix.get(i).toString() + ", ";
			} else {
				result += this.matrix.get(i).toString();
			}
		}
		result += "]";
		return result;
	}
	
}
