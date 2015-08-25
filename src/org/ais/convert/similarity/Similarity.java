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
package org.ais.convert.similarity;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.ais.convert.*;
import org.ais.convert.hir.CMorgans;

/**
 * 2-files processor, calculating HIRs.
 */
public class Similarity extends EachPairProcessor{
	
	private static ArrayList chromoList = new ArrayList();
	static{
		//22 chromos:
		//we will have a deficit of memory (>64M) on old machines - if we'll load 
		//all chromos, so we'll do one-by-one, reading the file mutliple
		//times.
		for(int i=1; i<23; i++){
			chromoList.add(Integer.toString(i));
		}
	}
	

	/**
	 * Called once, before files processing, by the framework (files processing framework,
	 * been developed in this project for different genome processing tasks).
	 * Here we initialize maps, constants, etc.  
	 */
	public void init(Result result) throws Exception{
		
		super.init(result);
		if(result.thereAreErrors())
			return;
		
		//read centimorgans mapping:
		if(!CMorgans.init()){
			result.addError();
			return;
		}
	}
	
	
	/**
	 * Files processing framework call-back on 2 files processing against each other
	 * (in case of HIR-processor - we compare 2 files always) 
	 */
	protected void processFilesPair(File file1, File file2) throws Exception{
		
		if(Main.trace)System.out.println("Processing " + file1.getName() + " vs " + file2.getName());
		
		SimilarityContext context = new SimilarityContext();
		
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
				
				processChromo(context, chromoContext, reader1, reader2);
			}
			finally{
				if(reader1!=null)try{reader1.close();}catch(Exception e){}
				if(reader2!=null)try{reader2.close();}catch(Exception e){}
			}
		}//for chromoList
		

		context.finalReport();
	}
	

    /**
     * Processes one chromosome. Readers - are streams from 2 files, provided by the framework 	
     */
	private void processChromo(SimilarityContext context, ChromoContext chromoContext, 
			RawReader reader1, RawReader reader2) throws Exception{

		//read 1st file into hash:
		
		HashMap firstFile= new HashMap();
		RawRecord record;
		while((record=reader1.getNextRecord())!=null){

			if(!record.chromosome.equals(chromoContext.chromo)){ //skip other chromos
				//System.out.println("skip: "+record.chromosome);
				//System.out.println(record.chromosome +" vs "+chromo+".");
				continue;
			}
			firstFile.put(record.snip, record);
		}
		
		//process second file:
		secondFile(context, chromoContext, firstFile, reader2);
	}
	
	
	private void secondFile(SimilarityContext similarityContext, ChromoContext chromoContext, 
			HashMap firstFile, RawReader reader2) throws Exception{
		
		RawRecord record2;
		int lineCounter=0;
		while((record2=reader2.getNextRecord())!=null){

			if(!record2.chromosome.equals(chromoContext.chromo)){ //skip other chromos
				continue;
			}
			
			RawRecord record1 = (RawRecord)firstFile.get(record2.snip);
			if(record1==null){
				//System.out.println("Not found in first file: "+record2.snip);
				continue;
			}

			//warn - if the same snips in 2 files have different positions:
			if(!record1.position.equals(record2.position)){
				if(Main.warning)System.out.println("WARN: Positions are different: " + record1.position +"," + record2.position + " and the second position will be shown in the report.");
			}
			
			//if we should not count the snips with -- - remove the following comments and put 
			//comments in isFullMismatch method:
			//we assume here (having looked into 23andme data) that no call is always '--', even for male's one-letter base
			//if(record1.base.charAt(0)=='-' || record2.base.charAt(0)=='-'){
			//	continue; //either no call is match
			//}
			
			lineCounter++;
			
			calculateSimilarity(record1.base, record2.base, similarityContext);
			
		}//while getNextRecord
	}

	
	public static void calculateSimilarity(String base1, String base2, SimilarityContext similarityContext){
		
		if(similarityContext.skipXYorMT)
			return;
		
		//we assume here (looked into 23andme data) that no call is always '--', even for male one-letter base
		if(base1.charAt(0)=='-' || base2.charAt(0)=='-'){
			return; //either no call is match
		}				
		
		similarityContext.totalNotNoCalls++;		
		
		if(base1.length()==1){
			if(base2.length()==1){ //both males
				if(base1.charAt(0)!=base2.charAt(0)){
					//full mismatch
				}
				else{
					similarityContext.halfMatch();
				}
			}
			else{ //base2.length==2
				//System.out.println("WARN: there is one-letter on one side and 2 - on another one");
				if(base1.charAt(0)!=base2.charAt(0) && base1.charAt(0)!=base2.charAt(1)){
					//full mismatch
				}
				else{ //one letter is matching
					similarityContext.halfMatch();
				}
			}
		}
		else{//base1.length()==2
			if(base2.length()==1){
				//System.out.println("WARN: there are 2 letters on one side and 1 - on another one");
				if(base1.charAt(0)!=base2.charAt(0) && base2.charAt(0)!=base1.charAt(1)){
					//full mismatch
				}
				else{ //one letter is matching
					similarityContext.halfMatch();
				}
			}
			else{//base2.length()==2
				//we assume that order is the same, so cannot be AD and DA
				if(base1.charAt(0)==base2.charAt(0)){
					if(base1.charAt(1)==base2.charAt(1)){
						similarityContext.fullMatch(base1);
					}
					else{
						similarityContext.halfMatch();
					}
				}
				else{
					if(base1.charAt(1)==base2.charAt(1)){
						similarityContext.halfMatch();
					}
					//else full mismatch
				}
			}//else base2.length()==2
		}//else base1.length()==2
	}
	
	
}
