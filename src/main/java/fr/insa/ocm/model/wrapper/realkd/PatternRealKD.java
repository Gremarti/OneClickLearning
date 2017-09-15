package fr.insa.ocm.model.wrapper.realkd;

import com.google.gson.annotations.Expose;
import de.unibonn.realkd.patterns.QualityMeasureId;
import fr.insa.ocm.model.wrapper.api.AbstractPattern;
import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.wrapper.api.Pattern;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PatternRealKD extends AbstractPattern {

	enum PatternType{
		ASSOCIATION, SUBGROUP, UNKNOWN;

		static PatternType getPatternType(String str){
			switch (str.toLowerCase()){
				case "association":
					return PatternType.ASSOCIATION;
				case "subgroup":
					return PatternType.SUBGROUP;
				default:
					return PatternType.UNKNOWN;
			}
		}
	}

	//The Pattern coming from the RealKD library.
	//private de.unibonn.realkd.patterns.Pattern pattern;

	//The algorithm launcher from which come from this pattern.
	//private AlgorithmLauncherRealKD algorithmLauncher;

	//The type of this pattern.
	@Expose private PatternType type;

	//TODO Javadoc, for serialization only
	public PatternRealKD(){}

	public PatternRealKD(String type, List<String> listAttributeNames,
	                     Vector attributeVector, double[] measures,
	                     String patternDescriptor, String algorithmType){
		this.type = PatternType.getPatternType(type);
		this.listAttributeNames = listAttributeNames;
		this.attributeVector = attributeVector;

		this.measures = measures;

		this.patternDescriptor = patternDescriptor;
		this.algorithmType = algorithmType;

		wrapperType = WrapperType.REALKD;
	}

	/**
	 * Package visible constructor of a PatternRealKD.
	 * @param p The Pattern obtained from the data mining algorithm, the Pattern is the one from the RealKD library.
	 * @param algorithmLauncherRealKD The Algorithm Launcher which had launched the data mining algorithm to obtain the Pattern.
	 * @param patternType The type of Pattern that were given by the data mining algorithm, e.g. Association, SubGroup, etc.
	 */
	PatternRealKD(@NotNull de.unibonn.realkd.patterns.Pattern p,
	              @NotNull AlgorithmLauncherRealKD algorithmLauncherRealKD,
	              @NotNull PatternType patternType,
	              @NotNull String algorithmType){
		type = patternType;
		this.algorithmType = algorithmType;

		listAttributeNames = new ArrayList<>();
		wrapperType = WrapperType.REALKD;

		initialize(p, algorithmLauncherRealKD);
	}

	private PatternRealKD(@NotNull PatternRealKD patternRealKD){
		super(patternRealKD);

		this.type = patternRealKD.type;
	}

	//********** Implemented Methods **********//

	public Pattern copy(){
		return new PatternRealKD(this);
	}

	//********** Internal Methods **********//

	private void initialize(de.unibonn.realkd.patterns.Pattern p, AlgorithmLauncherRealKD algorithmLauncherRealKD){
		patternDescriptor = p.descriptor().toString();

		// The order of these operation is important.
		// computeAttributeVector uses the result of computeAttributeName
		computeAttributeName(algorithmLauncherRealKD);
		computeAttributeVector(algorithmLauncherRealKD);
		computeInterestingValues(p, algorithmLauncherRealKD);
	}

	private void computeInterestingValues(de.unibonn.realkd.patterns.Pattern p, AlgorithmLauncherRealKD algorithmLauncherRealKD){
		if(type == PatternType.ASSOCIATION){
			// Frequency
			if(p.hasMeasure(QualityMeasureId.FREQUENCY)){
				measures[MeasureType.FREQUENCY.getIndex()] = p.value(QualityMeasureId.FREQUENCY);
			}else{
				measures[MeasureType.FREQUENCY.getIndex()] = 0.;
			}

			// RelativeShortness
			double totalNumberAttribute = algorithmLauncherRealKD.getListAttributeName().size();
			if(totalNumberAttribute != 0) {
				measures[MeasureType.RELATIVE_SHORTNESS.getIndex()] = listAttributeNames.size() / totalNumberAttribute;
			}else{
				measures[MeasureType.RELATIVE_SHORTNESS.getIndex()] = 0.;
			}

//			// Lift
//			if(p.hasMeasure(QualityMeasureId.LIFT)){
//				lift = p.value(QualityMeasureId.LIFT);
//			}else{
//				lift = 0.;
//			}
		}
	}

	//********** Standard Methods **********//

	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder("");
		NumberFormat formatter = new DecimalFormat("#0.00");

		stringBuilder = stringBuilder.append(patternDescriptor).append("\n");

		if(type == PatternType.ASSOCIATION){
			stringBuilder = stringBuilder.append("[Frequency: ").append(formatter.format(measures[MeasureType.FREQUENCY.getIndex()]))
					.append(", RelativeShortness: ").append(formatter.format(measures[MeasureType.RELATIVE_SHORTNESS.getIndex()]))
//					.append(", Lift: ").append(formatter.format(getLift()))
					.append(", Algorithm: ").append(algorithmType).append("]\n");
		}

		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object obj){
		boolean patternBool = false;

		if(obj instanceof PatternRealKD){
			PatternRealKD patternTmp = (PatternRealKD) obj;

			patternBool = patternDescriptor.equals(patternTmp.patternDescriptor);
		}

		return patternBool;
	}

}
