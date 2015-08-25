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
package org.ais.convert.hir;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.ais.convert.*;
import org.ais.convert.similarity.Similarity;
import org.ais.convert.similarity.SimilarityContext;

/**
 * 2-files processor, calculating HIRs.
 */
public class UPSometer extends EachPairProcessor{
	
	private int MIN_THREDHOLD = Constants.DEFAULT_SNIPS;
	private float CM_THRESHOLD = Constants.DEFAULT_CM;
	private boolean calculateSimilarity;
	private static ArrayList chromoList = new ArrayList();
	static{
		//22 chromos:
		//we will have a deficit of memory (>64M) on old machines - if we'll load 
		//all chromos, so we'll do one-by-one, reading the file mutliple
		//times.
		for(int i=1; i<23; i++){
			chromoList.add(Integer.toString(i));
		}
		chromoList.add("X");
		//chromoList.add("Y");
		//chromoList.add("MT");
	}
	
	/**
	 * Comparator used for sorting HIRs in descending order. We pick the biggest HIRs first.
	 */
	private Comparator hirsComparator = new Comparator(){
		public int compare(Object o1, Object o2){
				Region u1 = (Region)o1;
				Region u2 = (Region)o2;
				int c1=u1.getCount();
				int c2=u2.getCount();
				return (c1<c2 ? 1 : (c1==c2 ? 0 : -1));
			  }
	    };
	
	/**
	 * Called once, before files processing, by the framework (files processing framework,
	 * been developed in this project for different genome processing tasks).
	 * Here we initialize maps, constants, etc.  
	 */
	public void init(Result result) throws Exception{
		
		super.init(result);
		if(result.thereAreErrors())
			return;
		
		String arg = Parameters.getString(Constants.MIN_THRESHOLD_KEY);
		if(arg!=null){
			try{
				MIN_THREDHOLD = Integer.parseInt(arg);
			}
			catch(NumberFormatException e){
				result.addError();
				System.out.println("Threshold must be a natural number");
				return;
			}
		}
		
		arg = Parameters.getString(Constants.SM_THRESHOLD_KEY);
		if(arg!=null){
			try{
				CM_THRESHOLD = Float.parseFloat(arg);
			}
			catch(NumberFormatException e){
				result.addError();
				System.out.println("centiMorgans threshold value must be a number");
				return;
			}
		}
		
		calculateSimilarity = Parameters.getAsBoolean(Constants.SHOW_SIMILARITY);
		
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
		
		SimilarityContext similarityContext = new SimilarityContext();
		
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
				
				HirsContext hirsContext = new HirsContext();
				/*
				if(chromoContext.chromo.equals("X")){
					similarityContext.skipXYorMT = hirsContext.skipCentimorganThreshold = true;
				}
				else{
					similarityContext.skipXYorMT = hirsContext.skipCentimorganThreshold = false;
				}
				//System.out.println(context.skipCentimorganThreshold);
				*/
				processChromo(similarityContext, chromoContext, hirsContext, reader1, reader2);
			}
			finally{
				if(reader1!=null)try{reader1.close();}catch(Exception e){}
				if(reader2!=null)try{reader2.close();}catch(Exception e){}
			}
		}//for chromoList
		
		if(calculateSimilarity){
			similarityContext.finalReport();
		}
	}
	

    /**
     * Processes one chromosome. Readers - are streams from 2 files, provided by the framework 	
     */
	private void processChromo(SimilarityContext similarityContext, ChromoContext chromoContext, 
			HirsContext hirsContext, 
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
		secondFile(similarityContext, chromoContext, hirsContext, firstFile, reader2);
	}
	
	
	/**
	 * Here we temporarily rely on only one allowable full mismatch inside a HIR.
	 * If 2 or more will be necessary - we should implement this method in 
	 * another way: to keep Hirs in an ordered list and check each of them - 
	 * whether the number of full mismatches reached the max - 
	 * under if(fullMismatch)
	 * and increment each of them - every match/half.
	 * For now - it seems 2 are standard and nobody asks for variable, so this 
	 * approach is slightly lighter 
	 */
	private void secondFile(SimilarityContext similarityContext, ChromoContext chromoContext, 
			HirsContext hirsContext, HashMap firstFile, RawReader reader2) throws Exception{
		
		//2 sets of vars for 2 intersecting blocks
		CMRegion hir1 = null;
		CMRegion hir2 = null;
		
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
				if(Main.warning)System.err.println("WARN: Positions for " + record1.snip + " are different: " + record1.position +"," + record2.position + " and the second position will be shown in the report.");
			}
			
			//if we should not count the snips with -- - remove the following comments and put 
			//comments in isFullMismatch method:
			//we assume here (having looked into 23andme data) that no call is always '--', even for male's one-letter base
			//if(record1.base.charAt(0)=='-' || record2.base.charAt(0)=='-'){
			//	continue; //either no call is match
			//}
			
			lineCounter++;
			
			//
			// Here is the main algorithm. We have 2 big blocks match and mismatch:
			//
			
			//Here we are - in case of match
			//I.e. if we are in the middle - just update the counters and end positions
			//At every moment we store information about 2 HIRs, alternating between them.
			//If in future we need to add 2, 3 or N mismatches - we'll add ArrayList instead 
			//of those 2.
			if(!isFullMismatch(record1.base, record2.base, similarityContext)){

				if(hir1==null){
					if(hir2==null){ //if both hir1 and hir2 are nulls - we use the first one
						hir1=new CMRegion(chromoContext.chromo, lineCounter, record2.position);
					}
					else{ //in hir2
						if(hir2.contansOneMismatch){
							hir1=new CMRegion(chromoContext.chromo, lineCounter, record2.position);
						}
						hir2.advance(chromoContext.chromo, lineCounter, record2.position);
						hir2.endPos=record2.position;						
					}
				}
				else{ //in hir1
					hir1.advance(chromoContext.chromo, lineCounter, record2.position);
					hir1.endPos=record2.position;						
					
					if(hir2==null){
						if(hir1.contansOneMismatch){
							hir2=new CMRegion(chromoContext.chromo, lineCounter, record2.position);
						}
					}
					else{ //in hir2
						hir2.advance(chromoContext.chromo, lineCounter, record2.position);
						hir2.endPos=record2.position;						
					}
				}
				continue; //to avoid else, i.e. extra nesting (to simplify the rest under while)
			}
			
			//
			//And here we are - if we have a FULL MISMATCH:
			//
			
			if(hir1!=null){
				if(hir1.contansOneMismatch){
					//print results for hir1 and terminate it:
					hirEnded(hir1, chromoContext, hirsContext);
					hir1=null;
					if(hir2!=null){
						if(hir2.contansOneMismatch){
							System.out.println("This should never occur: both intersecting regions contain mismatches");
							return; // unexpected error
						}
						else{
							hir2.contansOneMismatch = true;
							hir2.advance(chromoContext.chromo, lineCounter, record2.position);
						}
					}
				}
				else{//this is the very first mismatch for hir1 
					if(hir2!=null){
						if(hir2.contansOneMismatch){
							//print results for hir2 and terminate it:
							hirEnded(hir2, chromoContext, hirsContext);
							hir2=null;
						}
						else{//
							System.out.println("This should not occur: both intersecting regions has no mismatches");
							return;
							//hir2.contansOneMismatch = true;
							//hir2.count++;
						}
					}
					hir1.contansOneMismatch = true;
					hir1.advance(chromoContext.chromo, lineCounter, record2.position);
				}
			}
			else{ //hir1==null, but hir2 may be not null
				if(hir2!=null){
					if(hir2.contansOneMismatch){
						//print results for hir2 and terminate it:
						hirEnded(hir2, chromoContext, hirsContext);
						hir2=null;
					}
					else{
						hir2.contansOneMismatch = true;
						hir2.advance(chromoContext.chromo, lineCounter, record2.position);		
					}
				}
			}

		
		}//while getNextRecord
		
		//print last in the order (we can have one more HIR - without mismatch)
		if(hir1!=null){
			if(hir2!=null){
				if(hir1.start<hir2.start){
					hirEnded(hir1, chromoContext, hirsContext);
					hirEnded(hir2, chromoContext, hirsContext);
				}
				else{
					hirEnded(hir2, chromoContext, hirsContext);
					hirEnded(hir1, chromoContext, hirsContext);
				}
			}
			else{ //hir2==null
				hirEnded(hir1, chromoContext, hirsContext);
			}
		}
		else{ //hir1==null
			if(hir2!=null){
				hirEnded(hir2, chromoContext, hirsContext);
			}
		}
		
		printChromoReport(chromoContext, hirsContext);
	}
	
	
	/**
	 * Call-back on a HIR end  
	 */
	private void hirEnded(CMRegion hir, ChromoContext chromoContext, HirsContext hirsContext){
		
		if(hir.getCount()<MIN_THREDHOLD){
			return;
		}
		
		/* no cM data for X, so the threshold is hardcoded to 0 (for X only) */
		if(/*!hirsContext.skipCentimorganThreshold && */hir.cMDistance()<CM_THRESHOLD){
			//System.out.println("Thresholds: m=" + hir.getCount()+" ,cM="+hir.cMDistance());
			return;
		}

		hirsContext.addHir(hir);
		//printHir(hir, context); //REMOVE ME!
	}
	
	
	/**
	 * After all HIRs are collected 
	 */
	private void postProcess(HirsContext context){

		int n=context.hirs.size();
		for(int i=0; i<n; i++){
			CMRegion hir = (CMRegion)context.hirs.get(i);
			context.sortedHirs.add(hir);
		}
		//sort in ascending order (from bigger HIRs to smaller)
	    java.util.Collections.sort(context.sortedHirs, hirsComparator);
		/*
	    System.out.println("Natural:");
	    System.out.println(context.hirs);
	    System.out.println("Sorted:");
	    System.out.println(context.sortedHirs);
	    */
	    //algorithm is the following:
		//go through the sorted list in descending order (from the biggest) 
	    //and mark intersecting neighbors to be removed  
		for(int i=0; i<n; i++){
			CMRegion hir = (CMRegion)context.sortedHirs.get(i);
			//System.out.println(hir);
			
			if(hir.removed)
				continue; //already removed
			
			int indexInList=hir.indexInNaturalList; //which index this Hir is under - in the main list
			
			//System.out.println(indexInList);
			
			if(indexInList>0){ //i.e. there is the predecessor
				CMRegion leftNeigbour = (CMRegion)context.hirs.get(indexInList-1);
				if(leftNeigbour.end>hir.start){
					leftNeigbour.removed=true;
					//System.out.println("Setting removed to hir: " + leftNeigbour);
				}
			}
			if(indexInList<n-1){ //i.e. there is the successor
				CMRegion rightNeigbour = (CMRegion)context.hirs.get(indexInList+1);
				//System.out.println("rightNeigbour=" + rightNeigbour+" me="+hir);
				if(rightNeigbour.start<hir.end){
					rightNeigbour.removed=true;
					//System.out.println("Setting removed to hir: " + rightNeigbour);
				}				
			}
			//printHir(hir, context);
		}
/*
		System.out.println("natural order:");
		for(int i=0; i<n; i++){
			System.out.println(context.hirs.get(i));
		}		
		
		System.out.println("sorted:");
		for(int i=0; i<n; i++){
			System.out.println(context.sortedHirs.get(i));
		}
	*/    
	}

	
	private void printChromoReport(ChromoContext chromoContext, HirsContext hirsContext){

		//post processing of hirs
		postProcess(hirsContext);
		
		int n=hirsContext.sortedHirs.size();
		for(int i=0; i<n; i++){
			CMRegion hir = (CMRegion)hirsContext.sortedHirs.get(i);
			if(!hir.removed){
				printHir(hir, chromoContext);
			}
		}
		
		/* biggest HIR - if necessary at the end (we are printing it actually at the begning currently)
		if(chromoContext.biggestHir!=null){
			//System.out.println("The biggest HIR in chromosome " + context.chromo+":");
			System.out.println("Biggest: "+ chromoContext.file1 + " " + chromoContext.file2 + " " + 
					chromoContext.chromo + " " + chromoContext.biggestHir.getCount() + " " + 
					chromoContext.biggestHir.startPos + " " + chromoContext.biggestHir.endPos + " " + 
					df.format(chromoContext.biggestHir.cMDistance()));
		}*/
	}
	
	
	private void printHir(CMRegion hir, ChromoContext chromoContext){
		System.out.println(chromoContext.file1 + " " + chromoContext.file2 + " " + 
				chromoContext.chromo + " " + hir.getCount() + " " + 
				hir.startPos + " " + hir.endPos + " " + Constants.df.format(hir.cMDistance()));
	}
	
	
	private void print(PrintStream out){
		
	}
	
	
	/**
	 * Compare 2 bases and return true - if we have a full mismatch.
	 * Example: 
	 * true returned for bases: (AD TT), (AT II), (AT DD), (A T), (D I), (A DT). 
	 * but not for bases: (AA, AT), (AD --), (-- T), (A AT)      
	 */
	private boolean isFullMismatch(String base1, String base2, SimilarityContext similarityContext){

		//we assume here (looked into 23andme data) that no call is always '--', even for male one-letter base
		if(base1.charAt(0)=='-' || base2.charAt(0)=='-'){
			return false; //either no call is match
		}
		
		//NOTICE: if we'll need to count no-calls in similarity - we have to call
		//the following method _before_ previous line. For now we benefit from one invocation 
		if(calculateSimilarity)
			Similarity.calculateSimilarity(base1, base2, similarityContext);
		
		if(base1.length()==1){
			if(base2.length()==1){ //both males
				if(base1.charAt(0)!=base2.charAt(0)){
					return true;
				}
				else{
					return false;
				}
			}
			else{ //base2.length==2
				//System.out.println("WARN: there is one-letter on one side and 2 - on another one");
				if(base1.charAt(0)!=base2.charAt(0) && base1.charAt(0)!=base2.charAt(1)){
					return true;
				}
				else{ //one letter is matching
					return false;
				}
			}
		}
		else{//base1.length()==2
			if(base2.length()==1){
				//System.out.println("WARN: there are 2 letters on one side and 1 - on another one");
				if(base1.charAt(0)!=base2.charAt(0) && base2.charAt(0)!=base1.charAt(1)){
					return true;
				}
				else{ //one letter is matching
					return false;
				}
			}
			else{//base2.length()==2
				//we assume that order is the same, so cannot be AD and DA
				if(base1.charAt(0)==base2.charAt(0)){
					return false;
				}
				else{
					if(base1.charAt(1)==base2.charAt(1)){
						return false;
					}
					else{ //full mismatch
						return true;						
					}
				}

			}//else base2.length()==2
		}//else base1.length()==2
	}
	
	
}
