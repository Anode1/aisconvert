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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.ais.convert.*;
import org.ais.convert.hir.CMorgans;



import junit.framework.TestCase;


/**
 * List of all unit tests. Please - run this list after any change and fix
 * (failure in this list means that something is broken by the change!)
 * 
 * TODO: write more regression tests! For all the processes and all different 
 * cases !
 */
public class Tests extends TestCase{

    public Tests(String name){
        super(name);
    }

    public void setUp(){
    	new Config();
    	Main.warning=false;
    }
    
    
    public void test23andmeReader() throws Exception{
    	RawReader reader = new RawReader(new BufferedReader(new FileReader("data/1.txt")));
    	RawRecord record=reader.getNextRecord();
    	
    	assertEquals(record.snip, "i1000001");
    	assertEquals(record.chromosome, "7");
    	assertEquals(record.position, "12345678");
    	assertEquals(record.base, "GG");
    	
    	record=reader.getNextRecord();
    	assertEquals(record.snip, "i1000003");
    	assertEquals(record.chromosome, "7");
    	assertEquals(record.position, "12345678");
    	assertEquals(record.base, "AA");
    	
    	record=reader.getNextRecord();
    	assertEquals(record.snip, "i1000011");
    	assertEquals(record.chromosome, "7");
    	assertEquals(record.position, "12345678");
    	assertEquals(record.base, "TA");
    	
    	record=reader.getNextRecord();
    	assertNull(record);
    }    
    
    
    public void testFTDNAReader() throws Exception{
    	RawReader reader = new RawReader(new BufferedReader(new FileReader("data/test_FF.txt")));
    	RawRecord record=reader.getNextRecord();
    	
    	assertEquals(record.snip, "rs3094315");
    	assertEquals(record.chromosome, "1");
    	assertEquals(record.position, "742429");
    	assertEquals(record.base, "AG");
    	
    	record=reader.getNextRecord();
    	assertEquals(record.snip, "rs3131972");
    	assertEquals(record.chromosome, "1");
    	assertEquals(record.position, "742584");
    	assertEquals(record.base, "AG");
    }    

    
    public void testRAW2PED() throws Exception{
    	
    	File output = new File(Constants.DEFAULT_OUTPUT_FILE);
    	output.delete();
    	
		ArrayList filesList = new ArrayList();
		filesList.add("data/1.txt");
		Parameters.getInstance().put(Constants.INPUT_FILE_KEY, filesList);    	
    	
    	MainProcessor processor = new MainProcessor();
		assertFalse(processor.init().thereAreErrors());
		processor.process();
		//System.out.println(TestUtils.file2String(Constants.DEFAULT_OUTPUT_FILE).trim());
		assertTrue("1.txt 1 1 1 1 1 G G  0 0  A A  0 0  0 0  0 0  0 0  0 0  0 0  T A  0 0  0 0  0 0  0 0  0 0".equals(TestUtils.file2String(Constants.DEFAULT_OUTPUT_FILE).trim()));
	
		assertTrue(output.delete());
		
		filesList = new ArrayList();
		filesList.add("data/2.txt");
		Parameters.getInstance().put(Constants.INPUT_FILE_KEY, filesList);   		
		processor.process();
		assertTrue("2.txt 1 1 1 1 1 0 0  A G  T T  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0".equals(TestUtils.file2String(Constants.DEFAULT_OUTPUT_FILE).trim()));		
		
		assertTrue(output.delete());
		
		filesList = new ArrayList();
		filesList.add("data/3.txt");
		Parameters.getInstance().put(Constants.INPUT_FILE_KEY, filesList);   		
		processor.process();
		assertTrue("3.txt 1 1 1 1 1 G A  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0".equals(TestUtils.file2String(Constants.DEFAULT_OUTPUT_FILE).trim()));		
		
		assertTrue(output.delete());
		
		filesList = new ArrayList();
		filesList.add("data/4.txt");
		Parameters.getInstance().put(Constants.INPUT_FILE_KEY, filesList);   		
		processor.process();
		assertTrue("4.txt 1 1 1 1 1 0 0  A G  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0".equals(TestUtils.file2String(Constants.DEFAULT_OUTPUT_FILE).trim()));		
				
		assertTrue(output.delete());
		
		filesList = new ArrayList();
		filesList.add("data/5.txt");
		Parameters.getInstance().put(Constants.INPUT_FILE_KEY, filesList);   		
		processor.process();
		assertTrue("5.txt 1 1 1 1 1 0 0  0 0  T T  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0  0 0".equals(TestUtils.file2String(Constants.DEFAULT_OUTPUT_FILE).trim()));		
				
		output.delete();
		processor.finish();
    }
    
    	
    /* takes long time, so have been commented out */
    public void testReadCM() throws Exception{
    	long t0=System.currentTimeMillis();
    	CMorgans.init();
    	assertTrue(CMorgans.size()==23);
    	HashMap chromoMap = (HashMap)CMorgans.get("X");
    	System.out.println(chromoMap.size());    	
    	assertTrue(chromoMap.size()>13875);
    	System.out.println((System.currentTimeMillis()-t0)+" ms");
    }
    
    
    public void testASort() throws Exception{
    	File file = new File("/");
    	String[] list = file.list();
		ArrayList arrayList = new ArrayList(list.length);
		for(int l=0; l<list.length; l++){
			arrayList.add(list[l]);
		}
		java.util.Collections.sort(arrayList);
		
    	//System.out.println(arrayList);
    	for(int i=0; i<list.length; i++){
    		//System.out.println(list[i]);
    	}
    }
    
    
    public void testUpsCalculator() throws Exception{
    	
    	String TEMPFILE1 = "data/6.txt";
    	String TEMPFILE2 = "data/7.txt";

    	Parameters params = Parameters.getInstance();
    	params.clear();
    	ArgsParser.setParameters(new String[]{"-u", TEMPFILE1, TEMPFILE2}, params);
    	
    	MainProcessor processor = new MainProcessor();
    	Result result = processor.processAll();

    	
    	//TODO implement me!!!
    }
    
    
    public void testMerge() throws Exception{
    	
    	String TEMPFILE1 = "data/merge/1.temp";
    	String TEMPFILE2 = "data/merge/2.temp";
    	String RESULTFILE = "data/merge/1_Result.txt";
    	
    	//test some general case on one chromosome:
    	TestUtils.string2File(TEMPFILE1,
    			"i1000001 7 1 GG" + Constants.NL +
    			"i1000003 7 3 AA" + Constants.NL +
    			"i1000011 7 4 TA" + Constants.NL +
    			"i1000005 7 5 AA");
    	TestUtils.string2File(TEMPFILE2,
    			"i1000002 7 2 AG" + Constants.NL +
    			"i1000003 7 3 TT" + Constants.NL +
    			"i1000005 7 5 AA");
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"i1000001	7	1	GG" + Constants.NL +
    			"i1000002	7	2	AG" + Constants.NL +
    			"#i1000003	7	3	AA	TT" + Constants.NL +
    			"i1000011	7	4	TA" + Constants.NL +
    			"i1000005	7	5	AA" + Constants.NL
    		)
    	);


    	TestUtils.string2File(TEMPFILE1,
				"4 7 5 AA");   
    	TestUtils.string2File(TEMPFILE2,
    			"1 7 1 GG" + Constants.NL +
    			"4 7 5 AA");   
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"1	7	1	GG" + Constants.NL +
    			"4	7	5	AA" + Constants.NL
    		)
    	);
    	
		//left file is empty
    	TestUtils.string2File(TEMPFILE1, "");
    	TestUtils.string2File(TEMPFILE2,
    			"1 7 1 GG" + Constants.NL +
    			"4 7 5 AA");
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"1	7	1	GG" + Constants.NL +
    			"4	7	5	AA" + Constants.NL
    		)
    	);

    	//right file is empty
    	TestUtils.string2File(TEMPFILE1,
    			"1 7 1 GG" + Constants.NL +
    			"4 7 5 AA");  
    	TestUtils.string2File(TEMPFILE2, ""); 	
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"1	7	1	GG" + Constants.NL +
    			"4	7	5	AA" + Constants.NL
    		)
    	);

    	
    	TestUtils.string2File(TEMPFILE1,
    			"1 7 1 GG" + Constants.NL +
    			"4 7 5 AA");   
    	TestUtils.string2File(TEMPFILE2,
    			"4 7 5 AA");    
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"1	7	1	GG" + Constants.NL +
    			"4	7	5	AA" + Constants.NL
    		)
    	);
    	
    	
    	TestUtils.string2File(TEMPFILE1,
    			"1 7 1 GG" + Constants.NL+
    			"4 7 5 AT");   
    	TestUtils.string2File(TEMPFILE2,
    			"4 7 5 AA");    
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
    			"1	7	1	GG" + Constants.NL +
    			"#4	7	5	AT	AA" + Constants.NL +
    			""
    		)
    	);
   	
    	
    	//multiple chromosomes:
    	TestUtils.string2File(TEMPFILE1,
    			"i1000001 1 1 GG" + Constants.NL +
    			"i1000003 2 3 AA");
    	TestUtils.string2File(TEMPFILE2,
    			"i1000001 1 1 GG" + Constants.NL +
    			"i1000002 2 2 AG");
    	Main.main(new String[]{"-M", TEMPFILE1, TEMPFILE2, "-o", RESULTFILE, "-W"});
    	//System.out.println(TestUtils.file2String(RESULTFILE));
    	assertTrue(
    		TestUtils.file2String(RESULTFILE).equals(
   				"i1000001	1	1	GG" + Constants.NL +
    			"i1000002	2	2	AG" + Constants.NL +
    			"i1000003	2	3	AA" + Constants.NL    			
    		)
    	);
    	
    }  

}
