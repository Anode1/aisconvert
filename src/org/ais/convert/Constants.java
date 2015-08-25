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

/**
 * strings or internal names used in the code: common for processors.
 * Processor-specific constants are in corresponding processors 
 */
public class Constants {

	public static final String releaseVersionString = "ver 1.14";
	
	public static final int MINIMUM_NUMBER_COLUMNS_IN_RAW = 4;		
	public static final String DEFAULT_PED_FILLER = "0";
	public static final String NL = System.getProperty("line.separator"); //for output (as raw) 
	
	//percentage format in UPS calculation 
	public static java.text.DecimalFormat df = new java.text.DecimalFormat("##0.0");
	//similarity percentage format
	public static java.text.DecimalFormat df2 = new java.text.DecimalFormat("##0.00");
	
	public static final String DEFAULT_INPUTS_DIR = "data";
	public static final String DEFAULT_OUTPUT_FILE = "output.txt";
	public static final String DEFAULT_SNIPS_FILE = DEFAULT_INPUTS_DIR + File.separator + 
														"snips.txt";
	public static final String DEFAULT_SM_FILE = DEFAULT_INPUTS_DIR + File.separator + 
														"cM";
	public static final String DEFAULT_OUTPUT_ERROR_FILE = "error_report.txt";

	public static final int DEFAULT_CM = 5;
	public static final int DEFAULT_SNIPS = 500;
	
	//public static final enum BP = {AA,AC,AG,AT,CC,CG,CT,DD,DI,GG,GT,II,TT);
	
	//
	//hash keys used for passing internal parameters from args, configs, gui 
	//to the processor (we are giving meaningful names for easier debugging - 
	//when the program will be big):
	//
	public static final String INPUT_FILE_KEY 		= "input_file";
	public static final String OUTPUT_FILE_KEY		= "output_file";
	public static final String OUTPUT_DIR_KEY		= "output_dir";
	public static final String OUTPUT_ERROR_KEY 	= "error_file";	
	public static final String SNIPS_MAPPING_KEY 	= "snips";
	public static final String HAPMAP_KEY 			= "hapmap";
	public static final String SM_THRESHOLD_KEY     = "cM";
	public static final String MIN_THRESHOLD_KEY    = "min";
	public static final String SHOW_SIMILARITY      = "cg";
	public static final String HOMOZYGOUSITY        = "hz";
	public static final String TREAT_NO_CALLS_AS_HOMOZYGOUS = "nc";

	public static final String UPS_MODE_KEY			= "upsometer";
	public static final String MERGE_MODE_KEY		= "merge";
	public static final String BEHAR_MODE_KEY		= "behar";
	public static final String PED2RAW_MODE_KEY		= "ped2raw";
	public static final String RAW2PED_MODE_KEY		= "raw2ped";
	public static final String PHASE_KEY			= "phase";	
	
	public static final String SIMILARITY_METRICS_1   = "cg1";
	public static final String SIMILARITY_METRICS_2   = "cg2";
	public static final String SIMILARITY_METRICS_3   = "cg3";
}