package com.zhao.temporal.FeatureExtraction;


public class ParagraphFeature {
	public int tag = 0;	//	The tag of the paragraph. Days = page time stamp - tagged time stamp
	public int tagRecent = -1;
	public int tagYear = 0;
	
	//	Features
	public int pageTime = 0;
	
	//	Feature Part 1: easy to extract
	public int pos = 0;	//	A score of the position of the phase in the doc. Using the position of the start point like 5 means 5%.
	public int lenAbs = 0;	//	Absolute length like 27
	public double lenRlt = 0; 	//	Relative length like 10 means 10% of the whole length of the doc
	public int lenDistFormerPara = 0;	//	The char distance between this paragraph and the former paragraph
	public int lenDistAfterPara = 0;	//	The char distance between this paragraph and the after paragraph
	
	//	Feature Part 2: use NLP with sentenceAnnotator
	public int numSent = 0;	//	The number of the sentences in this paragraph
	public int lenLongSent = 0;	//	The length of the longest sentence in this paragraph
	public int lenShortSent = 0;	//	The length of the shortest sentence in this paragraph
	public int lenAvgSent = 0;	//	The average length of the sentences in this paragraph
	
	//	Feature Part 3: use NLP with TimeAnnotation and tokensAnnotation
	public int numTEs = 0;	//	The number of temporal expressions in this paragraph
	public int numTEsBefore = 0;	//	The number of temporal expressions before this paragraph
		//	public int numTEsAfter = 0;	//	The number of temporal expressions after this paragraph
	public int numOfDate = 0;
	public int numOfDuration = 0;
	public int numOfTime = 0;
	public int numOfSet = 0;
	
		//	public int numExpTEs = 0;	//	The number of explicit temporal expressions in this paragraph
		//	public int numExpTEsBefore = 0;	//	The number of explicit temporal expressions before this paragraph
		//	public int numExpTEsAfter = 0;	//	The number of explicit temporal expressions after this paragraph
	public int valEarliestTE = 0;	//	Days = page time stamp - earliest time
	public int valLatestTE = 0;	//	Days = page time stamp - latest time
	public int valClosestTE = 0;	//	Days = page time stamp - closest time to the page time
	public int valSpanTE = 0;	//	Days = latest time - earliest
		//	public int valEarliestExpTE = 0;	//	Days = page time stamp - earliest explicit time
		//	public int valLatestExpTE = 0;	//	Days = page time stamp - latest explicit time
		//	public int valClosestExpTE = 0;	//	Days = age time stamp - closest explicit time to the page time
		//	public int valSpanExpTE = 0;	//	Days = latest explicit time - earliest explicit time
	
	public int lenDistAvgTEs = 0;	//	The average character distance between temporal expressions in the paragraph
	public int lenDistLongTEs = 0;	//	The longest character distance between temporal expression and the former one in the paragraph
	//	TODO it is an important feature.	
	//	public int lenDistShortTEs = 0;	//	The shortest character distance between temporal expressions in the paragraph
	public String orgFile = "";
	
	//	TODO need to change the DateTime features to string
	public String featuresToString() {
		String featureString = "";
		
		featureString = pos + " "
					  + lenAbs + " "
					  + lenRlt + " "
					  + lenDistFormerPara + " "
					  + lenDistAfterPara + " "
					  
					  + numSent + " "
					  + lenLongSent + " "
					  + lenShortSent + " "
					  + lenAvgSent + " "
					  
					  + numTEs + " "
					  + numTEsBefore + " "
					  
					  + numOfDate + " "
					  + numOfDuration + " "
					  + numOfTime + " "
					  + numOfSet + " "
					  // + numTEsAfter + " "
					  // + numExpTEs + " "
					  // + numExpTEsBefore + " "
					  // + numExpTEsAfter + " "
					  + valEarliestTE + " "
					  + valLatestTE + " "
					  + valClosestTE + " "
					  + valSpanTE + " "
					  // + valEarliestExpTE + " "
					  // + valLatestExpTE + " "
					  // + valClosestExpTE + " "
					  // + valSpanExpTE + " "
					  + lenDistAvgTEs + " "
					  + lenDistLongTEs;
					  // + lenDistShortTEs;
		
		return featureString;
	}
	
	//	TODO need to change the DateTime features to string
	public String featuresToStringWithTag() {
		String featureString = "";
		
		featureString = tag + ","
					  
					  + tagRecent + ","
					  + tagYear + ","
					  
				      + pageTime + ","
				      + pos + ","
					  + lenAbs + ","
					  + lenRlt + ","
					  + lenDistFormerPara + ","
					  + lenDistAfterPara + ","
					  
					  + numSent + ","
					  + lenLongSent + ","
					  + lenShortSent + ","
					  + lenAvgSent + ","
					  
					  + numTEs + ","
					  + numTEsBefore + ","
					  
					  + numOfDate + ","
					  + numOfDuration + ","
					  + numOfTime + ","
					  + numOfSet + ","
					  // + numTEsAfter + " "
					  // + numExpTEs + " "
					  // + numExpTEsBefore + " "
					  // + numExpTEsAfter + " "
					  + valEarliestTE + ","
					  + valLatestTE + ","
					  + valClosestTE + ","
					  + valSpanTE + ","
					  // + valEarliestExpTE + " "
					  // + valLatestExpTE + " "
					  // + valClosestExpTE + " "
					  // + valSpanExpTE + " "
					  + lenDistAvgTEs + ","
					  + lenDistLongTEs + ","
					  + orgFile;
					  // + lenDistShortTEs;
		
		return featureString;
	}
	
	public String featuresToARFF() {
		String featureString = "";
		
		featureString = tag + ","
					  
					  + tagRecent + ","
					  + tagYear + ","
					  
				      + pageTime + ","
				      + pos + ","
					  + lenAbs + ","
					  + lenRlt + ","
					  + lenDistFormerPara + ","
					  + lenDistAfterPara + ","
					  
					  + numSent + ","
					  + lenLongSent + ","
					  + lenShortSent + ","
					  + lenAvgSent + ","
					  
					  + numTEs + ","
					  + numTEsBefore + ","
					  
					  + numOfDate + ","
					  + numOfDuration + ","
					  + numOfTime + ","
					  + numOfSet + ","
					  // + numTEsAfter + " "
					  // + numExpTEs + " "
					  // + numExpTEsBefore + " "
					  // + numExpTEsAfter + " "
					  + valEarliestTE + ","
					  + valLatestTE + ","
					  + valClosestTE + ","
					  + valSpanTE + ","
					  // + valEarliestExpTE + " "
					  // + valLatestExpTE + " "
					  // + valClosestExpTE + " "
					  // + valSpanExpTE + " "
					  + lenDistAvgTEs + ","
					  + lenDistLongTEs+ ","
					  + orgFile;
					  // + lenDistShortTEs;
		
		return featureString;
	}
}
