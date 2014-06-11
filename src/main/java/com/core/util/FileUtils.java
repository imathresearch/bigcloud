package com.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.ejb.Stateful;

@Stateful
public class FileUtils {
	
	static public void copyFiles(File src, File dst) throws IOException{
		
		Path p_src = Paths.get(src.getPath());
		Path p_dst = Paths.get(dst.getPath());
		
		//System.out.println("source " + src.getPath());
		//System.out.println("destination " + dst.getPath());
		
		Files.copy(p_src, p_dst, StandardCopyOption.REPLACE_EXISTING);
		
	}

}
