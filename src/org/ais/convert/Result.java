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

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;


/**
 * We do not collect errors for scalability. We dump them to stdout currently
 * and can output into the log file. This class is for future use (single
 * context passed through the whole processing pipeline)
 */
public class Result {

	//private ArrayList list = new ArrayList();
	private boolean thereAreCriticalErrors;
	
    
    public Result(){
    }
    
    /*
	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}
	*/

	public void addError(){
		thereAreCriticalErrors=true;
	}
	
	
	public boolean thereAreErrors(){
		return thereAreCriticalErrors;
	}
	

	/**
	 * For debugging purposes only
	 */
	/*
	public String toString(){
		StringWriter writer = new StringWriter();
		try{
			print(writer);
			writer.close();
		}
		catch(Exception e){
			System.out.println("Cannot write to string");
		}
		return writer.toString();
	}
	*/
}
