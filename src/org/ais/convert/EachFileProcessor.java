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
package org.ais.convert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Base (superclass) for all processors which use only one file as input
 * (example - RAW2PED processor). Even if a directory or a set of directories 
 * is passed - only individual files from those directories are taken. 
 */
public abstract class EachFileProcessor extends ProcessorBase{
	

	public abstract boolean processFile(String filePath, BufferedWriter writer) throws Exception;
		
	
	public final void process(Result result) throws Exception{
		
		ArrayList listOfFilesCollected = (ArrayList)Parameters.getInstance().get(Constants.INPUT_FILE_KEY);
		for(int i=0; i<listOfFilesCollected.size(); i++){
			String path = (String)listOfFilesCollected.get(i);
			File file = new File(path);
			if(!file.exists() || !file.canRead()){
				System.out.println("Cannot read: " + path);
				continue;
			}
			if(file.isFile()){
				processFile(path, null);
			}
			else if(file.isDirectory()){ //process all files in this directory
				processDir(path);
			}
		}		
	}
	
	
	protected void processDir(String filesDir) throws Exception{

		BufferedWriter writer = null;
		try{
			//System.out.println("Trying to open: " + Config.getOutputFile());
			
			writer=new BufferedWriter(new FileWriter(Config.getOutputFile(), true));			
			
			File mainDir = new File(filesDir);
		    int entries = mainDir.list().length;
	
		    String[] level1files = mainDir.list();
		    
			//delete previous error report file
		    /*
			File oldFile = new File(Config.getDefaultErrorReportFile());
			if(oldFile.exists())
				oldFile.delete();
			*/

		    
			if(Main.progressListener!=null)Main.progressListener.start(entries); //show progress bar
			
		    for(int i = 0; i < entries; i++){
		    	String filePath=filesDir + File.separator + level1files[i];
		        File aFile = new File(filePath);
		        
		        if(aFile.isDirectory()){
		        	continue; //skip directories
		        }
	        	else if(aFile.isFile()){
	        		processFile(filePath, writer);
	        		if(Main.progressListener!=null)Main.progressListener.updateStatus();
	        	}
		    }
		    
		    if(Main.progressListener!=null)Main.progressListener.finish(); //hide progress bar
		    
		}
		finally{
			if(writer!=null)try{writer.close();}catch(Exception e){}
		}
	}	
	
}
