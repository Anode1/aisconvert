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

import org.ais.convert.converters.HapMap2ped;
import org.ais.convert.converters.Raw2Ped;


/**
 * Wrapper for all other processors. Actually - processor factory.
 * (in future - if number of processors will be big or we will use plugins - 
 * we may put here Class.newInstance(), i.e. creating class by name.
 * For now - while we have a couple of processors - this is not necessary, so
 * let's instantiate them in hard-coded way
 * 
 * Also we should use common Result object - if we need a uniform reports - for 
 * all the processors. Creating of Result (context for the pipeline) object
 * and printing of the report will be also in this class. 
 * We always return boolean here - to the command-line or GUI (if GUI will not
 * need to show log in a panel. Even in that case we will make a getter method here).
 */
public class MainProcessor {
	
	/** pointer to the concrete processor */
	private ProcessorBase concreteInstance;
	Result result;

	
	public Result init() throws Exception{
		
		result = new Result();
		
		if(Parameters.getAsBoolean(Constants.HAPMAP_KEY)){
			concreteInstance = new HapMap2ped();
		}
		else if(Parameters.getAsBoolean(Constants.UPS_MODE_KEY)){
			concreteInstance = new org.ais.convert.hir.UPSometer();
		}
		else if(Parameters.getAsBoolean(Constants.SHOW_SIMILARITY)){ //--cg but without -u
			concreteInstance = new org.ais.convert.similarity.Similarity();
		}	
		else if(Parameters.getAsBoolean(Constants.HOMOZYGOUSITY)){
			concreteInstance = new org.ais.convert.hz.Homozygousity();
		}
		else if(Parameters.getAsBoolean(Constants.MERGE_MODE_KEY)){
			concreteInstance = new org.ais.convert.merge.Merge();
		}
		else if(Parameters.getAsBoolean(Constants.PHASE_KEY)){
			concreteInstance = new org.ais.convert.phase.Phase();
		}		
		else if(Parameters.getAsBoolean(Constants.PED2RAW_MODE_KEY)){
			concreteInstance = new org.ais.convert.converters.Ped2Raw();
		}
		else if(Parameters.getAsBoolean(Constants.RAW2PED_MODE_KEY)){
			concreteInstance = new org.ais.convert.converters.Raw2Ped();
		}		
		else if(Parameters.getAsBoolean(Constants.BEHAR_MODE_KEY)){
			concreteInstance = new org.ais.convert.converters.Behar2Ped();
		}		
		else{ //default for now - 23andme RAW -> PED
			concreteInstance = new Raw2Ped();
		}
		concreteInstance.init(result);
		return result;
	}	
	
	
	public Result process() throws Exception{
		concreteInstance.process(result);
		return result;
	}
	
	
	public void finish() throws Exception{
		concreteInstance.finish(result);
	}
	
	
	/**
	 * Convenience method for unit tests (without separated init(),process(),finish() 
	 */
	public Result processAll() throws Exception{
		Result result = init();
		if(!result.thereAreErrors()){
			process();
			finish();
		}
		return result;
	}
	
}
