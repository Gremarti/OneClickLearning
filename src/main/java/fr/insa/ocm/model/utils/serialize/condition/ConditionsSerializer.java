package fr.insa.ocm.model.utils.serialize.condition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ConditionsSerializer {

	public static void saveConditions(@NotNull String pathSaveFile, @NotNull List<Condition> listConditions){
		Gson jsonSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.setPrettyPrinting()
				.create();
		Conditions conditions = new Conditions();
		String serializedForm;

		// Wrapping the list of conditions before serializing it.
		conditions.setConditions(listConditions);
		serializedForm = jsonSerializer.toJson(conditions);

		// Writing the serialized form to the file.
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(pathSaveFile))){
			writer.write(serializedForm);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
