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
 * Intended for extending by different kinds of 
 * processors (one-file processor as RAW2PED, two-file processor as this class) with uniform
 * infrastructure supplying arguments, parameters, gui etc - in one way. So, in case we need
 * another processor for genome processing (say, for some kind of conversion or comparison) - 
 * we do not need to reinvent the wheel and we just use one class, similar to this, i.e. 
 * add one file only. Sure, we'll add new arguments/options and optionally - unit tests.
 * If necessary - GUI can be easily reused as well.
 */  
public abstract class ProcessorBase{
	
	/**
	 * Common things - before individual files processing (loading maps).
	 * 
	 * This returns false - if something (a required map or an argument) is missing.
	 * This result is shown in either GUI or reported in the console
	 */
	public abstract void init(Result result) throws Exception;
	
	/**
	 * How to process individual file
	 */
	public abstract void process(Result result) throws Exception;
	
	/**
	 * Finalizing stuff
	 */
	public void finish(Result result) throws Exception{}
}
