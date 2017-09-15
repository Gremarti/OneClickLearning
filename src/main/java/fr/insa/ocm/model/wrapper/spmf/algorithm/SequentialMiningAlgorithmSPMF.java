package fr.insa.ocm.model.wrapper.spmf.algorithm;

import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class SequentialMiningAlgorithmSPMF implements MiningAlgorithmSPMF {


	// Interval used to split a long sequence
	private Duration intervalItemset;
	private Duration intervalSequence;

	private String fileExtension;

	String pathDataConverted;
	int numberSequence; // Be aware that this value is valid only if SequentialMiningAlgorithmSPMF.loadData is called

	SequentialMiningAlgorithmSPMF(String fileExtension, Duration intervalItemset, Duration intervalSequence){
		this.fileExtension = fileExtension;

		this.intervalItemset = intervalItemset;
		this.intervalSequence = intervalSequence;

		numberSequence = 0;
	}

	public void loadData(String csvPath){
		//Remove the extension of the file name.
		String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));

		File convertedFile = new File(fileName + fileExtension);

		pathDataConverted = convertedFile.getAbsolutePath();

		BufferedWriter writerSPMFDAT = null;
		BufferedReader readerCSV = null;

		try{
			readerCSV = new BufferedReader(new FileReader(new File(csvPath)));
			writerSPMFDAT = new BufferedWriter(new FileWriter(convertedFile));

			String line;
			LocalDateTime thresholdItemset = LocalDateTime.now();
			LocalDateTime thresholdSequence = LocalDateTime.now();
			List<List<Integer>> listAlreadyInItemSet = new ArrayList<>();
			int indexList = 0;

			line = readerCSV.readLine();
			if(line != null){
				String[] lineSplit = line.split(";");

				LocalDateTime dateLine = LocalDateTime.parse(lineSplit[0]);

				thresholdItemset = dateLine.plus(intervalItemset);
				thresholdSequence = dateLine.plus(intervalSequence);

				listAlreadyInItemSet.add(new ArrayList<>());
				listAlreadyInItemSet.get(indexList).add(Integer.valueOf(lineSplit[1]));
			}

			while ((line = readerCSV.readLine()) != null){
				String[] lineSplit = line.split(";");

				LocalDateTime dateLine = LocalDateTime.parse(lineSplit[0]);

				if(dateLine.isAfter(thresholdSequence)){
					// Write the sequence in the resulting file
					this.writeSequence(listAlreadyInItemSet, writerSPMFDAT);

					// Initializing anew the list containing the sequence.
					indexList = 0;
					listAlreadyInItemSet.clear();
					listAlreadyInItemSet.add(new ArrayList<>());

					// Set the new thresholds
					thresholdItemset = dateLine.plus(intervalItemset);
					thresholdSequence = dateLine.plus(intervalSequence);

					// Increase the number of Sequence read.
					numberSequence++;

				}else if(dateLine.isAfter(thresholdItemset)){
					thresholdItemset = dateLine.plus(intervalItemset);

					if(!listAlreadyInItemSet.get(indexList).isEmpty()){
						listAlreadyInItemSet.add(new ArrayList<>());
						indexList++;
					}
				}

				Integer item = Integer.valueOf(lineSplit[1]);

				if(!listAlreadyInItemSet.get(indexList).contains(item)) {
					// An itemset should contain only one occurence of an item.
					listAlreadyInItemSet.get(indexList).add(item);
				}
			}

			// Write the last sequence in the file
			this.writeSequence(listAlreadyInItemSet, writerSPMFDAT);
			numberSequence++;

		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if(writerSPMFDAT != null){
				try {
					writerSPMFDAT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(readerCSV != null){
				try {
					readerCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public abstract List<PatternSPMF> call();

	protected void writeSequence(List<List<Integer>> listItemsets, BufferedWriter fileWriter) throws IOException {
		for(List<Integer> listItemset : listItemsets){
			Collections.sort(listItemset);

			for(Integer item : listItemset){
				fileWriter.write(item + " ");
			}

			fileWriter.write("-1 ");
		}
		fileWriter.write("-2");
		fileWriter.newLine();
	}
}
