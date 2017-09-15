package fr.insa.ocm.model.wrapper.api;


import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.utils.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPattern implements Pattern {

	//Intern variables describing the pattern.

	@Expose protected double[] measures;

	@Expose protected Vector attributeVector = new Vector();
	@Expose protected List<String> listAttributeNames = new ArrayList<>();

	@Expose protected String patternDescriptor = "";
	@Expose protected String algorithmType = "";
	@Expose protected WrapperType wrapperType;

	protected AbstractPattern(){
		measures = new double[MeasureType.values().length];
		for(int i = 0; i < measures.length; ++i){
			measures[i] = 0d;
		}
	}

	protected AbstractPattern(AbstractPattern pattern){
		this.measures = pattern.measures;

		this.attributeVector = new Vector(pattern.attributeVector);
		this.listAttributeNames = new ArrayList<>(pattern.listAttributeNames);

		this.patternDescriptor = pattern.patternDescriptor;
		this.algorithmType = pattern.algorithmType;
		this.wrapperType = pattern.wrapperType;
	}

	@Override
	public double getMeasureValue(MeasureType measure){
		return measures[measure.getIndex()];
	}

	/**
	 * Getter for the attribute vector used to compute the interestingness of this pattern.
	 * @return The vector of presence of attributes. If the attribute of rank i is present in the pattern, the ith value is 1 else it is 0.
	 */
	@Override
	public Vector getAttributesVector(){
		return attributeVector;
	}

	@Override
	public List<String> getListAttributeNames(){ return new ArrayList<>(listAttributeNames); }

	@Override
	public String getAlgorithmName(){
		return algorithmType;
	}

	/**
	 * The Vector contains an array of values from 0d to 1d. First come the measures (between 0d and 1d), then the attributes (0d or 1d), then the algorithm (0d or 1d).
	 * @param algorithmLauncher The algorithm launcher from which the pattern ha been created.
	 */
	protected void computeAttributeVector(AlgorithmLauncher algorithmLauncher){
		List<String> listAllAttributeNames = algorithmLauncher.getListAttributeName();
		List<String> listAllAlgorithmNames = algorithmLauncher.getListAlgorithmName();
		int nbMeasure = measures.length;
		int nbAttr = listAllAttributeNames.size();
		int nbAlg = listAllAlgorithmNames.size();

		double attributeValue[] = new double[nbMeasure + nbAttr + nbAlg];
		for(int i = 0; i < attributeValue.length; ++i){
			attributeValue[i] = 0d;
		}

		int offset = 0;
		System.arraycopy(measures, 0, attributeValue, 0, nbMeasure);

		offset += nbMeasure;
		for(int i = offset; i < offset+nbAttr; ++i){
			if(listAttributeNames.contains(listAllAttributeNames.get(i-offset))){
				attributeValue[i] = 1d;
			}
		}

		offset += nbAttr;
		for(int i = offset; i < offset+nbAlg; ++i){
			if(listAllAlgorithmNames.get(i-offset).equals(algorithmType)){
				attributeValue[i] = 1d;
 			}
		}

		attributeVector = new Vector(attributeValue);
	}

	/**
	 * Compute the list of attribute name of the current pattern.
	 * The list of those name is lazy computed.
	 */
	protected void computeAttributeName(AlgorithmLauncher algorithmLauncher){
		List<String> listAlgoLaunAN = algorithmLauncher.getListAttributeName();

		//For each attribute known in the initial data set, we search it in the pattern.
		//If and only if the attribute is present in the patten, it is added to the listAttributeNames of the current pattern.
		for (String attributeStr : listAlgoLaunAN) {
			if (patternDescriptor.contains(attributeStr)) {
				listAttributeNames.add(attributeStr);
			}
		}
	}

	//********** Standard Methods **********//

	@Override
	public boolean equals(Object obj){
		boolean patternBool = false;

		if(obj instanceof AbstractPattern){
			AbstractPattern patternTmp = (AbstractPattern) obj;

			patternBool = attributeVector.equals(patternTmp.attributeVector);
		}

		return patternBool;
	}
}
