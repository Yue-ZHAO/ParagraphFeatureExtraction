package com.zhao.temporal.Utils;

import java.io.File;
import java.io.FilenameFilter;

public class nameFilter implements FilenameFilter {

	String filename = "";
	
	public nameFilter(String filename) {
		// TODO Auto-generated constructor stub
		this.filename = filename;
	}

	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		if (name.contains(filename))
			return true;
		else
			return false;
	}

}
