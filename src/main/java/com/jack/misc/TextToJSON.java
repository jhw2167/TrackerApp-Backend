package com.jack.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TextToJSON {

	/* VARS */
	private static List<Map<String, String>> vals = new ArrayList<Map<String, String>>();
	
	private static List<String> terms = new ArrayList<>();
	
	static {
		terms.add("tId");
		terms.add("purchaseDate");
		terms.add("vendor");
		terms.add("amount");
		terms.add("category");
		terms.add("payMethod");
		terms.add("boughtFor");
		terms.add("payStatus");
		terms.add("income");
		terms.add("reimburses");
		terms.add("postedDate");
		terms.add("notes");
	}
	
	private static String fileName = "src/main/resources/dataIn.txt";
	private static String outFile = "src/main/resources/dataOut.txt";
	
	public TextToJSON() {
		// TODO Auto-generated constructor stub
	}
	
	public TextToJSON(String folderName) {
		this.fileName=folderName + "\\dataIn.txt";
		this.outFile=folderName + "\\dataOut.txt";
	}
	
	/* METHODS */
	public void readFile() {
		
		//Use buffered reader - each line should be smaller than 1KB
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fileName), 1024);
			 Scanner read = new Scanner(in);
			   
				
			    //read in rest of meaningful data
			    while(read.hasNextLine()) 
			    {
			    	String data = read.nextLine();
			    	String[] valData = data.split("-");
			    	
			    	System.out.println("VAL DATA: " + data + "\n");
			    	
			    	//Each new transaction is a map
			    	Map<String, String> transaction = new HashMap<>();
			    	int i = 0;
			    	for(String term : terms) {
			    		if(term.equals("tId") ) {
			    			transaction.put(term, null);
			    			continue;
			    		}
			    			
			    		if(term.contains("Date") && i < valData.length && !(valData[i].equals(""))) {
			    			valData[i] = convertDate(valData[i].trim());
			    		}
			    		
			    		//Pack terms into vals array
			    		if(i < valData.length) {
			    			//we can put read value in array
			    			//System.out.println("-- " + valData[i].trim());
			    			transaction.put(term, valData[i].trim());
			    		} else {
			    			transaction.put(term, null);
			    		}
			    		
			    		i++;
			    		
			    	}
			    	//END FOR
			    	vals.add(transaction);
			    	
			    	//System.out.println(transaction.toString());
			    }
			    //END WHILE
			
			   read.close();
			    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		//END TRY CATCH
		writeJSON();
	}
	//END READ FILE FUNCTION
	
	private static String convertDate(String inDate) {
		//parse date string into coherent string
				String[] date = inDate.split("/");
				System.out.println("Date: " + date[0]);
				if(date.length < 3) {
					date = new String[] {date[0], date[1], "21"};
				}
				
				date[2] = "20" + date[2];
				return date[2] + "-" + String.format("%02d", Integer.parseInt(date[0])) + "-" + String.format("%02d",Integer.parseInt(date[1]));
	}
	//END STATIC CONVERT DATE
	
	private void writeJSON() {
		
		System.out.println("Size of transactions to write: " + vals.size());
		
		System.out.println("\nWriting to: "+outFile);
		
		File out = new File(outFile);
		try {
			out.createNewFile();
			FileWriter writer = new FileWriter(outFile);
			
			//Open Array
			writer.write("[\n");
			for(Map<String, String> val : vals) {
				System.out.println("val to write: " + val.toString());
				writer.write("{\n");
				for(String term : terms) {
					StringBuilder b = new StringBuilder();
					b.append('"');
					b.append(term);
					b.append('"');
					b.append(": ");
					b.append('"');
					
					if( (val.get(term) != null) && !(term.equals("reimburses")))
						b.append(val.get(term));
					b.append('"');
					
					if(!term.equals("notes"))
						b.append(",\n");
					
					//actually write the value
					writer.write(b.toString());
				}
				writer.write("\n},\n\n");
			}
			//END DOUBLE FOR
			
			writer.write("]");
			writer.close();
		} catch (IOException e) {
			System.out.println("\nError Writing to: "+outFile + "\n\n");

		}
		
		
		
	}
	//END FUNCTION WRITE JSON
	
}
//END CLASS TEXTTOJSON