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
package org.ais.convert.hz;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.ListIterator;

import org.ais.convert.ChromoContext;
import org.ais.convert.Constants;
import org.ais.convert.Parameters;
import org.ais.convert.RawReader;
import org.ais.convert.RawRecord;
import org.ais.convert.Region;
import org.ais.convert.Result;
import org.ais.convert.EachFileProcessor;
import org.ais.convert.hir.CMorgans;

/**
 * Homozygosity calculator  
 */
public class Homozygousity extends EachFileProcessor{
	
	private int MIN_THREDHOLD = 200;
	//private float CM_THRESHOLD = Constants.DEFAULT_CM;	
	private boolean treatNoCallsAsHomozygous=true;
	public static java.text.DecimalFormat df = new java.text.DecimalFormat("##0.0000");
	
	public void init(Result result) throws Exception{
		
		//validate necessary parameters
		
		if(Parameters.getObject(Constants.INPUT_FILE_KEY)==null){
			result.addError();
			System.out.println("You should pass at least one input file - to snips convertor.");
			return;
		}
		
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
		
		arg = Parameters.getString(Constants.TREAT_NO_CALLS_AS_HOMOZYGOUS);
		if(arg!=null){
			treatNoCallsAsHomozygous = Parameters.getAsBoolean(Constants.TREAT_NO_CALLS_AS_HOMOZYGOUS);
		}

		/*
		//read centimorgans mapping:
		if(!CMorgans.init()){
			result.addError();
			return;
		}*/		
	}	
	
	
	public boolean processFile(String filePath, BufferedWriter writer) throws Exception{
		
		RawReader reader = null;
		
		try{
			File file = new File(filePath);
			if(!file.exists() || !file.canRead()){
				System.out.println("Cannot read file");
				System.out.println();
				return false;
			}

			reader = new RawReader(new BufferedReader(new FileReader(file)));

			RawRecord record = null;

			String prevChromo=null;
			int lineCounter=0;
			
			while((record=reader.getNextRecord())!=null){
				
				lineCounter++;
				
				if(!record.chromosome.equals(prevChromo)){
					if(prevChromo!=null){
						onEndChromo(prevChromo);
					}
					onStartChromo(lineCounter, record, prevChromo, record.chromosome);
				}
										
				checkBase(lineCounter, record, prevChromo);
			
				prevChromo=record.chromosome;				
			}
			
			if(region!=null)
				onEnd(region, prevChromo);
			
			if(prevChromo!=null)
				onEndChromo(prevChromo);
			
			finalReport();

			return true;
		}
		finally{
			if(reader!=null)try{reader.close();}catch(Exception e){}
		}
	}

	
	private Region region=null;
	private int countHomo=0;
	private int countHetero=0;
	private int countNoCalls=0;
	private int totalHomo=0;
	private int total=0;
	private int totalNoCalls=0;
	
	
	private void onStartChromo(int lineCounter, RawRecord record, 
			String prevChromo, String chromo){
		
		if(region!=null) //previous region
			onEnd(region, prevChromo);
		
		countHomo=0;
		countHetero=0;
		countNoCalls=0;
	}
	
	
	private void onEndChromo(String chromo){
		
		if(!chromo.equals("X") && !chromo.equals("Y") &&
				!chromo.equals("MT")){
		
			float percentage = 100*countHomo/(float)(countHomo+countHetero);
			System.out.println("Chromosome " + chromo+ ": " + df.format(percentage) + 
					"% are homozygous, " + countNoCalls + " no-calls");
			
			totalHomo+=countHomo;
			total+=countHomo+countHetero;
			totalNoCalls+=countNoCalls;
		}
		else{
			System.out.println("Chromosome " + chromo+ ": " + countNoCalls + " no-calls");
		}
	}
	
	
	//hir2.advance(lineCounter, record2.snip);
	//hir2.endPos=record2.position;	
	
	private void onEnd(Region region, String prevChromo){
		
		if(region.getCount()<MIN_THREDHOLD){
			return;
		}
		
		if(!prevChromo.equals("X") && !prevChromo.equals("Y") &&
				!prevChromo.equals("MT")){
			System.out.println(prevChromo + " " + region.getCount() + " " + 
				region.startPos + " " + region.endPos);
		}
	}
	
	
	private void finalReport(){
		float percentage = 100*totalHomo/(float)(total);
		System.out.println("Total (Chr 1-22): " + df.format(percentage) + 
				"% are homozygous, " + totalNoCalls+ " no-calls"); 
	}
	
	
	private void homo(int lineCounter, RawRecord record){
		
		if(region==null){
			region=new Region(lineCounter, record.position);
		}
		region.advance(lineCounter);
		region.endPos=record.position;
		
		countHomo++;
	}
	

	private void hetero(int lineCounter, RawRecord record, String prevChromo){
		
		if(region!=null){
			onEnd(region, prevChromo);
			region=null;
		}
		
		countHetero++;
	}	
	
	
	private void checkBase(int lineCounter, RawRecord record, String prevChromo){
		
		String base=record.base;
		
		if(base.charAt(0)=='-'){
			if(treatNoCallsAsHomozygous){
				homo(lineCounter, record);
			}
			else{
				hetero(lineCounter, record, prevChromo);
			}
			countNoCalls++;
			return;
		}				
		
		if(base.length()==1){ //hemizygous
			return;
		}
		
		if(base.length()==2){
			if(base.charAt(0)!=base.charAt(1)){ //heterozygous
				hetero(lineCounter, record, prevChromo);
			}
			else{ //homozygous
				homo(lineCounter, record);
			}
		}
	}
	
}
