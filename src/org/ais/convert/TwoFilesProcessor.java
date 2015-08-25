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
 * Processor accepting exactly 2 files 
 */
public abstract class TwoFilesProcessor extends ProcessorBase{
	
	
	public void init(Result result) throws Exception{
		
		//we must have 2 files passed exactly:
		ArrayList listOfFilesCollected = (ArrayList)Parameters.getInstance().get(Constants.INPUT_FILE_KEY);
		if(listOfFilesCollected==null || listOfFilesCollected.size()!=2){
			result.addError();
			System.out.println("You have to pass 2 files exactly to this processor");
			return;
		}
	}
	
	
	public final void process(Result result) throws Exception{
		
		ArrayList listOfFilesCollected = (ArrayList)Parameters.getInstance().get(Constants.INPUT_FILE_KEY);
		String path1 = (String)listOfFilesCollected.get(0);
		File file1 = new File(path1);
		if(!file1.isFile() || !file1.canRead()){
			result.addError();
			System.out.println("Can't read first file: " +path1);
			return;
		}
		
		String path2 = (String)listOfFilesCollected.get(1);
		File file2 = new File(path2);
		if(!file2.isFile() || !file2.canRead()){
			result.addError();
			System.out.println("Can't read second file: " + path2);
			return;
		}
		
		processFilesPair(file1, file2);
	}
	
	
	protected abstract void processFilesPair(File file1, File file2) throws Exception;

}
