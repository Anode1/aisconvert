package org.ais.convert.converters;

import java.io.BufferedWriter;

import org.ais.convert.Constants;
import org.ais.convert.RawRecord;

public class PedWriter {
	
	private final static String DELIMITER_BETWEEN_BIALLELES = "  "; //2 spaces
	private final static String DELIMITER_BETWEEN_ALLELES = " "; //1 space
	
	public static void writeDefault(BufferedWriter writer) throws Exception{
		
		writer.write(Constants.DEFAULT_PED_FILLER);
		writer.write(DELIMITER_BETWEEN_ALLELES);
		writer.write(Constants.DEFAULT_PED_FILLER);
		
		writer.write(DELIMITER_BETWEEN_BIALLELES);	
	}
	
	
	public static void writeAllele(RawRecord currentRawRecord, BufferedWriter writer) throws Exception{
		writeAllele(currentRawRecord.base, writer);
	}	
	
	
	public static void writeAllele(String base, BufferedWriter writer) throws Exception{
	
		int len = base.length();
		if(len==0){
			writer.write(" ");
			writer.write(DELIMITER_BETWEEN_ALLELES);
			writer.write(" ");
		}
		else if(len==1){
			writer.write(base.charAt(0));
			writer.write(DELIMITER_BETWEEN_ALLELES);
			writer.write(" ");
			/* taking from another column
			if(currentRawRecord.tokens.length>Constants.MINIMUM_NUMBER_COLUMNS_IN_RAW){
				genotype2=currentRawRecord.tokens[4].charAt(0);
			}
			else 
				genotype2='0';
				*/			
		}
		else{ //2 and more (we will take only 2 characters
			writer.write(base.charAt(0));
			writer.write(DELIMITER_BETWEEN_ALLELES);
			writer.write(base.charAt(1));
		}
		
		writer.write(DELIMITER_BETWEEN_BIALLELES);	
	}
}
