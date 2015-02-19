package com.zhao.temporal.InfoExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.zhao.temporal.FeatureExtraction.Paragraph;
import com.zhao.temporal.FeatureExtraction.TaggedPageReader;
import com.zhao.temporal.Utils.FileProcess;
import com.zhao.temporal.Utils.nameFilter;

public class ParagraphExtractor {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Hello, I am paragraph extractor!");
		String featureFilePath = args[0];
		String pageFolderPath = args[1];
		String outputFilePath = args[2];
		
		int fileNameIndex = Integer.parseInt(args[3]);
		int positionIndex = Integer.parseInt(args[4]);
		//	int lengthIndex = Integer.parseInt(args[5]);
		
		ParagraphExtractor.extract(featureFilePath, pageFolderPath, outputFilePath, fileNameIndex, positionIndex);
		System.out.println("All finished, bye!");
	}

	public static void extract(String featureFilePath, String pageFolderPath,
			String outputFilePath, int fileNameIndex, int positionIndex) throws IOException {
				
		File featureFile = new File(featureFilePath);
		//	Read the file line by line to find the URL
		InputStream fis;
		BufferedReader br;
		String line;
			
		fis = new FileInputStream(featureFile);
		br = new BufferedReader(new InputStreamReader(fis));
		int i = 1;	
		while ((line = br.readLine()) != null && i < 1000) {
			//	Deal with the line
			if (!line.startsWith("#")) {
				System.out.println(i + "\t" + line);
				i++;
				
				String[] lineContent = line.split(",");
				String filename = lineContent[fileNameIndex];
				int position = Integer.parseInt(lineContent[positionIndex]);
				//	int length = Integer.parseInt(lineContent[lengthIndex]);
				
				File pageFolder = new File(pageFolderPath);
				File[] pageFileList = pageFolder.listFiles(new nameFilter(filename));
				if (pageFileList.length == 0)
					continue;
				else {
					boolean flag = false;
					for (File pageFile: pageFileList) {
						TaggedPageReader taggedPageReader = new TaggedPageReader(pageFile.getAbsolutePath());
						for (Paragraph paragraph: taggedPageReader.paragraphs) {
							if (paragraph.getStartPoint() == position) {
								// Write the paragraph into output file
								FileProcess.addLinetoaFile(line, outputFilePath);
								FileProcess.addLinetoaFile("Filename: " + taggedPageReader.originalFileName, outputFilePath);
								FileProcess.addLinetoaFile("File Timestamp: " + taggedPageReader.timestamp, outputFilePath);
								FileProcess.addLinetoaFile("Paragraph Timestamp: " + paragraph.getTimestamp(), outputFilePath);
								FileProcess.addLinetoaFile(paragraph.getContent(), outputFilePath);
								FileProcess.addLinetoaFile("", outputFilePath);
								FileProcess.addLinetoaFile("", outputFilePath);
								flag = true;
								break;
							}
						}
						if (flag)
							break;
					}
				}					
			}
		}

		//	Done with the file
		br.close();
		br = null;
		fis = null;
	}

}
