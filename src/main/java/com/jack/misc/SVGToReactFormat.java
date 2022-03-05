package com.jack.misc;

import java.io.*;

public class SVGToReactFormat {
	
	public static void convert(String filename) {
		
		//Get a buffered reader, read line at a time
		final String inPath = filename;
		File inFile = new File(inPath);		
		final int buffSize = 1024 * 1000;
		
		try {
					BufferedReader br = new BufferedReader(new FileReader(inFile), buffSize);
					FileWriter writer = new FileWriter("output.txt");
					
					//read until we hit EOF
					int a = '\0';
					int bad1 = 34; //double quote char
					int bad2 = 39; //single quote char
					
					while((a = br.read()) != -1) 
					{							
						//if a is a ' " ' - skip until closing
						if(a == bad1 || a==bad2) {
							writer.write(a);	//write initial quote
							while((a = br.read()) != bad1 && a != bad2) {
								writer.write(a); //write within quotes
							}
							writer.write(a); // write final quote
							continue;
						}
						
						if(a == ':') {
							writer.write(Character.toUpperCase(br.read()));
						} else {
							writer.write(a);
						}
							
					} //END WHILE
					br.close();
					writer.close();
				
		}
		catch (FileNotFoundException e) {
					System.out.println("Error opening File: closing");
					e.printStackTrace();
					System.exit(1);
		} catch (IOException e) {
					System.out.println("Error reading from file: closing");
					e.printStackTrace();
					System.exit(2);
				} 
				
		System.out.println("Finished, exiting cleanly");
		
	}

}
