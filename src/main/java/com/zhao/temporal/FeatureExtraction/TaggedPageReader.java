package com.zhao.temporal.FeatureExtraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zhao.temporal.Utils.FileProcess;

public class TaggedPageReader {
	
	public String url = "";
	public String timestamp = "";
	public String originalFileName = "";
	public int numOfPara = 0;
	public List<Paragraph> paragraphs = new ArrayList<Paragraph>();
	//	Add more elements if needed.............
	
	public TaggedPageReader (String pagePath) throws IOException {
		
		// TODO Filter the files which are not the tagged page
		int flagForTaggedPage = 0;
		List<String> linesFile = FileProcess.readFileLineByLine(pagePath);
		for (String lineFile: linesFile) {
			if (lineFile.startsWith("#")) {
        		//	2.1	Extract file info
				if (lineFile.startsWith("#URL: ")) {
					url = lineFile.substring(6);
					flagForTaggedPage++;
        		} else if (lineFile.startsWith("#Page Timestamp: ")) {
        			timestamp = lineFile.substring(17);
        			flagForTaggedPage++;
        		} else if (lineFile.startsWith("#Original File: ")) {
        			originalFileName = lineFile.substring(16);
        			flagForTaggedPage++;
        		} else if (lineFile.startsWith("#Number of Paragraphs: ")) {
        			numOfPara = Integer.parseInt(lineFile.substring(23));
        			flagForTaggedPage++;
        		} else
        			continue;
        	} else {
        		if (flagForTaggedPage < 4)
        			continue;
        		
				String[] lineContents = lineFile.split("\t", 3);
				if (lineContents.length < 3)
					continue;
				
				String position = lineContents[0].trim();
				String timestamp = lineContents[1].trim();
				String content = lineContents[2].trim();
				
				String startPos = position.split("-")[0].trim();
				String endPos = position.split("-")[1].trim();
				
				Paragraph paragraph = new Paragraph();
				paragraph.setStartPoint(Integer.parseInt(startPos));
				paragraph.setEndPoint(Integer.parseInt(endPos));
				paragraph.setTimestamp(timestamp);
				paragraph.setContent(content);
				
				paragraphs.add(paragraph);				
        	}
		}		
	}
	
	public static void main(String[] args) throws IOException {

		TaggedPageReader taggedPageReader = new TaggedPageReader(args[0]);
		System.out.println(taggedPageReader.url);
		System.out.println(taggedPageReader.timestamp);
		System.out.println(taggedPageReader.originalFileName);
		System.out.println(taggedPageReader.numOfPara);
		
		for (Paragraph paragraph: taggedPageReader.paragraphs) {
			System.out.println(paragraph.getStartPoint());
			System.out.println(paragraph.getEndPoint());
			System.out.println(paragraph.getTimestamp());
			System.out.println(paragraph.getContent());
		}
	}
}
