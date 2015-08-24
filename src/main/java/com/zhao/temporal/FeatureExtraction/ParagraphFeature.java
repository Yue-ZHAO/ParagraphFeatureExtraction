package com.zhao.temporal.FeatureExtraction;


public class ParagraphFeature {
	
	// TAGS
	// The tag of the paragraph. 
	public int tag;	
	// Days = page time stamp - tagged time stamp
	public int tagRecent;
	public int tagYear;
	
	//	Features
	public int pageTime;
	
	// Feature Part 1: Position and Length
	// A score of the position of the phase in the doc. 
	// Using the position of the start point like 0.05 means 5%.
	public double pos; 
	public int lenAbs; // Absolute length like 27
	// Relative length like 10 means 10% of the whole length of the doc
	public double lenRlt;
	// The char distance between this paragraph and the former paragraph
	public double lenDistFormerPara;
	// The char distance between this paragraph and the after paragraph
	public double lenDistAfterPara;
	
	// Feature Part 2: use NLP with SentenceAnnotation
	// The number of the sentences in this paragraph
	public int numSent;
	// The length of the longest sentence in this paragraph
	public double lenLongSent;
	// The length of the shortest sentence in this paragraph
	public double lenShortSent;
	// The average length of the sentences in this paragraph
	public double lenAvgSent;
	
	// Feature Part 3: use NLP with TimeAnnotation, for temporal expressions
	// The number of temporal expressions in this paragraph
	public int numTEs;
	// The number of temporal expressions before this paragraph
	public int numTEsBefore;		
	public double numOfDate;
	public double numOfDuration;
	public double numOfTime;
	public double numOfSet;
	// The average character distance between temporal expressions in the paragraph
	public double lenDistAvgTEs;
	// The longest character distance between temporal expression and the former one in the paragraph
	public double lenDistLongTEs;
	// For the value of TEs, only count those whose years are in 1900-2100
	// Days = page time stamp - earliest time
	public int valEarliestTE;
	// Days = page time stamp - latest time
	public int valLatestTE;	
	// Days = page time stamp - closest time to the page time
	public int valClosestTE;
	// Days = latest time - earliest
	public int valSpanTE;			
	// The number of temporal expressions whose years are in 1996-2012
	public int numYearsExpTE[] = new int[17];
	
	// Feature Part 4: use NLP with TokensAnnotation
	// The numbers of VB, VBD, VBG, VBN, VBP, and VBZ
	public int numVerbTense[] = new int[6];		
	//	File names
	public String orgFile;
	
	public ParagraphFeature () {
		tag = 0;
		tagRecent = -1;
		tagYear = 0;		
		// Features
		pageTime = 0;
		// Feature Part 1: Position and Length
		pos = 0;				
		lenAbs = 0;				
		lenRlt = 0; 			
		lenDistFormerPara = 0;	
		lenDistAfterPara = 0;	
		// Feature Part 2: use NLP with SentenceAnnotation
		numSent = 0;		
		lenLongSent = 0;	
		lenShortSent = 0;	
		lenAvgSent = 0;			
		// Feature Part 3: use NLP with TimeAnnotation
		numTEs = 0;			
		numTEsBefore = 0;		
		numOfDate = 0;
		numOfDuration = 0;
		numOfTime = 0;
		numOfSet = 0;		
		lenDistAvgTEs = 0;	
		lenDistLongTEs = 0;			
		// Value of temporal expressions
		valEarliestTE = 0;	
		valLatestTE = 0;	
		valClosestTE = 0;	
		valSpanTE = 0;		
		for(int i=0; i<numYearsExpTE.length; i++)
			numYearsExpTE[i] = 0;		
		// Feature Part 4: use NLP with TokensAnnotation
		for(int i=0; i<numVerbTense.length; i++)
			numVerbTense[i] = 0;
		// Filename
		orgFile = "";
	}
	
	public String featuresToARFFwithTag() {
		String featureString = "";		
		featureString = tag + "," 
		    + tagRecent + ","
			+ tagYear + ","  
			// Features
			+ pageTime + ","				  
			// Feature Part 1: Position and Length
			+ String.format("%.6f", pos).toString() + ","				
			+ lenAbs + ","
			+ String.format("%.6f", lenRlt).toString() + "," 				
			+ String.format("%.6f", lenDistFormerPara).toString() + ","	
			+ String.format("%.6f", lenDistAfterPara).toString() + ","							
			// Feature Part 2: use NLP with SentenceAnnotation
			+ numSent + ","											
			+ String.format("%.6f", lenLongSent).toString() + ","		
			+ String.format("%.6f", lenShortSent).toString() + ","	
			+ String.format("%.6f", lenAvgSent).toString() + ","
			// Feature Part 3: use NLP with TimeAnnotation
			+ numTEs + ","
			+ numTEsBefore + ","
			+ String.format("%.6f", numOfDate).toString() + ","
			+ String.format("%.6f", numOfDuration).toString() + ","
			+ String.format("%.6f", numOfTime).toString() + ","
			+ String.format("%.6f", numOfSet).toString() + ","						
			+ String.format("%.6f", lenDistAvgTEs).toString() + ","	
			+ String.format("%.6f", lenDistLongTEs).toString() + ","
			// Value of temporal expressions
			+ valEarliestTE + ","
			+ valLatestTE + ","
			+ valClosestTE + ","
			+ valSpanTE + ",";		
		for(int i=0; i<numYearsExpTE.length; i++)
			featureString = featureString + numYearsExpTE[i] + ",";							
		//	Feature Part 4: use NLP with TokensAnnotation
		for(int i=0; i<numVerbTense.length; i++)
			featureString = featureString + numVerbTense[i] + ",";						
		featureString = featureString + orgFile;
		return featureString;
	}
	
	public String featuresToARFF() {
		String featureString = "";
		
		featureString = pageTime + ","			  
			//	Feature Part 1: Position and Length
			+ String.format("%.6f", pos).toString() + ","				//	A score of the position of the phase in the doc. Using the position of the start point like 5 means 5%.
			+ lenAbs + ","												//	Absolute length like 27
			+ String.format("%.6f", lenRlt).toString() + "," 			//	Relative length like 10 means 10% of the whole length of the doc
			+ String.format("%.6f", lenDistFormerPara).toString() + ","	//	The char distance between this paragraph and the former paragraph
			+ String.format("%.6f", lenDistAfterPara).toString() + ","	//	The char distance between this paragraph and the after paragraph
					
			//	Feature Part 2: use NLP with SentenceAnnotation
			+ numSent + ","											//	The number of the sentences in this paragraph
			+ String.format("%.6f", lenLongSent).toString() + ","	//	The length of the longest sentence in this paragraph
			+ String.format("%.6f", lenShortSent).toString() + ","	//	The length of the shortest sentence in this paragraph
			+ String.format("%.6f", lenAvgSent).toString() + ","	//	The average length of the sentences in this paragraph
					
			//	Feature Part 3: use NLP with TimeAnnotation
			+ numTEs + ","			//	The number of temporal expressions in this paragraph
			+ numTEsBefore + ","	//	The number of temporal expressions before this paragraph
					
			+ String.format("%.6f", numOfDate).toString() + ","
			+ String.format("%.6f", numOfDuration).toString() + ","
			+ String.format("%.6f", numOfTime).toString() + ","
			+ String.format("%.6f", numOfSet).toString() + ","
					
			+ String.format("%.6f", lenDistAvgTEs).toString() + ","		//	The average character distance between temporal expressions in the paragraph
			+ String.format("%.6f", lenDistLongTEs).toString() + ","	//	The longest character distance between temporal expression and the former one in the paragraph
					
			//	Value of temporal expressions
			+ valEarliestTE + ","	//	Days = page time stamp - earliest time
			+ valLatestTE + ","		//	Days = page time stamp - latest time
			+ valClosestTE + ","	//	Days = page time stamp - closest time to the page time
			+ valSpanTE + ",";		//	Days = latest time - earliest						
						
		for(int i=0; i<numYearsExpTE.length; i++)
			featureString = featureString + numYearsExpTE[i] + ",";	
						
		//	Feature Part 4: use NLP with TokensAnnotation
		for(int i=0; i<numVerbTense.length; i++) {
			if (i < numVerbTense.length - 1)
				featureString = featureString + numVerbTense[i] + ",";
			else
				featureString = featureString + numVerbTense[i];
		}
		
		return featureString;
	}
}
