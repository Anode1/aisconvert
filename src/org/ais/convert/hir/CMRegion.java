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

import org.ais.convert.Main;
import org.ais.convert.Region;

/**
 * Region of the genome used in HIR calculator or Homozygousity calculator 
 */
public class CMRegion extends Region{
	
	public boolean contansOneMismatch=false;
	private float firstCm;
	private float lastCm;
	
	public int indexInNaturalList;
	
	
	public CMRegion(String chromo, int start, String startPos){
		super(start, startPos);
		
		//initialize startCM - if it is not null
		Float cMfound = CMorgans.get(chromo, startPos);
		if(cMfound!=null){
			firstCm = cMfound.floatValue();
		}
		else{
			if(Main.warning)System.err.println("WARN: snip not found in cM file for chromo: " + chromo + " pos: " + startPos);
		}
	}
	

	public void advance(String chromo, int end, String pos){
		super.advance(end);
		
		Float cMfound = CMorgans.get(chromo, pos);
		if(cMfound!=null){
			
			float cMFloat = cMfound.floatValue();
			
			if(firstCm==0) //initialize startCm - if it was null before
				firstCm = cMFloat;
	
			lastCm = cMFloat;
		}
		else{
			if(Main.warning)System.err.println("WARN: snip not found in cM file for chromo: " + chromo + " pos: " + pos);
		}
	}
	
	
	public float cMDistance(){
		return lastCm-firstCm;
	}
	
}
