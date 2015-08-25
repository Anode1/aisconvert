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

/**
 * Region of the genome used in HIR calculator or Homozygousity calculator 
 */
public class Region {
	
	public String startPos; //we do not parse to numbers for now since do not rely on pos
	public String endPos;
	
	public int start;
	public int end;
	
	public boolean removed;
	
	
	public Region(int start, String startPos){
		this.start=start;
		this.end=start;
		//remember for report:
		this.startPos = startPos;
		endPos = startPos;
	}
	

	public int getCount(){
		return end-start+1; 
	}
	
	
	public void advance(int end){
		this.end=end;
	}
	

	/**
	 * for debugging purposes only
	 */
	public String toString(){
		StringBuffer sb=new StringBuffer();
		sb.append("[start=" + start);
		sb.append(", end=" + end);
		sb.append(", removed=" + removed);
		sb.append(", length="+getCount());
		sb.append("]");
		return sb.toString();
	}
}
