package fr.insa.ocm.model.wrapper.api;

import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.wrapper.realkd.AlgorithmLauncherRealKD;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Pattern{

	enum WrapperType {
		REALKD, SPMF;

		@Override
		public String toString() {
			switch (this){
				case SPMF:
					return "SPMF";
				case REALKD:
					return "RealKD";
				default:
					return "";
			}
		}

		public AlgorithmLauncher getAlgorithmLauncher(String pathRawData){
			switch (this){
				case SPMF:
					return new AlgorithmLauncherSPMF(pathRawData);
				case REALKD:
					return new AlgorithmLauncherRealKD(pathRawData);
				default:
					return null;
			}
		}
	}

	enum MeasureType {
		FREQUENCY,
		RELATIVE_SHORTNESS,
//		LIFT,
//		SUBGROUP_INTERSTINGNESS,
//		TARGET_DEVIATION,
		RELATIVE_PERIODICITY;

		@Override
		public String toString() {
			switch (this) {
				case FREQUENCY:
					return "Frequency";
				case RELATIVE_SHORTNESS:
					return "Relative Shortness";
//				case LIFT:
//					return "Lift";
//				case SUBGROUP_INTERSTINGNESS:
//					return "SubGroup Interestingness";
//				case TARGET_DEVIATION:
//					return "Target Deviation";
				case RELATIVE_PERIODICITY:
					return "Relative Periodicity";
				default:
					return "";
			}
		}

		public int getIndex() {
			MeasureType[] values = values();
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(this)) {
					return i;
				}
			}
			return -1;
		}

		public static MeasureType getName(int index) {
			return values()[index];
		}
	}

	double getMeasureValue(MeasureType measure);

	Vector getAttributesVector();

	String getAlgorithmName();

	String toString();

	List<String> getListAttributeNames();

	Pattern copy();
}
