package org.ais.convert.similarity;

import org.ais.convert.Constants;
import org.ais.convert.Parameters;

public class SimilarityContext{
	
	private boolean calculateScore1;
	private boolean calculateScore2;
	private boolean calculateScore3;
	
	private double similarityScore1;
	private double similarityScore2;
	private double similarityScore3;
	
	long totalNotNoCalls;
	public boolean skipXYorMT;
	
	
	public SimilarityContext(){
		
		calculateScore1=Parameters.getAsBoolean(Constants.SIMILARITY_METRICS_1);
		calculateScore2=Parameters.getAsBoolean(Constants.SIMILARITY_METRICS_2);
		calculateScore3=Parameters.getAsBoolean(Constants.SIMILARITY_METRICS_3);	
	}
	
	
	public void fullMatch(String base){

		if(calculateScore1)
			similarityScore1+=1;
		
		if(calculateScore2){ //Kull's calculating method
			if(base.charAt(0)==base.charAt(1)) //homozygous
				similarityScore2+=1;
			else
				similarityScore2+=0.5;
		}

		if(calculateScore3){ //Kull's calculating method with centiMorgans
			
			if(base.charAt(0)==base.charAt(1)) //homozygous
				similarityScore3+=1;
			else
				similarityScore3+=0.5;
		}
	}

	
	public void halfMatch(){
		
		if(calculateScore1)
			similarityScore1+=0.5;
		
		if(calculateScore2)
			similarityScore2+=0.5;

		if(calculateScore3){
			similarityScore3+=0.5/**centiMorgans*/;
		}
		
	}
	
	
	/**
	 * Prints common report for all chromosomes 
	 */
	public void finalReport(){
		
		StringBuffer sb = new StringBuffer("Similarity:");
		
		if(calculateScore1){
			double similarity;
			if(totalNotNoCalls!=0)
				similarity=100*similarityScore1/totalNotNoCalls;
			else
				similarity=0;
			
			sb.append(" ");
			sb.append(Constants.df2.format(similarity) + "%");
		}
		
		if(calculateScore2){
			double similarity;
			if(totalNotNoCalls!=0)
				similarity=100*similarityScore2/totalNotNoCalls;
			else
				similarity=0;
			
			sb.append(" ");
			sb.append(Constants.df2.format(similarity) + "%");
		}
		
		if(calculateScore3){
			double similarity;
			if(totalNotNoCalls!=0)
				similarity=100*similarityScore3/totalNotNoCalls;
			else
				similarity=0;
			
			sb.append(" ");
			sb.append(Constants.df2.format(similarity) + "%");
		}
		
		System.out.println(sb);
	}

	

}
