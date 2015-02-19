package com.zhao.temporal.FeatureExtraction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.zhao.temporal.Utils.FileProcess;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Hello world!
 *
 */
public class App 
{

	public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello World!" );
        
        //	read all the tagged page file in the folder
        String srcFolderPath = args[0];
        File srcFolder = new File(srcFolderPath);
        
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, sutime");        
        props.put("customAnnotatorClass.sutime", "edu.stanford.nlp.time.TimeAnnotator");
        props.put("sutime.rules", "sutimeRules/defs.sutime.txt, sutimeRules/english.sutime.txt");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        //	Write the header of the ARFF format
        File outFile = new File(args[1]);
        FileProcess.addLinetoaFile("@RELATION \"paragraph timestamps\"", outFile.getAbsolutePath());
        
        FileProcess.addLinetoaFile("@ATTRIBUTE tag NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE tagRecent NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE tagYear NUMERIC", outFile.getAbsolutePath());    
        
        FileProcess.addLinetoaFile("@ATTRIBUTE pageTime NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE position NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lengthAbsolute NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lengthRelative NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lengthDistFormerPara NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lengthDistAfterPara NUMERIC", outFile.getAbsolutePath());
        
        FileProcess.addLinetoaFile("@ATTRIBUTE numSent NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lenLongSent NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lenShortSent NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lenAvgSent NUMERIC", outFile.getAbsolutePath());
        
        FileProcess.addLinetoaFile("@ATTRIBUTE numTEs NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE numTEsBefore NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE numOfDate NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE numOfDuration NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE numOfTime NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE numOfSet NUMERIC", outFile.getAbsolutePath());
        
        FileProcess.addLinetoaFile("@ATTRIBUTE valEarliestTE NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE valLatestTE NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE valClosestTE NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE valSpanTE NUMERIC", outFile.getAbsolutePath());
        
        FileProcess.addLinetoaFile("@ATTRIBUTE lenDistAvgTEs NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE lenDistLongTEs NUMERIC", outFile.getAbsolutePath());
        FileProcess.addLinetoaFile("@ATTRIBUTE orgFileName STRING", outFile.getAbsolutePath());
        
        FileProcess.addLinetoaFile("@DATA", outFile.getAbsolutePath());
        File[] srcFileList = srcFolder.listFiles();
        for (File srcFile: srcFileList) {
        	System.out.println();
        	System.out.println("Start the File: " + srcFile.getName());
        	
        	//	consider the files in the same folder, but not the tagged pages.
        	//	taggedPageReader.numOfPara will be 0.
        	TaggedPageReader taggedPageReader = new TaggedPageReader(srcFile.getAbsolutePath());
        	if (taggedPageReader.numOfPara == 0)
        		continue;
        	List<ParagraphFeature> paragraphFeatureList = ParagraphFeatureExtractor.extract(taggedPageReader, pipeline);
        	if (paragraphFeatureList.isEmpty() || paragraphFeatureList == null)
        		continue;
        	for (ParagraphFeature paragraphFeature: paragraphFeatureList) {
        		System.out.println(paragraphFeature.featuresToStringWithTag());
        		//	Write the features to the file as a ARFF format
        		FileProcess.addLinetoaFile(paragraphFeature.featuresToARFF(), outFile.getAbsolutePath());
        	}
        	
        	System.out.println("Finish the File: " + srcFile.getName());
        	System.out.println();
        }
        System.out.println( "All Finished!!" );
    }
}
