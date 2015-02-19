package com.zhao.temporal.InfoExtraction;

import java.io.File;
import java.io.IOException;

import com.zhao.temporal.FeatureExtraction.TaggedPageReader;
import com.zhao.temporal.Utils.FileProcess;

public class TimestampCount {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String taggedPageFolderPath = args[0];
		String outputFilePath = args[1];
		
		File taggedPageFolder = new File(taggedPageFolderPath);
		File[] taggedPageList = taggedPageFolder.listFiles();
		
		for (File taggedPage: taggedPageList) {
			TaggedPageReader taggedPageReader = new TaggedPageReader(taggedPage.getAbsolutePath());
			if (taggedPageReader.numOfTimestamps > 0) {				
				String writeDown = taggedPageReader.originalFileName + " " + taggedPageReader.numOfTimestamps;				
				System.out.println(writeDown);
				FileProcess.addLinetoaFile(writeDown, outputFilePath);
			}
		}		
	}

}
