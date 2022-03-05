package com.jack.misc;

public class DataWriter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TextToJSON j = new TextToJSON();
		//j.readFile();
		
		String file = "C:\\Users\\jack\\source\\repos\\Web_Dev\\02-Personal_Repos\\trackerapp-frontend\\src\\resources\\svg\\arrow.svg";
		SVGToReactFormat.convert(file);
		
	}

}
