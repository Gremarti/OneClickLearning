package fr.insa.ocm.model.utils.serialize;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.bandit.CesaBianciBandit;
import fr.insa.ocm.model.oneclicklearning.bandit.MultiArmedBandit;
import fr.insa.ocm.model.oneclicklearning.cache.api.Cache;
import fr.insa.ocm.model.oneclicklearning.cache.rank.CacheRanking;
import fr.insa.ocm.model.oneclicklearning.cache.set.CacheSet;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.CoactiveLearning;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.ranking.CoactiveLearningRanking;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.set.CoactiveLearningSet;
import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.wrapper.api.AbstractPattern;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.realkd.PatternRealKD;
import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OCMDeserializer {

	private class PatternDeserializer implements JsonDeserializer<Pattern>{

		@Override
		public Pattern deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonPattern = jsonElement.getAsJsonObject();

			AbstractPattern.WrapperType wrapperType;
			Vector attributeVector;
			String patternDescriptor;
			String algorithmType;
			double[] measures;
			List<String> listAttributeNames = new ArrayList<>();

			wrapperType = jsonDeserializer.fromJson(jsonPattern.get("wrapperType"), Pattern.WrapperType.class);
			attributeVector = jsonDeserializer.fromJson(jsonPattern.get("attributeVector"), Vector.class);
			patternDescriptor = jsonElement.getAsJsonObject().get("patternDescriptor").getAsString();
			algorithmType = jsonElement.getAsJsonObject().get("algorithmType").getAsString();
			JsonArray jsonMeasures = jsonPattern.getAsJsonArray("measures");
			JsonArray jsonListAttributeNames = jsonElement.getAsJsonObject().get("listAttributeNames").getAsJsonArray();

			measures = new double[jsonMeasures.size()];
			for(int i = 0; i < jsonMeasures.size(); ++i){
				measures[i] = jsonMeasures.get(i).getAsDouble();
			}
			for(int i = 0; i < jsonListAttributeNames.size(); ++i){
				listAttributeNames.add(jsonListAttributeNames.get(i).getAsString());
			}

			// Special parts for each kind of pattern
			switch (wrapperType){
				case REALKD:
					String typePattern = jsonElement.getAsJsonObject().get("type").getAsString();

					return new PatternRealKD(typePattern, listAttributeNames,
							attributeVector, measures,
							patternDescriptor, algorithmType);
				case SPMF:
					return new PatternSPMF(listAttributeNames, attributeVector,
							measures,
							patternDescriptor, algorithmType);
				default:
					DebugLogger.printDebug("OCMDeserialiazer: Impossible to retrieve the type of Pattern saved.", DebugLogger.MessageSeverity.HIGH);
					throw new RuntimeException("Impossible to determine the type of pattern to deserialize.");
			}


		}
	}

	private class BanditDeserializer implements JsonDeserializer<MultiArmedBandit>{

		@Override
		public MultiArmedBandit deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonArray jsonWeights = jsonElement.getAsJsonObject().getAsJsonArray("weights");

			double[] weights = new double[jsonWeights.size()];
			int round = jsonElement.getAsJsonObject().get("round").getAsInt();
			double V = jsonElement.getAsJsonObject().get("V").getAsDouble();

			for(int i = 0; i < jsonWeights.size(); ++i){
				weights[i] = jsonWeights.get(i).getAsDouble();
			}

			return new CesaBianciBandit(weights, round, V);
		}
	}

	private class CacheDeserializer implements JsonDeserializer<Cache>{

		@Override
		public Cache deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonCache = jsonElement.getAsJsonObject();

			Cache.CacheType cacheType = jsonDeserializer.fromJson(jsonCache.get("cacheType"), Cache.CacheType.class);
			Integer sizeListBestPatterns = jsonCache.get("sizeListBestPatterns").getAsInt();

			switch (cacheType){
				case RANKING:
					Double utility = jsonCache.get("utility").getAsDouble();
					Rank<Pattern> greedyRanking = new Rank<>();

					JsonArray jsonGreedyRanking = jsonCache.get("greedyRanking").getAsJsonArray();
					for(int i = 0; i < jsonGreedyRanking.size(); ++i){
						greedyRanking.add(jsonDeserializer.fromJson(jsonGreedyRanking.get(0), Pattern.class));
					}

					return new CacheRanking(sizeListBestPatterns, cacheType,
											utility, greedyRanking);
				case SET:
					return new CacheSet(sizeListBestPatterns, cacheType);
				default:
					DebugLogger.printDebug("OCMDeserializer: Impossible to retrieve the type of Cache saved.", DebugLogger.MessageSeverity.HIGH);
					throw new RuntimeException("Impossible to determine the type of cache to deserialize.");
			}
		}
	}

	private class CoactiveLearningDeserializer implements JsonDeserializer<CoactiveLearning>{

		@Override
		public CoactiveLearning deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonCoactive = jsonElement.getAsJsonObject();

			// Deserialize common attributs of the CoactiveLearning
			CoactiveLearning.CoactiveType coactiveType =
					jsonDeserializer.fromJson(jsonCoactive.get("coactiveType"), CoactiveLearning.CoactiveType.class);
			Vector weights = jsonDeserializer.fromJson(jsonCoactive.get("weights"), Vector.class);
			SystemState previousSystemState =
					jsonDeserializer.fromJson(jsonCoactive.get("previousSystemState"), SystemState.class);
			int nbCycles = jsonCoactive.get("nbCycles").getAsInt();
			int nbInterestingnessMeasures = jsonCoactive.get("nbInterestingnessMeasures").getAsInt();

			switch (coactiveType){
				case RANKING:
					int d = jsonCoactive.get("d").getAsInt();
					return new CoactiveLearningRanking(weights,
														previousSystemState,
														nbCycles,
														nbInterestingnessMeasures,
														coactiveType,
														d);
				case SET:
					return new CoactiveLearningSet(weights,
													previousSystemState,
													nbCycles,
													nbInterestingnessMeasures,
													coactiveType);
				default:
					DebugLogger.printDebug("OCMDeserializer: Impossible to retrieve the type of CoactiveLearning saved.", DebugLogger.MessageSeverity.HIGH);
					throw new RuntimeException("Impossible to determine the type of coactive learning to deserialize.");
			}
		}
	}

	private static Gson jsonDeserializer;
	private SearchSave searchSave;

	private String deserializedForm;
	private String pathLoadFile;

	public OCMDeserializer(@NotNull String pathLoadFile){
		this.pathLoadFile = pathLoadFile;
		this.deserializedForm = "";

		jsonDeserializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.setPrettyPrinting()
				.registerTypeAdapter(MultiArmedBandit.class, new BanditDeserializer())
				.registerTypeAdapter(Pattern.class, new PatternDeserializer())
				.registerTypeAdapter(Cache.class, new CacheDeserializer())
				.registerTypeAdapter(CoactiveLearning.class, new CoactiveLearningDeserializer())
				.create();
		searchSave = new SearchSave();

		this.deserialize();
	}

	private void deserialize(){
		DebugLogger.printDebug("OCMDeserializer: Loading.");
		this.loadFile();

		searchSave = jsonDeserializer.fromJson(deserializedForm, SearchSave.class);

		DebugLogger.printDebug("OCMDeserializer: Deserializing.");
		OCMManager.deserialize(searchSave);
	}

	private void loadFile(){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(pathLoadFile)));

			String line;
			StringBuilder stringBuilder = new StringBuilder();

			while ((line = reader.readLine()) != null){
				stringBuilder = stringBuilder.append(line);
			}

			deserializedForm = stringBuilder.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
