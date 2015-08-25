package org.ais.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class FileUtils{
	
    /**
     * Removes extension from the filename (last token delimited by '.')  
     * 
     * @param filename
     * @return
     */
    public static String getWithoutExtension(String filename){
        
    	//System.out.println(filename);
        int i=filename.lastIndexOf(".");
        if(i==-1)
           return filename;
        else
           return filename.substring(0, i);
    }
    
    
    /**
     * Finds the first line starting with string, returning the whole line 
     */
    public static String extractLineStartWith(File file, String startsWith) throws Exception{
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!=null){
				if(line.startsWith(startsWith)){
					return line;
				}
			}
			return null;
		}
		finally{
			if(reader!=null)try{reader.close();}catch(Exception e){}
		}
    }
    

    /**
     * Finds the lines starting with string, returning the Hash 
     */
    public static HashMap extractLines(File file, String[] keys) throws Exception{
    	HashMap results= new HashMap();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!=null){
				
				for(int i=0; i<keys.length; i++){
				
					if(line.startsWith(keys[i])){
						results.put(keys[i], line);
					}
				}
			}
			return results;
		}
		finally{
			if(reader!=null)try{reader.close();}catch(Exception e){}
		}
    }
}
