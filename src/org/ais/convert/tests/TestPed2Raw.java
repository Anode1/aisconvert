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
package org.ais.convert.tests;

import java.io.File;
import java.util.HashMap;

import org.ais.convert.*;
import org.ais.convert.hir.CMorgans;

import junit.framework.TestCase;

/**
 * This is a sandbox. Move working test into Tests or another TestSuite after 
 * completion of the work unit 
 */
public class TestPed2Raw extends TestCase{

	
    public TestPed2Raw(String name){
        super(name);
    }

    
    public void test() throws Exception{

    	String INPUT = "data/test_short.ped";
    	String SNIPS = "data/snips_short";
    	String RESULTDIR = "data";
    	
    	Main.main(new String[]{"-p", INPUT/*"data/dummy.txt"*/, "-s", SNIPS, "-d", RESULTDIR, "-t"});
    	
    	//System.out.println(TestUtils.file2String());
    	/*
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
        		"rs3094315	1	742429	AA" + Constants.NL +
        		"rs12184325	1	743968	CC" + Constants.NL +
        		"rs3131969	1	744045	GG" + Constants.NL +
    			"rs12562034	1	758311	AG" + Constants.NL + 
       			"rs2518996	1	782397	GG" + Constants.NL +
    			"rs3934834	1	995669	CC" + Constants.NL +    			
    			"")
    	);*/
    }
    
    
    public void testPerformance() throws Exception{

    	String INPUT = "/ISROOT/dna/ped2raw/VVerenich.ped";
    	String SNIPS = "/ISROOT/dna/ped2raw/VVerenich.map";
    	String RESULTDIR = "data";
    	
    	long t0=System.currentTimeMillis();
    	
    	Main.main(new String[]{"-p", INPUT/*"data/dummy.txt"*/, "-s", SNIPS, "-d", RESULTDIR, "-t"});

     	System.out.println((System.currentTimeMillis()-t0)+" ms");
    }    
    

}
