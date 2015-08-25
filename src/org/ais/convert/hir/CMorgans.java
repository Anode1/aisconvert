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
import java.util.HashMap;

import org.ais.convert.Constants;
import org.ais.convert.Parameters;
import org.ais.convert.Tokenizer;

/**
 * Container for centiMorgans hashes. Since we do not want to keep all them 
 * in-memory (not to allocate too much memory) - we are loading by pieces
 */
public class CMorgans {
	
	private static HashMap cM = new HashMap();

	
	public static boolean init() throws Exception{
		/*
		String pathToCM = Parameters.getString(Constants.SM_KEY);
		if(pathToCM==null){
			System.out.println("WARN: cM maps directory: " + Constants.DEFAULT_SM_FILE);
			//Parameters.getInstance().put(Constants.SM_KEY, Constants.DEFAULT_SM_FILE);
			;
		}
		*/
		
		String pathToCM = null;//Parameters.getString(Constants.SNIPS_MAPPING_KEY);
		//we do not need to pass cM file as argument
		if(pathToCM==null){
			//if(Main.warning)System.out.println("WARN: Snips not passed, default is used: " + Constants.DEFAULT_SM_FILE);
			//Parameters.getInstance().put(Constants.SNIPS_MAPPING_KEY, Constants.DEFAULT_SM_FILE);
			pathToCM=Constants.DEFAULT_SM_FILE;
		}	
		
		//read cM mapping:
		BufferedReader reader = null;
		try{
			File file= new File(pathToCM);
			if(file.exists() && file.canRead()){
				reader = new BufferedReader(new FileReader(file));
				String line;
				while((line = reader.readLine()) != null){
					
					if(line.startsWith("#"))
						continue;
					
					String[] records = Tokenizer.tokenize(line, "\t, ");
					if(records.length>2){
						String pos = records[0].trim();
						String weight = records[1];
						String chromo = records[2].trim();
						
						Float val;
						try{
							val = new Float(Float.parseFloat(weight));
							//System.out.println(snip);
						}
						catch(Exception e){
							System.out.println("Can't parse number in map: "+weight);
							continue;
						}
						
						HashMap chromoMap = (HashMap)cM.get(chromo);
						if(chromoMap==null){
							chromoMap = new HashMap();
							cM.put(chromo, chromoMap);
						}
						
						chromoMap.put(pos, val);
					}

				}
			}
			else{
				System.out.println("Cannot read file with centiMorgans: " + pathToCM);				
				return false;
			}
			return true;
		}
		finally{
			if(reader!=null)try{reader.close();}catch(Exception e){}	
		}
	}
	

	public static HashMap get(String chromo){
		return (HashMap)cM.get(chromo);
	}	
	
	
	public static Float get(String chromo, String pos){
		HashMap chromoMap = (HashMap)cM.get(chromo);
		if(chromoMap==null){ //no chromosome in mapping?
			return new Float(0);
		}
		Float cM = (Float)chromoMap.get(pos);
		return cM==null?new Float(0):cM;
	}
	

	public static int size(){
		return cM.size();
	}
	
}
