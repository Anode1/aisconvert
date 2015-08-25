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

import java.io.BufferedReader;
import java.io.FileReader;

import org.ais.convert.*;
import org.ais.convert.phase.Phase;

import junit.framework.TestCase;

/**
 * This is a sandbox. Move working test into Tests or another TestSuite after 
 * completion of the work unit 
 */
public class TestPhase extends TestCase{

	
    public TestPhase(String name){
        super(name);
    }
    
    public void setUp(){
    	new Config();
    }    

    
    public void testPhase() throws Exception{
        /*
        The following combinations allowed but we are not validating here, having routine generic 		
    	 AG,CT,AC,GT,CG,AT 
    	 Below are AT pair combinations only:
    	 (AA,AA)=>AA
    	 (AA,AT)=>TT  		 (AA,TA)=>TT,
    	 (AA,TT)=>TT
    	 (AT,AA)=>AA		 (TA,AA)=>AA
    	 (AT,AT)=>AT		 (AT,TA)=>AT		 (TA,AT)=>AT		 (TA,TA)=>AT
    	 (AT,TT)=>TT	     (TA,TT)=>TT	
    	 */

    	//error cases and special cases
    	assertEquals(Phase.getPhased("", ""), "--");
    	assertEquals(Phase.getPhased("", "AA"), "--");
    	assertEquals(Phase.getPhased("BBB", "AA"), "--");
    	assertEquals(Phase.getPhased("BB", "--"), "--");
    	assertEquals(Phase.getPhased("--", "CG"), "CG");
    	
    	assertEquals(Phase.getPhased("AA", "AA"), "AA");
    	assertEquals(Phase.getPhased("AA", "AT"), "TT");
    	assertEquals(Phase.getPhased("AA", "TA"), "TT");
    	assertEquals(Phase.getPhased("AA", "TT"), "TT");
    	assertEquals(Phase.getPhased("AT", "AA"), "AA");
    	assertEquals(Phase.getPhased("TA", "AA"), "AA");
    	assertEquals(Phase.getPhased("AT", "AT"), "AT");
    	assertEquals(Phase.getPhased("AT", "TA"), "TA");
    	assertEquals(Phase.getPhased("TA", "AT"), "AT");
    	assertEquals(Phase.getPhased("TA", "TA"), "TA");
    	assertEquals(Phase.getPhased("AT", "TT"), "TT");
    	assertEquals(Phase.getPhased("TA", "TT"), "TT");
    	//other letters are not different (the same code works, abstracting from the particular letter)  
    	assertEquals(Phase.getPhased("CG", "CC"), "CC");
    	//even not allowed ones
    	assertEquals(Phase.getPhased("AB", "BB"), "BB");
    }
    
    
    public void testSimple23andmeFiles() throws Exception{
    	
    	//Main.trace=true;
    	
    	String TEMPFILE1 = "data/.test1.temp";
    	String TEMPFILE2 = "data/.test2.temp";
    	String RESULTFILE = "data/.test1_Result.txt";

    	TestUtils.string2File(TEMPFILE1,
    			"i1000005 7 5 AA");
    	TestUtils.string2File(TEMPFILE2,
    			"i1000005 7 5 AT");
    	Main.main(new String[]{"--phase", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"i1000005	7	5	TT" + Constants.NL
    		)
    	);  
    }
    
    
    public void test23andme() throws Exception{    
    	
    	String TEMPFILE1 = "data/.test1.temp";
    	String TEMPFILE2 = "data/.test2.temp";
    	String RESULTFILE = "data/.test1_Result.txt";    	
    	
    	TestUtils.string2File(TEMPFILE1,
    			"i1000001 7 1 RR" + Constants.NL +
    			"i1000003 7 3 AA" + Constants.NL +
    			"i1000013 7 4 AT" + Constants.NL +
    			"i1000014 7 4 TA" + Constants.NL +
    			"i1000015 7 4 CG" + Constants.NL +
    			"i1000016 7 4 GG");
    	TestUtils.string2File(TEMPFILE2,
    			"i1000002 7 2 QQ" + Constants.NL +
    			"i1000003 7 3 AA" + Constants.NL +
    			"i1000013 7 4 TT" + Constants.NL +
    			"i1000014 7 4 TT" + Constants.NL +
    			"i1000015 7 4 CC" + Constants.NL +
    			"i1000016 7 4 AT" + Constants.NL +
    			"i1000017 7 5 WW");
    	Main.main(new String[]{"--phase", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE});
    	
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
        			"i1000003	7	3	AA" + Constants.NL +
        			"i1000013	7	4	TT" + Constants.NL +
        			"i1000014	7	4	TT" + Constants.NL +
        			"i1000015	7	4	CC" + Constants.NL +
        			"i1000016	7	4	--" + Constants.NL
    		)
    	);
    }
    
    
    public void testSimpleFTDNAFiles() throws Exception{
    	
    	//Main.trace=true;
    	
    	String TEMPFILE1 = "data/.test1.temp";
    	String TEMPFILE2 = "data/.test2.temp";
    	String RESULTFILE = "data/.test1_Result.txt";

    	TestUtils.string2File(TEMPFILE1,
    			"i1000005 7 5 AA");
    	TestUtils.string2File(TEMPFILE2,
    			"i1000005 7 5 AT");
    	Main.main(new String[]{"--phase", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"i1000005	7	5	TT" + Constants.NL
    		)
    	);  
    }
   

    public void testFTDNAFiles() throws Exception{
    	
    	String RESULTFILE = "data/.test1_phased.txt";
    	Main.main(new String[]{"--phase", "data/test_FF.txt", "data/test_FF2.txt", "-o", RESULTFILE});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertEquals(TestUtils.file2String(RESULTFILE),
    			"RSID,CHROMOSOME,POSITION,RESULT" + Constants.NL +
    			"\"rs3094315\",\"1\",\"742429\",\"AG\"" + Constants.NL +  
    			"\"rs3131972\",\"1\",\"742584\",\"GG\"" + Constants.NL +
    			"\"rs12562034\",\"1\",\"758311\",\"AA\"" + Constants.NL +
    			"\"rs12124819\",\"1\",\"766409\",\"AA\"" + Constants.NL +
    			"\"rs11240777\",\"1\",\"788822\",\"GA\"" + Constants.NL +
    			"\"rs6681049\",\"1\",\"789870\",\"TT\"" + Constants.NL +
    			"\"rs4970383\",\"1\",\"828418\",\"AA\"" + Constants.NL
    	);  
    }
    
    
    public void testReal() throws Exception{
    	Main.main(new String[]{"--phase", "/ISROOT/dna/data/FamilyFinder/N100415-autosomal-o-results.csv", 
    			"/ISROOT/dna/data/FamilyFinder/N76300-autosomal-results.csv"});
    }
    
 

}
