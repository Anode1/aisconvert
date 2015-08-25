/*
 	Copyright (C) 2009 Vasili Gavrilov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.ais.convert.merge;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.ais.convert.*;
import org.ais.convert.similarity.SimilarityContext;
import org.ais.convert.tests.TestUtils;

/**
 * Merges 2 raw files, ignoring snips which have different allele values.
 * Files must be in 23andme raw file format (i.e. records been delimited by
 * either tab or space but not wrapped into quotes) and assumed to be sorted
 * by position.
 * In future - it is easy to support on-the-fly conversion from comma-delimited
 * and quoted ftdna format.  
 */
public class Merge extends TwoFilesProcessor{
	

	private String outputFilename;
	
	private static ArrayList chromoList = new ArrayList();
	
	//private static final String HEADER = "# Results of merge ";
	
	
	/* 
	 * Notice: the chromosome order is predefined. 
	 * If in future the order will change in any of
	 * the files - we need to determine - what order to choose - for the output
	 * file (variants: read from 1st file, read from a master-file (sample).
	 * The order in input files does not matter    
	 */
	static{
		//chromoList.add("7");

		for(int i=1; i<23; i++){
			chromoList.add(Integer.toString(i));
		}
		chromoList.add("X");
		chromoList.add("Y");
		chromoList.add("MT");
	}
	
	
	public void init(Result result) throws Exception{
		
		super.init(result);
		
		String arg = Parameters.getString(Constants.OUTPUT_FILE_KEY);
		if(arg!=null){
			outputFilename=arg;
		}
	}
	
	
	protected void processFilesPair(File file1, File file2) throws Exception{
		
		if(Main.trace)System.out.println("Processing " + file1.getName() + " vs " + file2.getName());
		
		if(outputFilename==null){
			String newFileName=FileUtils.getWithoutExtension(file1.getName()) + "_Result.txt";
			String parentDir = file1.getParent();
			if(parentDir==null)
				outputFilename=newFileName;
			else
				outputFilename=parentDir+File.separator+newFileName;
			
			if(Main.warning)System.out.println("WARN: output filename havn't passed (option -o), default is used: "+outputFilename);
		}
		
		//System.out.println("1:\n"+TestUtils.file2String(file1.getAbsolutePath()));
		//System.out.println("2:\n"+TestUtils.file2String(file2.getAbsolutePath()));
		
		RawWriter out = null;
		try{
			out = new RawWriter(new PrintWriter(new BufferedWriter(new FileWriter(outputFilename, false))));
		
			for(int i=0; i<chromoList.size(); i++){
				
				RawReader reader1=null;
				RawReader reader2=null;
				try{
					reader1 = new RawReader(new BufferedReader(new FileReader(file1)));					
					reader2 = new RawReader(new BufferedReader(new FileReader(file2)));
	
					ChromoContext chromoContext = new ChromoContext();
					chromoContext.chromo = (String)chromoList.get(i);
					chromoContext.file1=file1.getName();
					chromoContext.file2=file2.getName();
					if(Main.trace)System.out.println("processing chromosome: " + chromoContext.chromo);
					
					//Context hirsContext = new Context();
					
					processChromo(chromoContext, reader1, reader2, out);
				}
				finally{
					if(reader1!=null)try{reader1.close();}catch(Exception e){}
					if(reader2!=null)try{reader2.close();}catch(Exception e){}
				}
			}//for chromoList
		}
		finally{
			if(out!=null)try{out.close();}catch(Exception ex){}
		}
	}
	

	private void processChromo(ChromoContext chromoContext, 
			RawReader reader1, RawReader reader2, RawWriter out) throws Exception{
		
		RawRecord record1;
		RawRecord record2;
		//int lineCounter1=0;
		//int lineCounter2=0;

		
		//The algorithm here is very close to merge-sort, but first - advance
		//the tape to the necessary chromosome :)
		
		//advance in file1 till necessary chromosome
		while((record1=reader1.getNextRecord())!=null){
			if(record1.chromosome.equals(chromoContext.chromo))
				break;
		}
		//advance in file2 till necessary chromosome			
		while((record2=reader2.getNextRecord())!=null){
			if(record2.chromosome.equals(chromoContext.chromo))
				break;
		}			
		
		
		while(record1!=null || record2!=null){
			
			if(record1!=null){
				if(record2!=null){
					int locPos = compare(record1, record2); 
					if(locPos==1){ //record1<record2
						out.write(record1);
						record1=reader1.getNextRecord(); //advance 1st
					}
					else if(locPos==0){ //the same location and same snip
						if(record1.base.equals(record2.base)){
							out.write(record1);
						}
						else{
							if(Main.warning)System.out.println("WARN: Bases for snip "+record1.snip+" are different:"+record1.base+" vs "+record2.base+" - the snip is skipped");
							out.writeCommented(record1, record2);
						}
						record1=reader1.getNextRecord(); //advance 1st
						record2=reader2.getNextRecord(); //advance 2nd
					}
					else{ //-1, i.e. record2 is before
						out.write(record2);
						record2=reader2.getNextRecord(); //advance 2nd						
					}
				}
				else{ //record2==null
					out.write(record1);
					record1=reader1.getNextRecord(); //advance 1st
				}
			}
			else{ //record1==null but record2!=null (according to while condition) 
				out.write(record2);
				record2=reader2.getNextRecord(); //advance 2nd	
			}
			
			//if next record is on a different chromo - assign null (skip the rest)
			
			if(record1!=null && !record1.chromosome.equals(chromoContext.chromo)){ //skip other chromos
				record1=null;
			}
			
			if(record2!=null && !record2.chromosome.equals(chromoContext.chromo)){ //skip other chromos
				record2=null;
			}			
			
		}//while(record1!=null || record2!=null)
		
	}
	
	
	private int compare(RawRecord record1, RawRecord record2){
		
		if(record1==null)
			return -1;
		if(record2==null)
			return 1;
		
		return record1.compareLocation(record2); //tests also - whether records have the same snip
	}

	
}
