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
package org.ais.convert.converters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.ListIterator;

import org.ais.convert.Config;
import org.ais.convert.Constants;
import org.ais.convert.EachFileProcessor;
import org.ais.convert.Main;
import org.ais.convert.Parameters;
import org.ais.convert.RawReader;
import org.ais.convert.RawRecord;
import org.ais.convert.Result;

public class Raw2Ped extends EachFileProcessor{
	
	private ArrayList snipsList;

	
	public void init(Result result) throws Exception{
		
		//validate necessary parameters
		
		if(Parameters.getObject(Constants.INPUT_FILE_KEY)==null){
			result.addError();
			System.out.println("You should pass at least one input file - to snips convertor.");
			return;
		}

		if(Parameters.getString(Constants.SNIPS_MAPPING_KEY)==null){
			if(Main.warning)System.out.println("WARN: Snips not passed, default is used: " + Config.getDefaultSnipsMappingFile());
			Parameters.getInstance().put(Constants.SNIPS_MAPPING_KEY, Config.getDefaultSnipsMappingFile());
		}	
		
		snipsList=Snips.readSnips(result);
	}	
	
	
	public boolean processFile(String filePath, BufferedWriter writer) throws Exception{
		
		BufferedWriter reportWriter = null;
		RawReader reader = null;
		boolean need2Close=false; //if not batch
		
		try{
			File file = new File(filePath);
			if(!file.exists() || !file.canRead()){
				System.out.println("Cannot read file");
				System.out.println();
				return false;
			}

			//report
			//reportWriter = new BufferedWriter(new FileWriter(Config.getOutputErrorReportFile(), true));		

		
			ListIterator snipsIt = snipsList.listIterator();

			reader = new RawReader(new BufferedReader(new FileReader(file)));
			
			if(writer==null){
				writer=new BufferedWriter(new FileWriter(Config.getOutputFile(), true));
				need2Close=true;
			}

			//Currently family,ids etc (first columns before alleles) are 
			//hard-coded, but likely will replaced by joining with FAM file

			
			//family
			writer.write(file.getName());
			writer.write(" ");
			//id
			writer.write("1"); 
			writer.write(" ");
			//father id
			writer.write("1");
			writer.write(" ");
			//mother id
			writer.write("1");
			writer.write(" ");
			//gender
			writer.write("1");
			writer.write(" ");
			//cat
			writer.write("1");
			writer.write(" ");
			
			
			//
			// The following is RIGHT OUTER JOIN:
			// snips (linesd) in RAW file and snips given in snips file.
			// Algorithm:
			// Snips in RAW and in the master list are compared lexigraphically 
			// and if the current snip in RAW is smaller - 
			// we have a new snip - just skip it, if the same - advance the line. 
			// If bigger - then we do not have the corresponding snip in RAW file
			// and put default alleles (0 0) - to fill the hole.		
			//
			
			RawRecord prevRecord = null;
			boolean eof=false;
			
	    	//while we have smaller snips - read those
		    while(snipsIt.hasNext()){
		    	String snip2Write = (String)snipsIt.next();
		    	
	    		if(prevRecord!=null){
	    			int res = prevRecord.snip.compareTo(snip2Write);
	    			if(res==0){
	    				PedWriter.writeAllele(prevRecord, writer);
	    				continue;
	    			}
	    		}
    	
		    	
		    	if(eof){ //reached the end
		    		PedWriter.writeDefault(writer);
		    	}
		    	else{ //there are records in RAW file
			    	RawRecord rawRecord = reader.getNextRecord();
			    	if(rawRecord!=null){
			    		int res = rawRecord.snip.compareTo(snip2Write);
				    	
			    		if(res==0){
			    			PedWriter.writeAllele(rawRecord, writer);
				    		prevRecord=rawRecord;
				    	}
				    	else if(res<0){
				    		while(res<0){ //forward absent in the map snips
				    			rawRecord = reader.getNextRecord();
				    			if(rawRecord==null){
				    				eof=true;
				    				break;
				    			}
				    			else{
				    				res = rawRecord.snip.compareTo(snip2Write);
				    				prevRecord=rawRecord;
				    			}
				    		}
				    	}
				    	else{ //snipFromRaw > snipsIter
				    		PedWriter.writeDefault(writer);
				    		prevRecord=rawRecord;
				    	}
			    	} 
			    	else{ //rawRecord==null
			    		eof=true;
			    		if(prevRecord!=null){
			    			int res = prevRecord.snip.compareTo(snip2Write);
			    			if(res==0){
			    				PedWriter.writeAllele(prevRecord, writer);
			    			}
				    		else{
				    			PedWriter.writeDefault(writer);
				    		}
			    		}
			    		else{
			    			PedWriter.writeDefault(writer);
			    		}
			    	}
		    	}//!eof
		    	
			}//for snips			
			
		    writer.newLine();
			
			return true;
		}
		finally{
			if(reader!=null)try{reader.close();}catch(Exception e){}
			if(reportWriter!=null)try{reportWriter.close();}catch(Exception e){}
			if(need2Close)try{writer.close();}catch(Exception e){}
		}
		
	}
	
	
}
