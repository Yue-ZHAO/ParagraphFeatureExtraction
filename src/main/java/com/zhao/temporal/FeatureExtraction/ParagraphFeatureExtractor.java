package com.zhao.temporal.FeatureExtraction;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

public class ParagraphFeatureExtractor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static List<ParagraphFeature> extract(TaggedPageReader taggedPageReader, StanfordCoreNLP pipeline) {
		
		if (taggedPageReader.paragraphs.isEmpty())
			return null;
		
		DateTime baseTime = new DateTime("1996-01-01");
		List<ParagraphFeature> paragraphFeatureList = new ArrayList<ParagraphFeature>();
		
		//	Get some information about the whole page.
		int startPosTotal = taggedPageReader.paragraphs.get(0).getStartPoint();	//	The start position of the first paragraph in the page
		int endPasTotal = taggedPageReader.paragraphs.
				get(taggedPageReader.paragraphs.size()-1).getEndPoint();	//	The end position of the last paragraph in the page		
		int lenthTotal = endPasTotal- startPosTotal;
		String pageTimestamp = taggedPageReader.timestamp;
		DateTime pageTime = new DateTime(pageTimestamp);
		
		int numTEsBefore = 0;	//	The number of TEs in the same page before this paragraph
		//	Procedure for each paragraph
		for (int i =0; i<taggedPageReader.paragraphs.size(); i++) {
			
			Paragraph paragraph = taggedPageReader.paragraphs.get(i);			
			ParagraphFeature paragraphFeature = new ParagraphFeature();
			
			
			//	Tag: using the time stamp of the paragraph as the tag.
			DateTime paraTimestamps = new DateTime(paragraph.getTimestamp());
			paragraphFeature.tag = Days.daysBetween(baseTime, paraTimestamps).getDays();
			
			
			//	Features:
			//	For the first paragraph in the page, treat the gap between this paragraph and the former one as 0
			paragraphFeature.pos = paragraph.getStartPoint();
			paragraphFeature.lenAbs = paragraph.getContent().length();
			paragraphFeature.lenRlt = (double)paragraphFeature.lenAbs / lenthTotal;
			if (i == 0) {
				paragraphFeature.lenDistFormerPara = 0;
			} else {
				paragraphFeature.lenDistFormerPara = paragraphFeature.pos - taggedPageReader.paragraphs.get(i-1).getEndPoint();
			}
				
			if (i == taggedPageReader.paragraphs.size()-1) {
				paragraphFeature.lenDistAfterPara = 0;
			} else {
				paragraphFeature.lenDistAfterPara = taggedPageReader.paragraphs.get(i+1).getStartPoint() - paragraph.getEndPoint();			
			}
			
			//	NLP for the paragraph content 
			String text = paragraph.getContent();			
			Annotation document = new Annotation(text);
			document.set(CoreAnnotations.DocDateAnnotation.class, pageTimestamp);			
			pipeline.annotate(document);
			
			//	For features about sentences
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			
			//	Consider that the sentence number is 0			
			paragraphFeature.numSent = sentences.size();
			if (paragraphFeature.numSent == 0) {
				
				paragraphFeature.lenLongSent = 0;
				paragraphFeature.lenShortSent = 0;
				paragraphFeature.lenAvgSent = 0;
				
			} else {
				
				int sentLenTotal = 0;
				int sentLenLong = 0;
				int sentLenShort = 0;
				
				for(CoreMap sentence: sentences) {				
					//	get the content of the sentence
					String sentContent = sentence.toString();
					int sentLength = sentContent.length();
					if (sentLength == 0)
						continue;
					else {
						if (sentLenTotal == 0) {
							sentLenLong = sentLength;
							sentLenShort = sentLength;
						}
						sentLenTotal += sentLength;
						if (sentLength > sentLenLong)
							sentLenLong = sentLength;
						if (sentLength < sentLenShort)
							sentLenShort = sentLength;
					}				
				}
				paragraphFeature.lenLongSent = sentLenLong;
				paragraphFeature.lenShortSent = sentLenShort;
				paragraphFeature.lenAvgSent = sentLenTotal / paragraphFeature.numSent;
				
			}
			
			//	Consider that the number of temporal expressions is 0, use default.
			List<CoreMap> timexAnnsAll = document.get(TimeAnnotations.TimexAnnotations.class);
			paragraphFeature.numTEs = timexAnnsAll.size();
			paragraphFeature.numTEsBefore = numTEsBefore;
			numTEsBefore += paragraphFeature.numTEs;
			
			if (timexAnnsAll.size() != 0) {
			
				int numOfDate = 0;
				int numOfDuration = 0;
				int numOfTime = 0;
				int numOfSet = 0;
				
				DateTime valEarliestTE = null;
				DateTime valLatestTE = null;
				DateTime valClosestTE = null;

				int lenDistLongTEs = 0;
				int formerTimeEndPos = 0;
				int lenDistTotalTEs = paragraphFeature.lenAbs;
				
				for(CoreMap timeExpression : timexAnnsAll) {
					
					//	For the features about TE type
					String typeOfTE = (timeExpression.get(TimeExpression.Annotation.class).getTemporal().getTimexType()).toString();

					if (typeOfTE.equals("DATE")) {
						numOfDate++;
					} else if (typeOfTE.equals("TIME")) {
						numOfTime++;
					} else if (typeOfTE.equals("DURATION")) {
						numOfDuration++;
					} else if (typeOfTE.equals("SET")) {
						numOfSet++;
					}
					
					//	For the features about TE value
					String dateOfTE = timeExpression.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
 					if (dateOfTE != null && dateOfTE.matches("^(\\d+)(.*)")) {
						//	System.out.println("Preprocess: " + dateOfTE);
						//	System.out.println("Range: " + timeExpression.get(TimeExpression.Annotation.class).getTemporal().getRange());
						//	filter some strange format
						//	can be separated by - or start with a number
						//	Pattern pattern = Pattern.compile("^(\\d+)(.*)");
						//	Matcher matcher = pattern.matcher(dateOfTE);
						String[] datePart = dateOfTE.split("-");

						Boolean flagTransformable = false;
						if (datePart.length == 1 && dateOfTE.matches("^[0-9]*$"))
							flagTransformable = true;
						else if (datePart.length == 2) {
							String year = datePart[0];
							String month = datePart[1];
							if (year.matches("^[0-9]*$")) {
								if (month.matches("^[0-9]*$") || (month.startsWith("W") && month.substring(1).matches("^[0-9]*$")))
									flagTransformable = true;
								else if (month.equals("SP")) {
									dateOfTE = year + "-03-20";	// CHUN FEN
									flagTransformable = true;
								} else if (month.equals("SU")) {
									dateOfTE = year + "-06-21"; // XIA ZHI
									flagTransformable = true;
								} else if (month.equals("FA")) {
									dateOfTE = year + "-09-23"; // QIU FEN
									flagTransformable = true;
								} else if (month.equals("WI")) {
									dateOfTE = year + "-12-21"; // DONG ZHI, CHI JIAO ZI ^_^
									flagTransformable = true;
								}
							}
						} else if (datePart.length >= 3) {
							String year = datePart[0];
							String month = datePart[1];
							String day;
							if (datePart[2].length() <= 2)
								day = datePart[2];
							else
								day = datePart[2].substring(0, 2);
							dateOfTE = year + "-" + month + "-" + day;
							if (year.matches("^[0-9]*$") && month.matches("^[0-9]*$") && day.matches("^[0-9]*$"))
								flagTransformable = true;
						}
						
						if (flagTransformable) {
							//	System.out.println("Type: " + typeOfTE);
							//	System.out.println("DURATION: " + timeExpression.get(TimeExpression.Annotation.class).getTemporal().getRange());	//	ERROR FOR 2012-03-06TDT
							//	System.out.println("Processed: " + dateOfTE);
							//	Calculate for datetime features
							DateTime timeValue = new DateTime(dateOfTE);
							if (valEarliestTE == null)
								valEarliestTE = timeValue;
							else {
								if (timeValue.isBefore(valEarliestTE))
									valEarliestTE = timeValue;
							}
							
							if (valLatestTE == null)
								valLatestTE = timeValue;
							else {
								if (timeValue.isAfter(valLatestTE))
									valLatestTE = timeValue;
							}
							
							if (valClosestTE == null)
								valClosestTE = timeValue;
							else {
								int daysGap1 = Days.daysBetween(timeValue, pageTime).getDays();
								int daysGap2 = Days.daysBetween(valClosestTE, pageTime).getDays();
								if (Math.abs(daysGap1) < Math.abs(daysGap2))
									valClosestTE = timeValue;	
							}
						}
					}
					
					//	For the features about TE distances
					List<CoreLabel> tokens = timeExpression.get(CoreAnnotations.TokensAnnotation.class);
					int startPosition = tokens.get(0).beginPosition();
					int endPostion = tokens.get(tokens.size() - 1).endPosition();
					//	If the TE is the first one, then from 0 to the start position is the distance, else distance
					lenDistTotalTEs -= (endPostion - startPosition);
					int tempLenDist = startPosition - formerTimeEndPos;
					
					if (tempLenDist > lenDistLongTEs)
						lenDistLongTEs = tempLenDist;
					
					formerTimeEndPos = endPostion;
				}
				
				paragraphFeature.numOfDate = numOfDate;
				paragraphFeature.numOfDuration = numOfDuration;
				paragraphFeature.numOfTime = numOfTime;
				paragraphFeature.numOfSet = numOfSet;
				
				if (valEarliestTE != null)
					paragraphFeature.valEarliestTE = Days.daysBetween(baseTime, valEarliestTE).getDays();
				if (valLatestTE != null)
					paragraphFeature.valLatestTE = Days.daysBetween(baseTime, valLatestTE).getDays();
				if (valLatestTE != null)
					paragraphFeature.valClosestTE = Days.daysBetween(baseTime, valClosestTE).getDays();
				if (valEarliestTE != null && valLatestTE != null)
					paragraphFeature.valSpanTE = Days.daysBetween(valEarliestTE, valLatestTE).getDays();
				
				paragraphFeature.lenDistAvgTEs = lenDistTotalTEs / (timexAnnsAll.size() + 1);
				if (lenDistLongTEs > (paragraphFeature.lenAbs - formerTimeEndPos))
					paragraphFeature.lenDistLongTEs = lenDistLongTEs;
				else
					paragraphFeature.lenDistLongTEs = paragraphFeature.lenAbs - formerTimeEndPos;
			}
			paragraphFeatureList.add(paragraphFeature);
		}		
		return paragraphFeatureList;
	}

}
