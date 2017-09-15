package fr.insa.ocm.model.utils.serialize.condition;

import com.google.gson.*;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.oneclicklearning.bandit.MultiArmedBandit;
import fr.insa.ocm.model.utils.fastforward.condition.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ConditionsDeserializer {

	private static class ConditionDeserializer implements JsonDeserializer<Condition>{

		@Override
		public Condition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			Condition.ConditionType conditionType = jsonDeserializer.fromJson(jsonElement.getAsJsonObject().get("conditionType"), Condition.ConditionType.class);

			switch (conditionType){
				case CONDITION_ATTRIBUTE:
					return jsonDeserializer.fromJson(jsonElement, ConditionAttribute.class);
				case CONDITION_ALGORITHM:
					return jsonDeserializer.fromJson(jsonElement, ConditionAttribute.class);
				case CONDITION_MEASURE_STATIC:
					return jsonDeserializer.fromJson(jsonElement, ConditionMeasureStatic.class);
				case CONDITION_MEASURE_DYNAMIC:
					return jsonDeserializer.fromJson(jsonElement, ConditionMeasureDynamic.class);
				case CONDITION_MEASURE_BETWEEN:
					return jsonDeserializer.fromJson(jsonElement, ConditionMeasureBetween.class);
				default:
					DebugLogger.printDebug("ConditionsDeserializer: Impossible to retrieve the type of Condition saved.", DebugLogger.MessageSeverity.HIGH);
					throw new RuntimeException("Impossible to determine the type of condition to deserialize.");
			}
		}
	}

	private static Gson jsonDeserializer;

	public static List<Condition> loadConditions(@NotNull String pathLoadFile){
		StringBuilder sbSerializedForm = new StringBuilder("");
		Conditions conditions;
		jsonDeserializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.setPrettyPrinting()
				.registerTypeAdapter(Condition.class, new ConditionDeserializer())
				.create();

		try(BufferedReader reader = new BufferedReader(new FileReader(pathLoadFile))){
			String line;

			while ((line = reader.readLine()) != null){
				sbSerializedForm.append(line);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		conditions = jsonDeserializer.fromJson(sbSerializedForm.toString(), Conditions.class);

		return conditions.getConditions();
	}
}
