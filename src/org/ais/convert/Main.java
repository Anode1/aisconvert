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

public class Main{
	
	public static boolean consoleRun; //to differentiate with a GUI run (used to know - when to log)
	public static ProgressListener progressListener;
	public static boolean trace;
	public static boolean warning = true;


	/**
	 * non-GUI entry point (command-line)
	 */
    public static void main(String args[]) throws Exception{

		try{ 
			long t0=System.currentTimeMillis();
			
			if(args.length<1){
				System.out.println("Pass filename or directory to process - as an argument");
				System.out.println();
				return;
			}
			
			Parameters.getInstance().clear();
			new Config();
			
			consoleRun=true;
			
			if(new ArgsParser().setArgs(args, Parameters.getInstance()) != 0)
				return; //either we have an error in arguments or help asked to be printed
		    
			MainProcessor processor = new MainProcessor();
			if(processor.init().thereAreErrors()){
				System.out.println();
				return;
			}			
			Result result = processor.process();
			processor.finish();
			
		//	if(result.thereAreErrors()){
				/*
				PrintWriter pw = new PrintWriter(System.out);
				result.print(pw);
				pw.close();
				*/
		//		System.out.println();
		//	}
		//	else{
				//System.out.println("Processed in " + (System.currentTimeMillis()-t0) + " ms.");
		//	}
		}
		catch(Throwable e){
			//log.error("", e);
			e.printStackTrace();
		}
 	}
    
}
