package org.ais.convert.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.ais.convert.Constants;
import org.ais.convert.Parameters;
import org.ais.convert.Result;

public class Snips {
	
	/**
	 * read snips list:
	 */
	static ArrayList readSnips(Result result) throws Exception{

		BufferedReader snipsReader = null;
		ArrayList tempList = new ArrayList();
		try{
			String snipsPath = Parameters.getString(Constants.SNIPS_MAPPING_KEY);
			if(snipsPath!=null){
				File file= new File(snipsPath);
				if(file.exists() && file.canRead()){
					snipsReader = new BufferedReader(new FileReader(file));
					String line;
				    while((line = snipsReader.readLine()) != null){
				    	tempList.add(line.trim());
				    }
				}
				else{
					result.addError();
					System.out.println("Cannot read snips map: " + snipsPath);
				}
			}
			return tempList;
		}
		finally{
			if(snipsReader!=null)try{snipsReader.close();}catch(Exception e){}	
		}
	}
}
