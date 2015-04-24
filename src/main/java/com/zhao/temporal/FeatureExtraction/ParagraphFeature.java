package com.zhao.temporal.FeatureExtraction;


public class ParagraphFeature {
	
	//	Tags
	public int tag;	//	The tag of the paragraph. Days = page time stamp - tagged time stamp
	public int tagRecent;
	public int tagYear;
	
	//	Features
	public int pageTime;
	
	//	Feature Part 1: Position and Length
	public double pos;					//	A score of the position of the phase in the doc. Using the position of the start point like 5 means 5%.
	public int lenAbs;					//	Absolute length like 27
	public double lenRlt; 				//	Relative length like 10 means 10% of the whole length of the doc
	public double lenDistFormerPara;	//	The char distance between this paragraph and the former paragraph
	public double lenDistAfterPara;		//	The char distance between this paragraph and the after paragraph
	
	//	Feature Part 2: use NLP with SentenceAnnotation
	public int numSent;				//	The number of the sentences in this paragraph
	public double lenLongSent;		//	The length of the longest sentence in this paragraph
	public double lenShortSent;		//	The length of the shortest sentence in this paragraph
	public double lenAvgSent;		//	The average length of the sentences in this paragraph
	
	//	Feature Part 3: use NLP with TimeAnnotation, for temporal expressions
	public int numTEs;			//	The number of temporal expressions in this paragraph
	public int numTEsBefore;	//	The number of temporal expressions before this paragraph
	
	public double numOfDate;
	public double numOfDuration;
	public double numOfTime;
	public double numOfSet;
	
	public double lenDistAvgTEs;	//	The average character distance between temporal expressions in the paragraph
	public double lenDistLongTEs;	//	The longest character distance between temporal expression and the former one in the paragraph
	
	public int numYearsExpTE[] = new int[17];	//	The number of explicit temporal expressions whose years are in 1996-2012
	
	//	For the value of TEs, only count those whose years are in 1900-2100
	public int valEarliestTE;	//	Days = page time stamp - earliest time
	public int valLatestTE;		//	Days = page time stamp - latest time
	public int valClosestTE;	//	Days = page time stamp - closest time to the page time
	public int valSpanTE;		//	Days = latest time - earliest
	
	public int valEarliestExpTE;	//	Days = page time stamp - earliest explicit time
	public int valLatestExpTE;		//	Days = page time stamp - latest explicit time
	public int valClosestExpTE;		//	Days = age time stamp - closest explicit time to the page time
	public int valSpanExpTE;		//	Days = latest explicit time - earliest explicit time
		
	//	Feature Part 4: use NLP with TokensAnnotation
	public int numVerbTense[] = new int[6];	//	The numbers of VB, VBD, VBG, VBN, VBP, and VBZ
	
	//	File names
	public String orgFile;
	
	
	public ParagraphFeature () {
		tag = 0;			//	The tag of the paragraph. Days = page time stamp - tagged time stamp
		tagRecent = -1;
		tagYear = 0;
		
		//	Features
		pageTime = 0;
		
		//	Feature Part 1: Position and Length
		pos = 0;				//	A score of the position of the phase in the doc. Using the position of the start point like 5 means 5%.
		lenAbs = 0;				//	Absolute length like 27
		lenRlt = 0; 			//	Relative length like 10 means 10% of the whole length of the doc
		lenDistFormerPara = 0;	//	The char distance between this paragraph and the former paragraph
		lenDistAfterPara = 0;	//	The char distance between this paragraph and the after paragraph
		
		//	Feature Part 2: use NLP with SentenceAnnotation
		numSent = 0;		//	The number of the sentences in this paragraph
		lenLongSent = 0;	//	The length of the longest sentence in this paragraph
		lenShortSent = 0;	//	The length of the shortest sentence in this paragraph
		lenAvgSent = 0;		//	The average length of the sentences in this paragraph
		
		//	Feature Part 3: use NLP with TimeAnnotation
		numTEs = 0;			//	The number of temporal expressions in this paragraph
		numTEsBefore = 0;	//	The number of temporal expressions before this paragraph
		
		numOfDate = 0;
		numOfDuration = 0;
		numOfTime = 0;
		numOfSet = 0;
		
		lenDistAvgTEs = 0;	//	The average character distance between temporal expressions in the paragraph
		lenDistLongTEs = 0;	//	The longest character distance between temporal expression and the former one in the paragraph
		
		//	Value of temporal expressions
		valEarliestTE = 0;	//	Days = page time stamp - earliest time
		valLatestTE = 0;	//	Days = page time stamp - latest time
		valClosestTE = 0;	//	Days = page time stamp - closest time to the page time
		valSpanTE = 0;		//	Days = latest time - earliest
		
		valEarliestExpTE = 0;	//	Days = page time stamp - earliest explicit time
		valLatestExpTE = 0;		//	Days = page time stamp - latest explicit time
		valClosestExpTE = 0;	//	Days = age time stamp - closest explicit time to the page time
		valSpanExpTE = 0;		//	Days = latest explicit time - earliest explicit time
		for(int i=0; i<numYearsExpTE.length; i++)
			numYearsExpTE[i] = 0;
		
		//	Feature Part 4: use NLP with TokensAnnotation
		for(int i=0; i<numVerbTense.length; i++)
			numVerbTense[i] = 0;
		
		orgFile = "";
	}
	
	public String featuresToARFFwithTag() {
		String featureString = "";
		
		featureString = tag + ","					  
					  + tagRecent + ","
					  + tagYear + ","
					  
					  //	Features
					  + pageTime + ","
					  
					  //	Feature Part 1: Position and Length
					  + String.format("%.6f", pos).toString() + ","					//	A score of the position of the phase in the doc. Using the position of the start point like 5 means 5%.
					  + lenAbs + ","												//	Absolute length like 27
					  +	String.format("%.6f", lenRlt).toString() + "," 				//	Relative length like 10 means 10% of the whole length of the doc
					  +	String.format("%.6f", lenDistFormerPara).toString() + ","	//	The char distance between this paragraph and the former paragraph
					  +	String.format("%.6f", lenDistAfterPara).toString() + ","	//	The char distance between this paragraph and the after paragraph
						
					  //	Feature Part 2: use NLP with SentenceAnnotation
					  + numSent + ","											//	The number of the sentences in this paragraph
					  + String.format("%.6f", lenLongSent).toString() + ","		//	The length of the longest sentence in this paragraph
					  +	String.format("%.6f", lenShortSent).toString() + ","	//	The length of the shortest sentence in this paragraph
					  +	String.format("%.6f", lenAvgSent).toString() + ","		//	The average length of the sentences in this paragraph
						
					  //	Feature Part 3: use NLP with TimeAnnotation
					  +	numTEs + ","		//	The number of temporal expressions in this paragraph
					  +	numTEsBefore + ","	//	The number of temporal expressions before this paragraph
						
					  +	String.format("%.6f", numOfDate).toString() + ","
					  +	String.format("%.6f", numOfDuration).toString() + ","
					  +	String.format("%.6f", numOfTime).toString() + ","
					  +	String.format("%.6f", numOfSet).toString() + ","
						
					  + String.format("%.6f", lenDistAvgTEs).toString() + ","		//	The average character distance between temporal expressions in the paragraph
					  +	String.format("%.6f", lenDistLongTEs).toString() + ","		//	The longest character distance between temporal expression and the former one in the paragraph
						
					  //	Value of temporal expressions
					  +	valEarliestTE + ","	//	Days = page time stamp - earliest time
					  +	valLatestTE + ","	//	Days = page time stamp - latest time
					  +	valClosestTE + ","	//	Days = page time stamp - closest time to the page time
					  +	valSpanTE + ","		//	Days = latest time - earliest
						
					  +	valEarliestExpTE + ","	//	Days = page time stamp - earliest explicit time
					  +	valLatestExpTE + ","	//	Days = page time stamp - latest explicit time
					  +	valClosestExpTE + ","	//	Days = age time stamp - closest explicit time to the page time
					  +	valSpanExpTE + ",";		//	Days = latest explicit time - earliest explicit time
						
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
				  		+ valSpanTE + ","		//	Days = latest time - earliest
						
				  		+ valEarliestExpTE + ","	//	Days = page time stamp - earliest explicit time
				  		+ valLatestExpTE + ","		//	Days = page time stamp - latest explicit time
				  		+ valClosestExpTE + ","		//	Days = age time stamp - closest explicit time to the page time
				  		+ valSpanExpTE + ",";		//	Days = latest explicit time - earliest explicit time
						
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
