package com.jack.model.misc;

public class DataWriter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String file = args[1]; 	//or folder in textToJsonCase
		
		int process = Integer.parseInt(args[0]);
		
		switch(process) {
		case 1:
			TextToJSON j = new TextToJSON(file);
			j.readFile();
			break;
		case 2:
			SVGToReactFormat.convert(file);
			break;
		}
		//END SWITCH
		
	}

}
