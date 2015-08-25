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

import java.io.File;
import java.util.ArrayList;

/**
 * Base (superclass) for all processors which use only one file as input
 * (example - RAW2PED processor). Even if a directory or a set of directories 
 * is passed - only individual files from those directories are taken.
 */
public abstract class EachPairProcessor extends ProcessorBase{
	

	public void init(Result result) throws Exception{
		
		//validation of the input
		
		//there must be minimum 2 files in the list:
		ArrayList listOfFilesCollected = (ArrayList)Parameters.getInstance().get(Constants.INPUT_FILE_KEY);
		if(listOfFilesCollected==null){
			result.addError();
			System.out.println("You have to pass either 2 files or more or directories");
			return;
		}
		else if(listOfFilesCollected.size()==1){ //one file and it is not a directory
			String path = (String)listOfFilesCollected.get(0);
			File file = new File(path);
			if(!file.isDirectory()){
				result.addError();
				System.out.println("You have to pass either 2 files or more or 1 directory or more");
				return;
			}
		}
		else{
			boolean thereIsFile = false;
			boolean thereIsDir = false;
			//check whether there are no files mixed with directories
			for(int i=0; i<listOfFilesCollected.size(); i++){
				String path = (String)listOfFilesCollected.get(i);
				File file = new File(path);
				if(!file.exists() || !file.canRead()){//we'll check readabilty later
					continue;
				}
				if(file.isFile()){
					thereIsFile = true;
				}
				else if(file.isDirectory()){
					thereIsDir = true;
				}
			}
			if(thereIsFile && thereIsDir){
				result.addError();
				System.out.println("You can't mix files and directories in this mode: pass either files (2 or more) or directories (1 or more)");
				return;
			}
		}
	}
	
	
	protected abstract void processFilesPair(File file1, File file2) throws Exception;
	
	
	public final void process(Result result) throws Exception{
		
		ArrayList listOfFilesCollected = (ArrayList)Parameters.getInstance().get(Constants.INPUT_FILE_KEY);
		
		//we assume that at least one file is passed (if one - a directory) or  
		//if it file - it must be at least two of them
		String path = (String)listOfFilesCollected.get(0);
		File file = new File(path);
		if(!file.exists() || !file.canRead()){
			System.out.println("Cannot read: " + path);
			return;
		}
		if(file.isFile()){
			processSet(null, listOfFilesCollected); //full paths
		}
		else if(file.isDirectory()){
			for(int i=0; i<listOfFilesCollected.size(); i++){
				path = (String)listOfFilesCollected.get(i);
				file = new File(path);
				if(!file.exists() || !file.canRead()){
					System.out.println("Cannot read: " + path);
					continue;
				}
				if(file.isDirectory()){ //process all files in this directory
					String[] list = file.list();
					ArrayList arrayList = new ArrayList(list.length);
					for(int l=0; l<list.length; l++){
						arrayList.add(list[l]);
					}
					processSet(path, arrayList); //path and filenames
				}
			}//for			
		}//else if isDirectory
	
	}


	private void processSet(String path, ArrayList arrayList) throws Exception{

		java.util.Collections.sort(arrayList);
		
		String[] list = (String [])arrayList.toArray(new String[0]);
		
		int n = list.length;
	    
		if(Main.progressListener!=null)Main.progressListener.start(n); //show progress bar
		
	    for(int i=0; i<n; i++){
	    	
    		String filePath1;
    		
	    	if(path!=null)
	    		filePath1=path + File.separator + list[i];
	    	else
	    		filePath1=list[i];
	    	
	    	File file1 = new File(filePath1);
	        if(!file1.isFile() || !file1.canRead()){
	        	continue; //skip not files
	        }
	        
	    	for(int j=i+1; j<n; j++){
	    		String filePath2;
	    		
		    	if(path!=null)
		    		filePath2=path + File.separator + list[j];
		    	else
		    		filePath2=list[j];

		    	File file2 = new File(filePath2);
		        if(!file2.isFile()){
		        	continue; //skip directories in directories
		        }
		        
		        processFilesPair(file1, file2);
	        		
        		if(Main.progressListener!=null)Main.progressListener.updateStatus();
	    	}//for j
	    }//for i
	    
	    if(Main.progressListener!=null)Main.progressListener.finish(); //hide progress bar
	}
	
	
}
