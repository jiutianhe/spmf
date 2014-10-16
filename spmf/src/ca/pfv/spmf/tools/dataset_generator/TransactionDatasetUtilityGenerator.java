package ca.pfv.spmf.tools.dataset_generator;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Convert a transaction database to a transaction database with utility values
 * from the source code.
 * @author Philippe Fournier-Viger, 2010
 */
public class TransactionDatasetUtilityGenerator {
	
	/**
	 * Convert a transaction database to a transaction database with utility values
	 * from the source code.
	 * @param input the input file path (a transaction database in SPMF format)
	 * @param output the output file path
	 * @param maxQuantity the maximum quantity of each item in a transaction
	 * @param externalUtilityFactor the external utility of items generated by Random.nextGaussian() will be multiplied by this value
	 * @throws IOException if an error while reading/writting files.
	 * @throws NumberFormatException 
	 */
	public void convert(String input, String output, int maxQuantity, double externalUtilityFactor) throws NumberFormatException, IOException {
		
		// for stats
		Set<Integer> items = new HashSet<Integer>();
		long avglength =0;
		long tidcount = 0;
		
		Random randomGenerator = new Random(System.currentTimeMillis());
		
		Map<Integer, Integer> externalUtilities = new HashMap<Integer, Integer>();

		BufferedWriter writer = new BufferedWriter(new FileWriter(output)); 
		BufferedReader myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
		// for each line (transaction) until the end of file
		String thisLine;
		while ((thisLine = myInput.readLine()) != null) {
			// if the line is  a comment, is  empty or is a
			// kind of metadata
			if (thisLine.isEmpty() == true ||
					thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
							|| thisLine.charAt(0) == '@') {
				continue;
			}
			
			// split the transaction according to the : separator
			String split[] = thisLine.split(" "); 
			

			tidcount++;
			avglength += split.length;

			for(int i=0; i <split.length; i++){
				// convert item to integer
				Integer item = Integer.parseInt(split[i]);
				
				items.add(item);
				
				if(externalUtilities.containsKey(item) == false) {
					double rand = Math.abs(randomGenerator.nextGaussian() * externalUtilityFactor);
//					System.out.println("rand " + rand);
					int extUtility = (int) (rand) + 1;
					externalUtilities.put(item, extUtility);
//					System.out.println(extUtility);
				}
			}
		}
		myInput.close();
		
		myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
		// for each line (transaction) until the end of file
		while ((thisLine = myInput.readLine()) != null) {
			// if the line is  a comment, is  empty or is a
			// kind of metadata
			if (thisLine.isEmpty() == true ||
					thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
							|| thisLine.charAt(0) == '@') {
				continue;
			}
			
			// split the transaction according to the : separator
			String split[] = thisLine.split(" "); 
			
			List<Integer> quantities = new ArrayList<Integer>();
			int TU = 0;
			
			// split the transaction according to the : separator
			for(int i=0; i <split.length; i++){
				// convert item to integer
				Integer item = Integer.parseInt(split[i]);
				int quantity = randomGenerator.nextInt(maxQuantity) +1;
				quantities.add(quantity);
				int extutility = externalUtilities.get(item);
				TU += extutility * quantity;
			}
			
			for(int i=0; i <split.length; i++){
				// convert item to integer
				Integer item = Integer.parseInt(split[i]);
				writer.write(""+ item);
				if(i != split.length -1) {
					writer.write(" ");
				}
			}
			writer.write(":");
			writer.write(""+ TU);
			writer.write(":");
			for(int i=0; i <split.length; i++){
				// convert item to integer
				Integer item = Integer.parseInt(split[i]);
				Integer q = quantities.get(i);
				int extutility = externalUtilities.get(item);
				writer.write(""+ q * extutility);
				if(i != split.length -1) {
					writer.write(" ");
				}
			}
			writer.newLine();
		}
		writer.close();
		
		
		System.out.println("item count " + items.size());
		System.out.println("transacion count " + tidcount);
		System.out.println("transacion avg length " + (avglength / (double) tidcount));
	}

}

	