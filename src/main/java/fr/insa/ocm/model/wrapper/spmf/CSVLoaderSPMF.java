package fr.insa.ocm.model.wrapper.spmf;


import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CSVLoaderSPMF {

	// Extension of the files used by the algorithm.
	private static final String DAT_EXT = ".spmfdat";
	private static final String MAP_EXT = ".spmfmap";

	// The path of last files used.
	private String pathRawData;

	// The informations about the current loaded data.
	private Map<String, Integer> mapLabelId;
	private Map<Integer, String> mapIdLabel;
	private String pathConvertedData;


	CSVLoaderSPMF(@NotNull String pathCSV){
		this.pathRawData = pathCSV;
		this.pathConvertedData = "";

		this.mapLabelId = new HashMap<>();
		this.mapIdLabel = new HashMap<>();

		this.convert();
	}

	private void convert(){
		//Remove the extension of the file name.
		String fileName = pathRawData.substring(0, pathRawData.lastIndexOf("."));

		File convertedFile = new File(fileName + DAT_EXT);
		File mapFile = new File(fileName + MAP_EXT);

		pathConvertedData = convertedFile.getAbsolutePath();

		BufferedWriter writerSPMFDAT = null;
		//BufferedWriter writerSPMFMAP = null;
		BufferedReader readerCSV = null;

		try{
			readerCSV = new BufferedReader(new FileReader(pathRawData));
			writerSPMFDAT = new BufferedWriter(new FileWriter(fileName + DAT_EXT));
			//writerSPMFMAP = new BufferedWriter(new FileWriter(fileName + MAP_EXT));

			String line;
			int id = 1;

			while ((line = readerCSV.readLine()) != null){
				String[] lineSplit = line.split(";");
				String data;

				if(!mapLabelId.containsKey(lineSplit[1])){
					mapLabelId.put(lineSplit[1], id);
					mapIdLabel.put(id, lineSplit[1]);

					data = Integer.toString(id);
					id++;
				}else{
					data = mapLabelId.get(lineSplit[1]).toString();
				}

				writerSPMFDAT.write(lineSplit[0] + ";" + data);
				writerSPMFDAT.newLine();
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if(readerCSV != null){
				try {
					readerCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writerSPMFDAT != null){
				try {
					writerSPMFDAT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			/*
			if(writerSPMFMAP != null){
				try {
					writerSPMFMAP.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			*/
		}
	}

	Map<Integer, String> getMapIdLabel(){
		return mapIdLabel;
	}

	List<String> getListItem(){
		return new ArrayList<>(mapLabelId.keySet());
	}

	String getPathConvertedData(){
		return pathConvertedData;
	}
}
