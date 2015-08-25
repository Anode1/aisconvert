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

import java.util.ArrayList;

import org.ais.convert.Region;

public class HirsContext {


	ArrayList hirs = new ArrayList(); //hirs in natural order
	ArrayList sortedHirs = new ArrayList(); //hirs in sorted order
	//public boolean skipCentimorganThreshold;
	
	CMRegion biggestHir;
	int biggestHirIndex=-1;
	
	public void addHir(CMRegion hir){
		hirs.add(hir);
		
		int hirsIndex = hirs.size()-1;
		hir.indexInNaturalList = hirsIndex;		
		
		if(biggestHir==null){
			biggestHir=hir;
		}
		else{
			if(hir.getCount()>biggestHir.getCount()){
				biggestHir=hir;
			}
		}
		biggestHirIndex=hirsIndex;		
	}
	
}
