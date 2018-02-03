package fr.insa.ocm.model.utils.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import fr.insa.ocm.model.OCMManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OCMSerializer {

	private Gson jsonSerializer;
	private SearchSave searchSave;

	private String serializedForm;
	private String pathSaveFile;

	public OCMSerializer(@NotNull String pathSaveFile){
		this.pathSaveFile = pathSaveFile;
		serializedForm = "";

		jsonSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		searchSave = new SearchSave();

		this.serialize();
	}

	private void serialize(){
		OCMManager.serialize(searchSave);

		serializedForm = jsonSerializer.toJson(searchSave);

		this.saveFile();
	}

	private void saveFile(){
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(pathSaveFile)));

			writer.write(serializedForm);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
