package fr.insa.ocm.model.wrapper.spmf;

import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.wrapper.api.AbstractPattern;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class PatternSPMF extends AbstractPattern {

	//TODO Javadoc for serializer only
	public PatternSPMF(List<String> listAttributeNames,
	                   Vector attributeVector, double[] measures,
	                   String patternDescriptor, String algorithmType){

		this.algorithmType = algorithmType;
		this.patternDescriptor = patternDescriptor;

		this.attributeVector = attributeVector;
		this.listAttributeNames = listAttributeNames;

		System.arraycopy(measures, 0, this.measures, 0, this.measures.length);

		wrapperType = WrapperType.SPMF;
	}

	public PatternSPMF(String patternDescriptor,
	                   List<String> listAttributeNames,
	                   AlgorithmLauncherSPMF algorithmLauncher,
	                   double[] interestingMeasures,
	                   String algorithmType){
		this.patternDescriptor = patternDescriptor;
		this.listAttributeNames = listAttributeNames;
		this.algorithmType = algorithmType;

		wrapperType = WrapperType.SPMF;

		initialize(algorithmLauncher, interestingMeasures);
	}

	private PatternSPMF(PatternSPMF patternSPMF){
		super(patternSPMF);
	}

	//********** Implemented Methods **********//

	public Pattern copy(){
		return new PatternSPMF(this);
	}

	//********** Internal Methods **********//

	private void initialize(AlgorithmLauncherSPMF algorithmLauncher,
	                        double[] interestingMeasures){
		// The order of these operation is important.
		// computeAttributeVector uses the result of computeAttributeName

		setInterestingValues(interestingMeasures);
		computeAttributeVector(algorithmLauncher);
		computeAttributeName(algorithmLauncher);
	}

	private void setInterestingValues(double[] interestingMeasures){
		System.arraycopy(interestingMeasures, 0, measures, 0, measures.length);
	}

	//********** Standard Methods **********//

	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder("");
		NumberFormat formatter = new DecimalFormat("#0.00");

		boolean needComma = false;

		stringBuilder.append(patternDescriptor);
		stringBuilder.append("\n[");

		for(MeasureType measureType : MeasureType.values()){
			if(AlgorithmLauncherSPMF.getMeasurePatternDescriptor(measureType.getIndex())) {
				if (needComma) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(measureType.toString());
				stringBuilder.append(": ");
				stringBuilder.append(formatter.format(measures[measureType.getIndex()]));

				needComma = true;
			}
		}

		if(needComma){
			stringBuilder.append(", ");
		}

		stringBuilder.append("Algorithm: ");
		stringBuilder.append(algorithmType);
		stringBuilder.append("]");

		return stringBuilder.toString();
	}

}
