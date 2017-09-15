package fr.insa.ocm.model.utils;
import com.google.gson.annotations.Expose;

import java.lang.Math;
import java.util.Arrays;

public class Vector {

    @Expose private double[] values;

    public Vector(){
        values = new double[0];
    }

    public Vector(double[] vals) {
    	values = new double[vals.length];

    	System.arraycopy(vals, 0, values,0, vals.length);
    }

    public Vector(Vector orig){
    	values = new double[orig.values.length];
    	System.arraycopy(orig.values, 0, values, 0, values.length);
    }

    //********* Getters/Setters Method ********//

	public double[] getValues() {
		return values;
	}

	public int size(){
    	return values.length;
	}

	//********* Public Method ********//

    public double norm(int p) {
    	if(p == 0){
		    throw new ArithmeticException("Cannot ask the norm with p=0");
	    }

        double result = 0;
        for (double coords : this.values) {
            result += Math.pow((Math.abs(coords)),p);
        }

        result = Math.pow(result,(1.0/p));

        if(result == Double.NaN || result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY){
        	throw new ArithmeticException("Non valid number: "+ result +"\nVector: "+ this.toString() +"\n");
        }

        return result;
    }

    public double get(int index){
		if(index < 0 || index >= values.length){
			throw new ArrayIndexOutOfBoundsException();
		}
		return values[index];
    }

    public void put(Vector vector) {
	    int oldValuesSize = this.values.length;
        int size = vector.values.length + oldValuesSize;

        //Resize the current values array while keeping its content.
        values = Arrays.copyOf(values, size);

        //Adding the vector given in parameter at the end of the current vector.
        System.arraycopy(vector.values, 0, values, oldValuesSize, size-oldValuesSize);
    }

    public void put(double value) {
		values = Arrays.copyOf(values, values.length+1);
	    values[values.length-1] = value;
    }

    public static double scalarProduct(Vector vector1, Vector vector2) {

    	//Checking if both vectors have the same size
    	if(vector1.values.length != vector2.values.length){
		    throw new ArithmeticException("Scalar Product with different sized vectors");
	    }

        double result = 0;
        int vectorlength = vector1.values.length;

        for(int i = 0; i < vectorlength; i++){
	        result += vector1.values[i] * vector2.values[i];
        }
        
        return result;
    }

    public void minusForEach(double value){
	    for (int i = 0; i < values.length; i++) {
		    values[i] -= value;
	    }
    }

	//********* Standard Method ********//

	@Override
	public String toString(){
    	StringBuilder stringBuilder = new StringBuilder("[");

    	for(int i = 0; i < values.length-1; ++i){
    		if(i != values.length-1){
			    stringBuilder = stringBuilder.append(values[i]);
		    } else {
			    stringBuilder = stringBuilder.append(values[i]).append(", ");
		    }
	    }
		stringBuilder = stringBuilder.append("]");

    	return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object obj){
		boolean vectorBool = false;
		if(obj instanceof Vector){
			Vector inputVector = (Vector) obj;

			if(inputVector.values.length == values.length){
				vectorBool = true;

				for(int i = 0; i < values.length; ++i){
					vectorBool = vectorBool && (inputVector.values[i] == values[i]);
				}
			}
		}
		return vectorBool;
	}
}
