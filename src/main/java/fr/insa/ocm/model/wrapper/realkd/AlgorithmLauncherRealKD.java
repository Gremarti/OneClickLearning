package fr.insa.ocm.model.wrapper.realkd;

import de.unibonn.realkd.algorithms.StoppableMiningAlgorithm;
import de.unibonn.realkd.algorithms.association.AssociationMiningBeamSearch;
import de.unibonn.realkd.algorithms.association.AssociationSampler;
import de.unibonn.realkd.algorithms.emm.ExceptionalModelSampler;
import de.unibonn.realkd.common.workspace.Workspace;
import de.unibonn.realkd.common.workspace.Workspaces;
import de.unibonn.realkd.data.propositions.*;
import de.unibonn.realkd.data.table.DataTable;
import de.unibonn.realkd.data.table.attribute.Attribute;
import fr.insa.ocm.model.wrapper.api.AbstractAlgorithmLauncher;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.*;


public class AlgorithmLauncherRealKD extends AbstractAlgorithmLauncher{

	// The static list of all usable algorithms.
	// This list is instantiated only once in the static method "instantiateListAlgorithm"
	private static List<String> listAvailableMiningAlgorithm = null;

	// The workspace contains the dataset
	private static String lastMiningAlgorithm;
	private Workspace currentWorkspace;

	// Algorithm Launcher keeps the track of the current mining algorithm in order to stop it later if needed.
	private StoppableMiningAlgorithm currentMiningAlgorithm;
	private List<Pattern> patternResults;
	private PatternRealKD.PatternType typeOfPatternResult;

	static{
		// Instantiate the list of available algorithm.
		listAvailableMiningAlgorithm = new ArrayList<>();
		listAvailableMiningAlgorithm.add("AssociationMiningBS");
		listAvailableMiningAlgorithm.add("AssociationSampler");

		listAvailableMiningAlgorithm.forEach(s -> mapNumberCallsPerAlgorithm.put(s, 0));
	}

	/**
	 * Constructor of algorithm launcher, it needs a path to the data file and a path to the attribute file.
	 */
	public AlgorithmLauncherRealKD(String pathRawData){
		super();

		patternResults = new ArrayList<>();
		currentMiningAlgorithm = null;

		// The creation of a Workspace is only done with the factory "Workspaces"
		currentWorkspace = Workspaces.workspace();

		this.importData(pathRawData);
	}

	//********** Implemented methods **********//

	/**
	 * Getter to know the number of data mining algorithm that can be used.
	 * @return The integer representing the number of data mining algorithm that cand be used.
	 */
	@Override
	public int getNbAlgorithms(){
		return listAvailableMiningAlgorithm.size();
	}

	/**
	 * Main method of the algorithm launcher : it starts the algorithm asked and at the end gives the pattern resulting.
	 * The patterns in the list are directly usable by OCM.
	 * @param algoNb The rank of the algorithm to launch.
	 * @return The list of pattern resulting from the launched data mining algorithm.
	 */
	@Override
	public List<Pattern> startAlgorithm(int algoNb){
		//Clear all the precedent found results.
		patternResults.clear();

		if(algoNb < listAvailableMiningAlgorithm.size()){
			//System.err.println("LOG: Getting the algorithm.");

			//Get algorithm from the given number parameter.

			StoppableMiningAlgorithm  miningAlgorithm = getAlgorithm(listAvailableMiningAlgorithm.get(algoNb));

			//If no valid algorithm has been found, it returns an empty result list.
			if(miningAlgorithm == null){
				System.err.println("ERR: getAlgorithm did not sent a valid algorithm.");
				return new ArrayList<>();
			}

			//System.err.println("LOG: Algorithm begins its search.");

			//Launch the data mining algorithm asked and gather the raw results.
			currentMiningAlgorithm = miningAlgorithm;
			Collection<de.unibonn.realkd.patterns.Pattern<?>> results = miningAlgorithm.call();
			currentMiningAlgorithm = null;

			//System.err.println("LOG: Algorithm ends its search.\nConverting results.");

			//Converts the raw result to have results usable for OCM.
			convertResults(results);
			//System.err.println("LOG: Results converted successfully.");

		}else{
			System.err.println(algoNb + "is not a valid number of algorithm.");
		}

		return patternResults;
	}

	/**
	 * Method to request a stop on the current launched algorithm.
	 */
	@Override
	public void stopAlgorithm(){
		if(currentMiningAlgorithm != null){
			currentMiningAlgorithm.requestStop();
			currentMiningAlgorithm = null;
		}
	}

	/**
	 * Imports the data and the attributes from the csv files.
	 * @param csv The path to the csv file.
	 */
	public void importData(String csv){
		CSVLoaderRealKD csvLoaderRealKD = new CSVLoaderRealKD(csv);
		csvLoaderRealKD.convert();
		DataTable dataTable = csvLoaderRealKD.createDataTable();

		//Once the data table is created, it is incorporated in the current workspace.
		currentWorkspace.overwrite(dataTable);

		//Creates all the propositional logic used by RealKD in its algorithms.
		currentWorkspace.overwrite(new PropositionalLogicFromTableBuilder().build(dataTable));

		//Create the list of name of all attributes
		createListAttributeName();
	}

	public List<String> getListAlgorithmName(){
		return new ArrayList<>(listAvailableMiningAlgorithm);
	}

	//********** Package methods **********//

	//********** Public methods **********//

	public static List<String> getListAvailableMiningAlgorithm(){
		return new ArrayList<>(listAvailableMiningAlgorithm);
	}

	//********** Internal methods **********//

	/**
	 * Method to get an algorithm from its name. The available names are listed in listAvailableMiningAlgorithm.
	 * For each name in this list, an algorithm for the library RealKD is given.
	 * @param name The name of the algorithm to get.
	 * @return The stoppable mining algorithm corresponding to the given name.
	 */
	private StoppableMiningAlgorithm getAlgorithm(String name){
		lastMiningAlgorithm = name;
		int numberCalls;

		if(name == null) {
			System.err.println("A Null argument was found instead of a valid parameter in getAlgorithm");
			return null;
		}

		switch (name) {
			case "AssociationMiningBS":
				typeOfPatternResult = PatternRealKD.PatternType.ASSOCIATION;
				numberCalls = mapNumberCallsPerAlgorithm.getOrDefault(name, 0);
				mapNumberCallsPerAlgorithm.put(name, numberCalls + 1);

				return new AssociationMiningBeamSearch(currentWorkspace);

			case "AssociationSampler":
				typeOfPatternResult = PatternRealKD.PatternType.ASSOCIATION;
				numberCalls = mapNumberCallsPerAlgorithm.getOrDefault(name, 0);
				mapNumberCallsPerAlgorithm.put(name, numberCalls + 1);

				return new AssociationSampler(currentWorkspace);

			case "ExceptionalModelSampler":
				typeOfPatternResult = PatternRealKD.PatternType.SUBGROUP;
				numberCalls = mapNumberCallsPerAlgorithm.getOrDefault(name, 0);
				mapNumberCallsPerAlgorithm.put(name, numberCalls + 1);

				return new ExceptionalModelSampler(currentWorkspace);

			default:
				System.err.println(name + " is not a valid algorithm name");
				return null;
		}
	}

	/**
	 * The results sent will be converted in an OCM format. It allows to use the results within the software OCM.
	 * @param results Results should come directly from the RealKD librarie in order to be correctly converted.
	 */
	private void convertResults(Collection<de.unibonn.realkd.patterns.Pattern<?>> results){

		//For each pattern from the library RealKD
		for(de.unibonn.realkd.patterns.Pattern pattern : results){

			if(typeOfPatternResult != null){

				//A type of pattern is required for the pattern to correctly work.
				patternResults.add(new PatternRealKD(pattern, this, typeOfPatternResult, lastMiningAlgorithm));

			}else{

				System.out.println("ERR: The current type of pattern is null");
				break;

			}

		}

		typeOfPatternResult = null;

	}

	/**
	 * Called only once when the algorithm launcher is created.
	 * It creates the list of attributes's name of the dataset for a later use.
	 */
	private void createListAttributeName(){

		if(listAttributeName == null){
			listAttributeName = new ArrayList<>();

			//Checks if the current workspace has no data tables, if there is none then an error is printed.
			if(currentWorkspace.getAllDatatables() == null || currentWorkspace.getAllDatatables().isEmpty()){
				System.err.println("ERR: No Attributes were found. Cannot instantiate listAttributeName in AlgorithmLauncherRealKD");
				return;
			}

			//Adds all the attributes names of the first data table of the current workspace
			for(Attribute<?> attribute : currentWorkspace.getAllDatatables().get(0).attributes()){
				listAttributeName.add(attribute.name());
			}
		}
	}

}
