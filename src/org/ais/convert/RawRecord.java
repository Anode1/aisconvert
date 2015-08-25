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

public class RawRecord {

	public String snip;
	public String chromosome;
	public String position;
	public String base;

	
	/**
	 * The same as comparePositions() but also prints warning - when snips are
	 * different (notice that this occurs only when they are also close to each
	 * other, in one chromosome and are neighbours
	 */
	public int compareLocation(RawRecord record){
		int posResult = comparePosition(record);
		if(posResult==0){
			if(!snip.equals(record.snip)){
				if(Main.warning)System.out.println("WARN: snips are different on the same position. First snip is taken first.");
				return 1;
			}
		}
			
		return posResult;
	}
	
	
	/**
	 * Compare positions of 2 RawRecords. Returns {1,0,-1}. Notice that the position
	 * is not unique in genome and only snip is a candidate (primary) key. So, if
	 * you need the absolute order of snips - you need a master file with such order
	 * convention.  See also compareLocation.
	 */
	public int comparePosition(RawRecord record){
		double pos1 = Double.parseDouble(position);
		double pos2 = Double.parseDouble(record.position);

		return pos1<pos2 ? 1 : (pos1==pos2 ? 0 : -1); //as usually in comparators
	}
	
	
	/**
	 * For debugging purposes only
	 */
	public String toString(){
		StringBuffer sb=new StringBuffer();
		sb.append("snip=" + snip);
		sb.append(", chromosome=" + chromosome);
		sb.append(", position=" + position);
		sb.append(", base="+base);
		return sb.toString();
	}
}
