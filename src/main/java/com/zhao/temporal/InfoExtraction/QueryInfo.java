package com.zhao.temporal.InfoExtraction;

public class QueryInfo {
	public int queryID = 0;
	public int queryTime1 = 0;
	public int queryTime2 = 0;
	public int queryTime3 = 0;
	public int queryTime4 = 0;
	public int queryTotal = 0;
	
	public String toString() {
		String line = queryID + " " 
	                + queryTime1 + " " 
				    + queryTime2 + " " 
	                + queryTime3 + " " 
				    + queryTime4 + " " 
	                + queryTotal;
		return line;
	}
}
