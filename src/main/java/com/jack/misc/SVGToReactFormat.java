package com.jack.misc;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SVGToReactFormat {
	
	private static Set<String> notUpper = new HashSet<>();
	private static Map<String, String> replacers = new HashMap<>();
	
	static {
		notUpper.addAll( Arrays.asList( "xmlnsSvg", 
				"xmlnsSodipodi",
				"xmlnsInkscape",
				"sodipodiDocname",
				"inkscapeVersion",
				"inkscapeLabel",
				"inkscapeTransform-center-y",
				"inkscapeTransform-center-x",
				"inkscapeRandomized",
				"inkscapeRounded",
				"sodipodiArg2",
				"sodipodiArg1",
				"sodipodiR2",
				"sodipodiR1",
				"sodipodiCy",
				"sodipodiCx",
				"sodipodiSides",
				"inkscapeFlatsided",
				"sodipodiType",
				"inkscapeCurrent-layer",
				"inkscapeWindow-maximized",
				"inkscapeWindow-y",
				"inkscapeWindow-x",
				"inkscapeWindow-height",
				"inkscapeWindow-width",
				"inkscapeCy",
				"inkscapeCx",
				"inkscapeZoom",
				"inkscapeDocument-units",
				"inkscapePagecheckerboard",
				"inkscapePageopacity",
				"inkscapePageshadow"
				)
		);
		
		//build map for replacing
		replacers.put("font-weight:normal", "font-weight:bold");
		replacers.put("fill:#000000", "fill:current");
		replacers.put("stroke:#000000", "stroke:current");
		replacers.put("stroke-width:\\d.\\d{6}", "strokeWidth:current");
	}
	
	public static void convert(String filename) {
		
		//Get a buffered reader, read line at a time
		final String inPath = filename;
		File inFile = new File(inPath);		
		final int buffSize = 1024 * 1000;
		
		try {
					BufferedReader br = new BufferedReader(new FileReader(inFile), buffSize);
					StringBuilder output = new StringBuilder();
					
					//read until we hit EOF
					int a = '\0';
					int bad1 = 34; //double quote char
					int bad2 = 39; //single quote char
					
					while((a = br.read()) != -1) 
					{							
						//if a is a ' " ' - skip until closing
						if(a == bad1 || a==bad2) {
							output.append( (char) a);	//write initial quote
							while((a = br.read()) != bad1 && a != bad2) {
								output.append( (char) a); //write within quotes
							}
							output.append( (char) a); // write final quote
							continue;
						}
						
						if(a == ':') {
							output.append(Character.toUpperCase(br.read()));
						} else {
							output.append( (char) a);
						}
							
					} //END WHILE
					
					//check all namespaces that must be lowercase
					String s = output.toString();
					for(String v : notUpper) {
						s = s.replaceAll(v, v.toLowerCase());
					}
					
					//replace all values with "current" where necessary
					for(Entry<String, String> v : replacers.entrySet()) {
						s = s.replaceAll(v.getKey(), v.getValue());
					}
					
					br.close();
					FileWriter writer = new FileWriter(inFile);
					writer.write(s);
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
