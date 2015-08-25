package org.ais.convert;

import java.io.BufferedReader;

/**
 * Deserializer of RawRecords from a stream. Returns null when no more records 
 */
public class RawReader{
	
	private boolean initialized;
	private int format;
	private BufferedReader reader; //underlying stream
	
	
	public RawReader(BufferedReader bufferedReader){
		this.reader=bufferedReader;
	}
	
	
	public RawRecord getNextRecord() throws Exception{

		//skip lines with insufficient number of columns
		while(true){
			String line = reader.readLine();
			if(line==null) //end of file
				return null;
			
			if(!initialized){
				//determine the format (whether it is 23andme or FTDNA)
				//make the best guess about the format and store it in this reader for the following records
				//it is like 'magic number' for files
				
				//for now - if the first line is FTDNA header - use FTDNA, otherwise - 23andme 
				if(line.startsWith("RSID,CHROMOSOME,POSITION,RESULT")){
					format=Format.FTDNA_build27;
					line = reader.readLine(); //read next line, skipping the header 
				}
				else{
					format=Format._23andme_build27;
				}
				
				initialized=true;
			}
			
			if(line.startsWith("#")) //skip comments
				continue;

			//parse the line:
			if(format==Format._23andme_build27){
		    	String[] tokens = Tokenizer.tokenize(line, " \t");
		    	if(tokens.length<Constants.MINIMUM_NUMBER_COLUMNS_IN_RAW){
		    		continue;
		    	}
				
				RawRecord record = new RawRecord();    	
		    	
		    	record.snip = tokens[0];
		    	record.chromosome = tokens[1];
		    	record.position = tokens[2];
		    	record.base = tokens[3];
				
				return record;
			}
			else if(format==Format.FTDNA_build27){
		    	String[] tokens = Tokenizer.tokenize(line, ",");
		    	if(tokens.length<Constants.MINIMUM_NUMBER_COLUMNS_IN_RAW){
		    		continue;
		    	}
				
				RawRecord record = new RawRecord();    	
		    	
				//it is assumed that the tokens are wrapped by quotes "\"", so we remove them (1st and last characters implicitly - without checks)
		    	record.snip = tokens[0].substring(1, tokens[0].length()-1);
		    	record.chromosome = tokens[1].substring(1, tokens[1].length()-1);
		    	record.position = tokens[2].substring(1, tokens[2].length()-1);
		    	record.base = tokens[3].substring(1, tokens[3].length()-1);
				
				return record;
			}
			

		}
		
	}
	
	
	public int getFormat(){
		return format;
	}
	
	
	public void close() throws Exception{
		reader.close();
	}
	
}
