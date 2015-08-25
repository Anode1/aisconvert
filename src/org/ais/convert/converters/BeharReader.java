package org.ais.convert.converters;

import java.io.BufferedReader;

import org.ais.convert.Constants;
import org.ais.convert.RawRecord;
import org.ais.convert.Tokenizer;

/**
 * Alleles records rider from the stream. Returns null when no more records 
 */
public class BeharReader {
	
	public static AlleleRecord getNextRecord(BufferedReader reader, int numCol) throws Exception{

		//skip lines with insufficient number of columns
		while(true){
			String line = reader.readLine();
			if(line==null) //end of file
				return null;
			
			if(line.startsWith("!")) //skip comments
				continue;

	    	String[] tokens = Tokenizer.tokenize(line, "\t");
	    	if(tokens.length<numCol){
	    		continue;
	    	}
	    	
			AlleleRecord record = new AlleleRecord();    	
	    	
	    	record.snip = tokens[0];
	    	record.base = tokens[numCol];
			
			return record;
		}
		
	}
}
