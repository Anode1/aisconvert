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
package org.ais.convert.phase;

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
 * Phase two files (child and parent) producing 3rd (difference) file   
 */
public class Phase extends TwoFilesProcessor{
	

	private String outputFilename;
	
	private static ArrayList chromoList = new ArrayList();
	
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
			String newFileName=FileUtils.getWithoutExtension(file2.getName()) + "_phased.txt";
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

		
		//advance the tape to the necessary chromosome :)
		
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
		
		//here we know the format, so let out format to be as 2nd file format
		out.setFormat(reader2.getFormat());
		
		while(record1!=null || record2!=null){
			
			if(record1!=null){
				if(record2!=null){
					int locPos = compare(record1, record2); 
					if(locPos==1){ //record1<record2
						record1=reader1.getNextRecord(); //advance 1st
					}
					else if(locPos==0){ //the same location and same snip
						
						String result = getPhased(record1.base, record2.base);
						
						//System.out.println(record1.base + " " + record2.base + " => "+result);
						
						record2.base=result; //change record2 (not gonna to use it again)
						out.write(record2);
			
						record1=reader1.getNextRecord(); //advance 1st
						record2=reader2.getNextRecord(); //advance 2nd
					}
					else{ //-1, i.e. record2 is before
						record2=reader2.getNextRecord(); //advance 2nd						
					}
				}
				else{ //record2==null but record1!=null
					record1=reader1.getNextRecord(); //advance 1st
				}
			}
			else{ //record1==null but record2!=null (according to while condition) 
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
	
	
	/**
	 * Convenience method. See method with the same name below
	 */
	public static String getPhased(String parent, String child) throws Exception{
		
		if(parent.length()!=2 || child.length()!=2)
			return "--";
		return getPhased(parent.charAt(0), parent.charAt(1), child.charAt(0), child.charAt(1));
	}
	
	
	/**
	 * Takes bialleles of the parent and the child and returns phased genotype (biallele)
	     The following combinations allowed but we are not validating here, having routine generic 		
		 AG,CT,AC,GT,CG,AT 
		 Below are AT pair combinations only:
		 (AA,AA)=>AA
		 (AA,AT)=>TT  		 (AA,TA)=>TT,
		 (AA,TT)=>TT
		 (AT,AA)=>AA		 (TA,AA)=>AA
		 (AT,AT)=>AT		 (AT,TA)=>AT		 (TA,AT)=>AT		 (TA,TA)=>AT <== changed in the last version to -- 
		 (AT,TT)=>TT	     (TA,TT)=>TT

		 Assuming (AB,XY) notation, where AB is the paren't genome, XY the descendent's one...
		 Writing down graphs with 4 nodes and edges connecting same letter (it is easier for me to solve all combinations graphically!), I got 8 valid combinations:
		 (A=B=X=Y), (A=X,B=Y,A!=B), (A=B=X, A!=Y), (A!=B, B=X=Y), (A=B,X=Y,A!=Y), (A!=B,A=Y,B=X), (A=B=Y,X!=Y), (A=X=Y, A!=B)
		 and 7 invalid (3 or 4 letter are distinct):
		 (we can still use ones with the error in paren't matrix, assuming that matrix was fine):
		 (A!=B,A!=X,B!=X,X=Y),(A!=Y,B=X,A!=Y,X!=Y),(A=Y,A!=B,B!=X),(A=X,A!=B,B!=Y,A!=Y),(A=B,A!=X,A!=Y,X!=Y),(A!=B,A!=X,A!=Y,B!=X,B=Y),(A!=B,A!=X,A!=Y,B!=X,X!=Y)  
	 */
	public static String getPhased(char a, char b, char x, char y) throws Exception{
		
		//Some special (no-call) cases 
		if(x=='-' || y=='-') //if child genome unknown
			return "--";
		if(a=='-' || b=='-') //uncertanty but one of child's genome should be correct (take both)
			return Character.toString(x)+Character.toString(y);
			
		
		if(a==x){ 
			if(b==y){
				if(a==b){
					return Character.toString(x)+Character.toString(x); //A==X,B==Y,A==B, example: (AA,AA)=>AA)
				}
				else{ //A!=B
					//return Character.toString(x)+Character.toString(y); //A==X,B==Y,A!=B, example: (AT,AT)=>AT, (TA,TA)=>TA
					return "--"; //due to Alexander and Vadim
				}
			}
			else{ //B!=Y
				if(b==x){
					return Character.toString(y)+Character.toString(y); //A==X,B!=Y,B==X, example: (AA,AT)=>TT 
				}
				else{ //B!=X 
					if(a==y){
						return Character.toString(y)+Character.toString(y); //A==X,B!=Y,B!=X,A==Y, example: (AT,AA)=>AA
					}
					else{ //A!=Y
						return "--"; //A==X,B!=Y,A!=Y, example: (AT,AX)=>--
					}
				}
			}
		}
		else{//A!=X
			if(a==y){
				if(b==y){
					return Character.toString(x)+Character.toString(x); //A!=X,A==Y,B==Y,B!=X (B=X was checked before, so never here), (AA,TA)=>TT
				}
				else{ //B!=Y
					if(b==x){
						//return Character.toString(x)+Character.toString(y); //A!=X,A==Y,B!=Y,B==X, example: (AT,TA)=>TA, (TA,AT)=>AT
						return "--"; //due to Alexander and Vadim
					}
					else{ //B!=X
						return "--"; //A!=X,A==Y,B!=Y,B!=X, example: (AT,CA)=>--
					}
				}
			}
			else{ //A!=X,A!=Y
				if(b==y){
					if(b==x){
						return Character.toString(y)+Character.toString(y);//A!=X,A!=Y,B==Y,B==X example: (AT,TT)=>TT
					}
					else{ //B!=X
						return "--"; //A!=X,A!=Y,B==Y,B!=X example: (AT,CT)=>--
					}
				}
				else{ //A!=X,A!=Y,B!=Y
					if(b==x){
						return "--"; //A!=X,A!=Y,B!=Y,B=X example: (AT,TC)=>--
					}
					else{ //A!=X,A!=Y,B!=Y,B!=X
						if(a==b){
							if(x==y){
								return Character.toString(x)+Character.toString(y); //A!=X,A!=Y,B!=Y,B!=X,A=B,X=Y example: (AA,TT)=>TT,(TT,AA)=>AA
							}
							else{ //X!=Y
								return "--"; //A!=X,A!=Y,B!=Y,B!=X,A=B,X!=Y example: (AA,CG)=>--
							}
						}
						else{ //A!=B
							return "--"; //A!=X,A!=Y,B!=Y,B!=X,A!=B example: (AT,CC)=>--, (AT,CG)=>--
						}
					} //else B!=X
				} //else B!=Y
			} //else A!=Y)
		} //else A!=X		
	}
	
	
	private int compare(RawRecord record1, RawRecord record2){
		
		if(record1==null)
			return -1;
		if(record2==null)
			return 1;
		
		return record1.compareLocation(record2); //tests also - whether records have the same snip
	}

	
}
