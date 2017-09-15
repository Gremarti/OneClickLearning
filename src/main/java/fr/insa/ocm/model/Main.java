package fr.insa.ocm.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.wrapper.api.Pattern;

import static java.lang.Integer.parseInt;

@Deprecated
public class Main {

	@Deprecated
	public static void main(String[] args) throws FileNotFoundException {
		//Redirecting standard error in a log file.
		File file = new File("log.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
//		System.setErr(ps);

		System.out.println("Welcome - OneClick Mining");

		Scanner scanner = new Scanner(System.in);
		System.out.print("Please enter the path to the CSV file: ");
		String path = scanner.nextLine();

		if(path.equals("")){
			path = "ressource/presidentielle2017.csv";
		}

		OCMManager.initialize(Pattern.WrapperType.REALKD, path);
		System.out.println("Waiting for round 1 ...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		List<Pattern> results = OCMManager.getNewRanking(new Rank<>(), new Rank<>(),new Rank<>());
		int round = 1;
		boolean exit = false;
		while (!exit) {
			System.out.println("==========================");
			System.out.println("Current patterns - Round "+round);
			System.out.println("==========================\n");


			int i = 1;
			for (Pattern pattern : results) {
				System.out.format("%2d. %-60s %-15s %-15s %-15s %-15s\n", i, "Pattern", "Freq.", "Lift", "Rel. short.", "Target dev.");
//				System.out.format("  %-60s %-15f %-15f %-15f %-15f\n", pattern.toString(), pattern.getFrequency(), pattern.getLift(), pattern.getRelativeShortness(), pattern.getTargetDeviation());
				i++;
			}

			boolean hasChosen = false;
			String selected = "", rejected = "";
			int[] selectedNb = {}, rejectedNb = {};
			while (!hasChosen) {
				System.out.print("\nPlease type your selected patterns (ex: 1 3 14): ");
				selected = scanner.nextLine();
				System.out.print("Please type your rejected patterns: ");
				rejected = scanner.nextLine();
				System.out.println(rejected);

				int min = 1, max = results.size();
				selectedNb = parseStringToNumber(selected, min, max);
				rejectedNb = parseStringToNumber(rejected, min, max);

				if (rejectedNb != null && selectedNb != null) {
				    if (!(allDifferent(selectedNb))) {
				        System.out.println("You can't select a pattern twice.");
                    }
                    else if (!(allDifferent(rejectedNb))) {
                        System.out.println("You can't reject a pattern twice.");
                    }
					else if (!(allDifferent(selectedNb, rejectedNb))) {
						System.out.println("You can't select and reject the same pattern.");
					} else {
						hasChosen = true;
					}
				}
			}

			System.out.println("You have selected patterns: " + selected + ".");
			System.out.println("You have rejected patterns: " + rejected + ".");

			System.out.println("Results size: " + results.size());
			Rank<Pattern> selectedPatterns = new Rank<>();
			for (int index : selectedNb) {
				selectedPatterns.add(results.get(index-1));
			}
			Rank<Pattern> rejectedPatterns = new Rank<>();
			for (int index : rejectedNb) {
				rejectedPatterns.add(results.get(index-1));
			}
			int maxSelectedOrRejected = getMax(selectedNb, rejectedNb);
			Rank<Pattern> neutralPatterns = new Rank<>();
			if (maxSelectedOrRejected != -1) {
				for (int val=1; i<maxSelectedOrRejected; ++i) {
					neutralPatterns.add(results.get(i));
				}
			}
			neutralPatterns.removeAll(selectedPatterns);
			neutralPatterns.removeAll(rejectedPatterns);

			System.out.print("Type m for MINING or exit to stop the program)");
			String mining = scanner.nextLine();

			if (mining.equals("exit")) {
				exit = true;
			}

			results = OCMManager.getNewRanking(selectedPatterns, neutralPatterns, rejectedPatterns);
			round++;
		}
	}

	private static int[] parseStringToNumber(String str, int min, int max) {
		if (str.length() == 0) {
			return new int[0];
		}

		int localMin = min, localMax = max;
		String[] s = str.split(" ");
		int[] sNb = new int[s.length];
		for(int j = 0; j<s.length; ++j) {
			sNb[j] = parseInt(s[j]);
			if (sNb[j] > max) {
				localMax = sNb[j];
			}
			if (sNb[j] < min) {
				localMin = sNb[j];
			}
		}
		if (localMax > max || localMin < min) {
			System.out.println("You must enter existing patterns from 1 to "+max);
			return null;
		}
		return sNb;
	}

	private static boolean allDifferent(int[] tab) {
	    for (int i=0; i<tab.length-1; ++i) {
	        for (int j = i+1; j<tab.length; ++j) {
	            if (tab[i] == tab[j]) {
	                return false;
                }
            }
        }
        return true;
    }

	private static boolean allDifferent(int[] tab1, int[] tab2) {
		for(int i : tab1) {
			for(int j : tab2) {
				if (i == j) {
					return false;
				}
			}
		}
		return true;
	}

	public static int getMax(int[] tab1, int[] tab2) {
		int max = -1;
		for(int i : tab1) {
			if (i>max) {
				max = i;
			}
		}
		for(int i : tab2) {
			if(i>max) {
				max = i;
			}
		}
		return max;
	}
}
