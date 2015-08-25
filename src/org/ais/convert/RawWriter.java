package org.ais.convert;

import java.io.PrintWriter;

/**
 * Writes genotype files in 23andme or FTDNA format
 */
public class RawWriter {

	private int format;
	private PrintWriter out;
	private boolean initialized;
	
	
	public RawWriter(PrintWriter out){
		this.out=out;
	}
	
	
	/**
	 * For now - just static. Then - when we'll need more functionality - we'll
	 * do as an object.
	 */
	public void write(RawRecord record) throws Exception{
		
		//header - if necessary. Here we already know the format
		if(!initialized){
			if(format==Format.FTDNA_build27){
				out.print("RSID,CHROMOSOME,POSITION,RESULT");
				out.println();
			}
			else if(format==Format._23andme_build27){
				//nothing for now
			}
			initialized=true;
		}
		
		if(format==Format._23andme_build27){
			out.print(record.snip);
			out.print("\t");
			out.print(record.chromosome);
			out.print("\t");
			out.print(record.position);
			out.print("\t");
			out.print(record.base);
			out.println();
		}
		else if(format==Format.FTDNA_build27){
			out.print("\"");
			out.print(record.snip);
			out.print("\",\"");
			out.print(record.chromosome);
			out.print("\",\"");
			out.print(record.position);
			out.print("\",\"");
			out.print(record.base);
			out.print("\"");
			out.println();
		}
		else throw new Exception("Format is not set!");
	}
	
	/**
	 * Convenience method
	 */
	public void writeCommented(RawRecord record) throws Exception{
		
		out.print("#");
		write(record);
	}	
	
	/**
	 * Convenience method
	 */
	public void writeCommented(RawRecord record, RawRecord record2) throws Exception{
		
		out.print("#");
		if(format==Format._23andme_build27){
			out.print(record.snip);
			out.print("\t");
			out.print(record.chromosome);
			out.print("\t");
			out.print(record.position);
			out.print("\t");
			out.print(record.base);
			out.print("\t");
			out.print(record2.base);
			out.println();
		}
		else if(format==Format.FTDNA_build27){
			out.print("\"");
			out.print(record.snip);
			out.print("\",");
			out.print(record.chromosome);
			out.print("\",");
			out.print(record.position);
			out.print("\",");
			out.print(record.base);
			out.print("\",");
			out.print(record2.base);
			out.print("\"");
			out.println();
		}
		else throw new Exception("Format is not set!");
	}
	
	public void close() throws Exception{
		out.close();
	}
	
	
	public void setFormat(int format){
		this.format=format;
	}
	
}
