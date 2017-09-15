package fr.insa.ocm.model.wrapper.realkd;

import com.sun.istack.internal.NotNull;
import de.unibonn.realkd.data.table.DataFormatException;
import de.unibonn.realkd.data.table.DataTable;
import de.unibonn.realkd.data.table.DataTableFromCSVFileBuilder;

import java.io.*;
import java.util.*;

class CSVLoaderRealKD {

	private enum AttributeType{
		NAME,
		NUMERIC,
		CATEGORIC
	}

	private String csvPath;
	private String datPath;
	private String attPath;

	private boolean isConverted;

	CSVLoaderRealKD(@NotNull String csv){
		csvPath = csv;

		//Remove the extension of the file name.
		String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));

		//Create the new name for the the new data file and new attribute file.
		datPath = fileName + ".dat";
		attPath = fileName + ".att";

		isConverted = false;
	}

	void convert(){
		if (csvPath == null || datPath == null || attPath == null){
			System.err.println("ERR: One of the path name is empty");
			return;
		}

		BufferedReader readerCSV = null;
		BufferedWriter writerDAT = null;
		BufferedWriter writerATT = null;

		String[] attributeArr;
		AttributeType[] typeArr;
		try{
			readerCSV = new BufferedReader(new FileReader(new File(csvPath)));
			writerDAT = new BufferedWriter(new FileWriter(new File(datPath)));
			writerATT = new BufferedWriter(new FileWriter(new File(attPath)));

			//Read the first line, which has the attributes.
			String line = readerCSV.readLine();
			if(line == null){
				System.err.println("ERR: Could not read the first line of the CSV file");
				throw new IOException();
			}

			//Create the list of attributes for the attribute file
			attributeArr = line.split(";");

			//Create the list of types of attributes for the attribute file
			//By default, the first attribute is always the name, all the other ones are numeric (unless detected categoric while reader)
			typeArr = new AttributeType[attributeArr.length];
			typeArr[0] = AttributeType.NAME;
			for(int i = 1; i < typeArr.length; ++i){
				typeArr[i] = AttributeType.NUMERIC;
			}

			//Read the other lines which has the data and create the data file
			while((line = readerCSV.readLine()) != null){
				//Write the data inside the data file
				writerDAT.write(line);
				writerDAT.newLine();

				//Check the type of each attribute (without the first one which is always name)
				String[] split = line.split(";");
				if(split.length != typeArr.length){
					System.err.println("ERR: The CSV is malformed, one line has one less or one more field than expected");
					System.err.println(line);
					throw new IOException();
				}

				for(int i = 1; i < split.length; ++i){
					//If the attribute is set as numeric and the data is not empty and is not a number, than the corresponding attribute is categoric
					if(typeArr[i].equals(AttributeType.NUMERIC) && !split[i].equals("") && !split[i].matches("-?\\d+(\\.\\d+)?")) {
						typeArr[i] = AttributeType.CATEGORIC;
					}
				}
			}

			//Create the attribute file
			for(int i = 0; i < attributeArr.length; ++i){
				writerATT.write(attributeArr[i] + ";" + typeArr[i].toString().toLowerCase() + ";" + attributeArr[i] + "\'s description");
				writerATT.newLine();
			}

			isConverted = true;
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			//No matter what happens, close all the file handlers
			if(readerCSV != null){
				try {
					readerCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writerDAT != null){
				try {
					writerDAT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writerATT != null){
				try {
					writerATT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Static method to create a data table from a data csv file. Used internally to create the dataset.
	 * @return The datatable (defined by RealKD) containing the data and its attributes in a usuable form for RealKD.
	 */
	DataTable createDataTable(){
		if(!isConverted){
			System.err.println("ERR: The CSV has not been converted, thus it is impossible the create the corresponding datatable");
			return null;
		}

		DataTable resultDataTable = null;

		//Builder to create a data table. It uses the data file and the attribute file to import the data and the attributes.
		//Then it gives an ID and a name for the soon to be created data table.
		DataTableFromCSVFileBuilder builder =
				new DataTableFromCSVFileBuilder()
						.setDataCSVFilename(datPath)
						.setAttributeMetadataCSVFilename(attPath)
						.setId("OCM")
						.setName("OCM");

		try{
			//The builder tries to create the datable.
			resultDataTable = builder.build();
		}catch(DataFormatException dfException) {
			System.err.println("DataFormatException encountered when loading the CSV file.");
			System.err.println(dfException.getMessage());
			System.exit(-1);
		}

		return resultDataTable;
	}

	public List<List<String>> loadCSV(){
		List<List<String>> stringCSV = new ArrayList<>();

		BufferedReader readerCSV;

		String[] tmp;
		int nbAttr;
		try {
			readerCSV = new BufferedReader(new FileReader(new File(csvPath)));

			//Read the first line, which has the attributes.
			String line = readerCSV.readLine();
			if(line == null){
				System.err.println("ERR: Could not read the first line of the CSV file");
				throw new IOException();
			}

			//Create the list of attributes for the attribute file
			tmp = line.split(";");
			nbAttr = tmp.length;

			stringCSV.add(new ArrayList<>(Arrays.asList(tmp)));

			while ((line = readerCSV.readLine()) != null){
				tmp = line.split(";");

				if(tmp.length != nbAttr){
					System.err.println("ERR: The CSV is malformed, one line has one less or one more field than expected");
					System.err.println(line);
					throw new IOException();
				}

				stringCSV.add(new ArrayList<>(Arrays.asList(tmp)));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringCSV;
	}
}
