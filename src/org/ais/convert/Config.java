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
import java.io.InputStream;
import java.util.Properties;

/**
 * Config used in GUI. It is left here - just in case - the command line version will
 * use property file similar to mplayer or other program (to have consolidated default user
 * arguments, been passed every run.    
 */
public class Config extends Parameters{

	public static boolean isWindows;
	private static String applicationRoot; 
	
	public Config(){
		this(System.getProperty("user.dir"));
	}	

	
	/**
	 * Constructor to be used from servlet or jsp.
	 * Example:
	 * put in servlet's init:
	 * new Config(config.getServletContext().getRealPath("/") + File.separator + "WEB-INF");
	 */
	public Config(String appRoot){

		applicationRoot=appRoot;
	}
	
	
	public void loadSystemProperties() throws Exception{
		
		//	All the system properties (parameters):
	    Parameters props=Parameters.getInstance();
	    //1st: take all parameters from file:
	    try{
	    	String path2props = getConfigDir() + File.separator + "system.properties";
	    	InputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(path2props));
	    	//java.io.InputStream in=Config.class.getResourceAsStream("/system.properties");
        	props.load(in);
//        	System.out.println(logProps);
	        in.close();
	    }
	    catch(Exception e){
	    	//System.out.println("Cannot load properties:");
	    	//e.printStackTrace();
	      //throw new NullPointerException("Cannot load properties from path:"+pathToProps+":"+e);
	    }
/*
	//LOGS:
        try{
	      String path2log4j = getConfigDir() + File.separator + "log4j.properties";
	      InputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(path2log4j));
          //InputStream in=Config.class.getResourceAsStream("/log4j.properties");
	      Properties logProps = new Properties();	
	      logProps.load(in);
	      in.close();
	   	  PropertyConfigurator.configure(logProps);
        }
	    catch(Exception e){
	      BasicConfigurator.configure();
	      System.err.println("Problem configuring Log4J properties -- basic console configurator used");
	    }
	    //print something into log on startup:
        //if(log.isDebugEnabled())log.debug("applicationRoot used:"+applicationRoot);
*/	    
	    
		String osName=System.getProperty("os.name").toLowerCase();
		if(osName.endsWith("nt") || osName.startsWith("window")){
			isWindows=true;
		}
	}

	
	public static String getConfigDir(){
		return applicationRoot+File.separator+"conf";
	}
	
	
	  /////////////////////////////////////////////////////////
	  // Convenience methods (if we'll have significantly more than 3 - we'll make 
	  // it generic

	public static String getDefaultInputsDir(){

		return applicationRoot + File.separator + Constants.DEFAULT_INPUTS_DIR;
	}
	
	
	public static String getInputsDir(){
		String value = Parameters.getString(Constants.INPUT_FILE_KEY);
		if(value!=null)
			return value;
		
		return Config.getDefaultInputsDir();
	}	

	
	public static String getDefaultOutputFile(){
		
		return applicationRoot + File.separator + Constants.DEFAULT_OUTPUT_FILE;
	}
	
	
	public static String getOutputFile(){
		String value = Parameters.getString(Constants.OUTPUT_FILE_KEY);
		if(value!=null)
			return value;
		
		return Config.getDefaultOutputFile();
	}		
	
		
	public static String getDefaultErrorReportFile(){

		return applicationRoot + File.separator + Constants.DEFAULT_OUTPUT_ERROR_FILE;
	}

	public static String getDefaultSnipsMappingFile(){

		return applicationRoot + File.separator + Constants.DEFAULT_SNIPS_FILE;
	}	

	
}
