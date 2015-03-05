package com.zhao.temporal.InfoExtraction;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zhao.temporal.Utils.FileProcess;


public class QueryCount {

	public static void main(String[] args) throws IOException {
		
		File queryInfoFile = new File(args[0]);
		
		//  Initialize
		List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
		for(int i=201;i<=300;i++) {
			QueryInfo queryInfo = new QueryInfo();
			queryInfo.queryID = i;
			queryInfoList.add(queryInfo);
		}
		
		List<String> queryLineList = FileProcess.readFileLineByLine(queryInfoFile);
		for(String queryLine: queryLineList) {
			String[] queryLineContent = queryLine.split(" ");
			int queryID = Integer.parseInt(queryLineContent[1].trim());
			int numOfParaTime = Integer.parseInt(queryLineContent[2].trim());
			QueryInfo queryInfo = queryInfoList.get(queryID - 201);
			if (queryInfo.queryID != queryID)
				return;
			queryInfo.queryTotal = queryInfo.queryTotal + 1;
			switch(numOfParaTime) {			
				case 1: {
					queryInfo.queryTime1++;
					break;
				}
				case 2: {
					queryInfo.queryTime2++;
					break;
				}
				case 3: {
					queryInfo.queryTime3++;
					break;
				}
				default: {
					queryInfo.queryTime4++;
					break;
				}
			}
			queryInfoList.set(queryID - 201, queryInfo);
		}
		
		for(QueryInfo queryInfo: queryInfoList)
			FileProcess.addLinetoaFile(queryInfo.toString(), args[1]);
	}
}
