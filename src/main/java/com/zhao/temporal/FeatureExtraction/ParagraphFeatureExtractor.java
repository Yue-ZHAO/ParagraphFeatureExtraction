package com.zhao.temporal.FeatureExtraction;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

public class ParagraphFeatureExtractor {

	public static void main(String[] args) {

	}

	public static List<ParagraphFeature> extract(TaggedPageReader taggedPageReader, StanfordCoreNLP pipeline) {
		
		if (taggedPageReader.paragraphs.isEmpty())
			return null;
		
		DateTime baseTime = new DateTime("1996-01-01");
		List<ParagraphFeature> paragraphFeatureList = new ArrayList<ParagraphFeature>();
		
		//	Get some information about the whole page.
		int startPosTotal = -1;		//	The start position of the first paragraph in the page
		int endPosTotal = -1;		//	The end position of the last paragraph in the page
		for (Paragraph paragraph: taggedPageReader.paragraphs) {
			int startPos = paragraph.getStartPoint();
			int endPos = paragraph.getEndPoint();
			if (startPosTotal == -1 || startPos < startPosTotal)
				startPosTotal = startPos;
			if (endPosTotal == -1 || endPos > endPosTotal)
				endPosTotal = endPos;
		}
		int lenthTotal = endPosTotal- startPosTotal;
		
		String pageTimestamp = taggedPageReader.timestamp;
		DateTime pageTime = new DateTime(pageTimestamp);
		
		int numTEsBefore = 0;	//	The number of TEs in the same page before this paragraph
				
		/* ---------------------------- *	
		 * Procedure for each paragraph *
		 * ---------------------------- */
		for (int i =0; i<taggedPageReader.paragraphs.size(); i++) {
			
			Paragraph paragraph = taggedPageReader.paragraphs.get(i);
			ParagraphFeature paragraphFeature = new ParagraphFeature();
						
			//	Tag: using the time stamp of the paragraph as the tag.
			String stringParagraphTimestamps = paragraph.getTimestamp();
			String[] contentParagraphTimestamps = stringParagraphTimestamps.split("-");
			String yearPara = contentParagraphTimestamps[0];
			String monthPara = contentParagraphTimestamps[1];
			String dayPara = contentParagraphTimestamps[2];
			int monthParaInt = Integer.parseInt(monthPara);
			if(monthParaInt > 12) monthPara = "12";
			if(monthParaInt < 1) monthPara = "01";
			int dayParaInt = Integer.parseInt(dayPara);
			if(dayParaInt > 31) dayPara = "31";
			if(dayParaInt < 1) dayPara = "01";
			stringParagraphTimestamps = yearPara + "-" + monthPara + "-" + dayPara;
			
			DateTime paraTimestamps = new DateTime(stringParagraphTimestamps);
			paragraphFeature.tag = Days.daysBetween(baseTime, paraTimestamps).getDays();
			paragraphFeature.tagYear = paraTimestamps.getYear();
			paragraphFeature.tagRecent = Days.daysBetween(paraTimestamps, pageTime).getDays();
			paragraphFeature.orgFile = taggedPageReader.originalFileName;
			
			//	Features:
			paragraphFeature.pageTime = Days.daysBetween(baseTime, pageTime).getDays();
			//	For the first paragraph in the page, treat the gap between this paragraph and the former one as 0
			paragraphFeature.pos = (double)(paragraph.getStartPoint() - startPosTotal) / lenthTotal;
			paragraphFeature.lenAbs = paragraph.getContent().length();
			paragraphFeature.lenRlt = (double)paragraphFeature.lenAbs / lenthTotal;
			if (i == 0) {
				paragraphFeature.lenDistFormerPara = 0;
			} else {
				paragraphFeature.lenDistFormerPara = (double)(paragraph.getStartPoint() - taggedPageReader.paragraphs.get(i-1).getEndPoint()) / paragraphFeature.lenAbs;
			}
				
			if (i == taggedPageReader.paragraphs.size()-1) {
				paragraphFeature.lenDistAfterPara = 0;
			} else {
				paragraphFeature.lenDistAfterPara = (double)(taggedPageReader.paragraphs.get(i+1).getStartPoint() - paragraph.getEndPoint()) / paragraphFeature.lenAbs;			
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
					if (sentLength > 0) {
						if (sentLenTotal == 0) {
							sentLenLong = sentLength;
							sentLenShort = sentLength;
						} else {
							if (sentLength > sentLenLong)
								sentLenLong = sentLength;
							if (sentLength < sentLenShort)
								sentLenShort = sentLength;
						}
						sentLenTotal += sentLength;
					}				
				}
				paragraphFeature.lenLongSent = (double)sentLenLong / paragraphFeature.lenAbs;
				paragraphFeature.lenShortSent = (double)sentLenShort / paragraphFeature.lenAbs;
				paragraphFeature.lenAvgSent = (double)(sentLenTotal / paragraphFeature.numSent) / paragraphFeature.lenAbs;
				
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

						String[] datePart = dateOfTE.split("-");

						Boolean flagTransformable = false;
						if (datePart.length == 1 && dateOfTE.matches("^[0-9]*$")) {
							flagTransformable = true;
						} else if (datePart.length == 2) {
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

							DateTime timeValue = new DateTime(dateOfTE);
							
							//	To make sure the TE is meaningful, I set a meaningful timespan, from 1900-01-01 to 2100-12-31.
							DateTime timeMinMeaningful = new DateTime("1900-01-01");
							DateTime timeMaxMeaningful = new DateTime("2100-12-31");
							if (timeValue.isAfter(timeMinMeaningful) && timeValue.isBefore(timeMaxMeaningful)) {
								if (valEarliestTE == null || timeValue.isBefore(valEarliestTE))
									valEarliestTE = timeValue;
							
								if (valLatestTE == null || timeValue.isAfter(valLatestTE))
									valLatestTE = timeValue;
							
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
				
				paragraphFeature.numOfDate = (double)numOfDate / paragraphFeature.numTEs;
				paragraphFeature.numOfDuration = (double)numOfDuration / paragraphFeature.numTEs;
				paragraphFeature.numOfTime = (double)numOfTime / paragraphFeature.numTEs;
				paragraphFeature.numOfSet = (double)numOfSet / paragraphFeature.numTEs;
				
				if (valEarliestTE != null)
					paragraphFeature.valEarliestTE = Days.daysBetween(baseTime, valEarliestTE).getDays();
				if (valLatestTE != null)
					paragraphFeature.valLatestTE = Days.daysBetween(baseTime, valLatestTE).getDays();
				if (valLatestTE != null)
					paragraphFeature.valClosestTE = Days.daysBetween(baseTime, valClosestTE).getDays();
				if (valEarliestTE != null && valLatestTE != null)
					paragraphFeature.valSpanTE = Days.daysBetween(valEarliestTE, valLatestTE).getDays();
				
				paragraphFeature.lenDistAvgTEs = (double)(lenDistTotalTEs / (timexAnnsAll.size() + 1)) / paragraphFeature.lenAbs;
				if (lenDistLongTEs > (paragraphFeature.lenAbs - formerTimeEndPos))
					paragraphFeature.lenDistLongTEs = (double)lenDistLongTEs / paragraphFeature.lenAbs;
				else
					paragraphFeature.lenDistLongTEs = (double)(paragraphFeature.lenAbs - formerTimeEndPos) / paragraphFeature.lenAbs;
			}
			
			//	Tokens
        	for (CoreLabel token: document.get(TokensAnnotation.class)) {
        		String pos = token.get(PartOfSpeechAnnotation.class);
        		if (pos.equals("VB"))
        			paragraphFeature.numVerbTense[0]++;
        		else if (pos.equals("VBD"))
        			paragraphFeature.numVerbTense[1]++;
        		else if (pos.equals("VBG"))
        			paragraphFeature.numVerbTense[2]++;
        		else if (pos.equals("VBN"))
        			paragraphFeature.numVerbTense[3]++;
        		else if (pos.equals("VBP"))
        			paragraphFeature.numVerbTense[4]++;
        		else if (pos.equals("VBZ"))
        			paragraphFeature.numVerbTense[5]++;
        	}
			
			//	Use 1111-11-11 as the datetime to find the explicit temporal expressions
			Annotation document_forExp = new Annotation(text);
			document_forExp.set(CoreAnnotations.DocDateAnnotation.class, "1111-11-11");			
			pipeline.annotate(document_forExp);
			List<CoreMap> timexAnnsAllExp = document_forExp.get(TimeAnnotations.TimexAnnotations.class);
			if (timexAnnsAllExp.size() != 0) {
				DateTime valEarliestExpTE = null;
				DateTime valLatestExpTE = null;
				DateTime valClosestExpTE = null;
				
				for(CoreMap timeExpression : timexAnnsAllExp) {
					String dateOfTE = timeExpression.get(TimeExpression.Annotation.class).getTemporal().getTimexValue();
					if (dateOfTE != null && dateOfTE.matches("^(\\d+)(.*)")) {
						String[] datePart = dateOfTE.split("-");
						Boolean flagTransformable = false;
						if (datePart.length == 1 && dateOfTE.matches("^[0-9]*$")) {
							flagTransformable = true;
						} else if (datePart.length == 2) {
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

							DateTime timeValue = new DateTime(dateOfTE);
							
							//	To make sure the TE is meaningful, I set a meaningful timespan, from 1900-01-01 to 2100-12-31.
							DateTime timeMinMeaningful = new DateTime("1900-01-01");
							DateTime timeMaxMeaningful = new DateTime("2100-12-31");
							if (timeValue.isAfter(timeMinMeaningful) && timeValue.isBefore(timeMaxMeaningful)) {
								//	public int numYearsExpTE[] = new int[17];	//	The number of explicit temporal expressions whose years are in 1996-2012
								int year = timeValue.getYear();
								if (year >= 1996 && year <= 2012)
									paragraphFeature.numYearsExpTE[(year-1996)]++;
								
								//	Explicit temporal expressions
								if (valEarliestExpTE == null || timeValue.isBefore(valEarliestExpTE))
									valEarliestExpTE = timeValue;
							
								if (valLatestExpTE == null || timeValue.isAfter(valLatestExpTE))
									valLatestExpTE = timeValue;
							
								if (valClosestExpTE == null)
									valClosestExpTE = timeValue;
								else {
									int daysGap1 = Days.daysBetween(timeValue, pageTime).getDays();
									int daysGap2 = Days.daysBetween(valClosestExpTE, pageTime).getDays();
									if (Math.abs(daysGap1) < Math.abs(daysGap2))
										valClosestExpTE = timeValue;	
								}
							}
						}
					}					
				}
				if (valEarliestExpTE != null)
					paragraphFeature.valEarliestExpTE = Days.daysBetween(baseTime, valEarliestExpTE).getDays();
				if (valLatestExpTE != null)
					paragraphFeature.valLatestExpTE = Days.daysBetween(baseTime, valLatestExpTE).getDays();
				if (valLatestExpTE != null)
					paragraphFeature.valClosestExpTE = Days.daysBetween(baseTime, valClosestExpTE).getDays();
				if (valEarliestExpTE != null && valLatestExpTE != null)
					paragraphFeature.valSpanExpTE = Days.daysBetween(valEarliestExpTE, valLatestExpTE).getDays();				
			}
						
			paragraphFeatureList.add(paragraphFeature);
		}
		/* ---------------------------- *	
		 * End the Procedure for each paragraph *
		 * ---------------------------- */
		return paragraphFeatureList;
	}

}
