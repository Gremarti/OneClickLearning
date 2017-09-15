package fr.insa.ocm.model.wrapper.spmf.algorithm;


import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ItemsetMiningAlgorithm implements MiningAlgorithmSPMF {

	// Interval used to split a long sequence
	private Duration intervalItemset;

	private String fileExtension;

	String pathDataConverted;
	int numberItemset; // Be aware that this value is valid only if SequentialMiningAlgorithmSPMF.loadData is called

	ItemsetMiningAlgorithm(String fileExtension, Duration intervalItemset){

		this.fileExtension = fileExtension;
		this.intervalItemset = intervalItemset;

		numberItemset = 0;
	}

	@Override
	public void loadData(String csvPath) {
		//Remove the extension of the file name.
		String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));

		File convertedFile = new File(fileName + fileExtension);

		pathDataConverted = convertedFile.getAbsolutePath();

		BufferedWriter writerSPMFDAT = null;
		BufferedReader readerCSV = null;

		try {
			writerSPMFDAT = new BufferedWriter(new FileWriter(convertedFile));
			readerCSV = new BufferedReader(new FileReader(csvPath));

			String line;
			LocalDateTime thresholdDateTime = LocalDateTime.now();
			List<Integer> listAlreadyInItemSet = new ArrayList<>();

			line = readerCSV.readLine();
			if (line != null) {
				String[] lineSplit = line.split(";");

				LocalDateTime dateLine = LocalDateTime.parse(lineSplit[0]);
				thresholdDateTime = dateLine.plus(intervalItemset);

				listAlreadyInItemSet.add(Integer.valueOf(lineSplit[1]));
			}

			while ((line = readerCSV.readLine()) != null) {
				String[] lineSplit = line.split(";");

				LocalDateTime dateLine = LocalDateTime.parse(lineSplit[0]);

				// Each itemset should end with a newline. Here we are taking an itemset with all the items of the same day.
				// It assumes that the file has sorted records from the oldest to the newest.
				if (dateLine.isAfter(thresholdDateTime)) {
					// Set the new threshold as the next day if a day as already passed.
					thresholdDateTime = dateLine.plus(intervalItemset);

					// Write the itemset all at once.
					Collections.sort(listAlreadyInItemSet);
					for (Integer idInt : listAlreadyInItemSet) {
						writerSPMFDAT.write(idInt + " ");
					}

					writerSPMFDAT.newLine();

					listAlreadyInItemSet.clear();
					numberItemset++;
				}

				Integer item = Integer.valueOf(lineSplit[1]);

				if (!listAlreadyInItemSet.contains(item)) {
					// An itemset should contain only one occurence of an item.
					listAlreadyInItemSet.add(item);
				}
			}

			// Write the last itemset.
			Collections.sort(listAlreadyInItemSet);
			for (Integer idInt : listAlreadyInItemSet) {
				writerSPMFDAT.write(idInt + " ");
			}
			writerSPMFDAT.newLine();
			numberItemset++;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writerSPMFDAT != null) {
				try {
					writerSPMFDAT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (readerCSV != null) {
				try {
					readerCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
