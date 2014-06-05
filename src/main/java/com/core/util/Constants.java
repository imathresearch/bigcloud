package com.core.util;

public class Constants {
				
		static public final String ROOT_FILE_SYSTEM = "/bigCloud"; //In Production, it must be changed
	    
	    static public final String TEMPLATES = ROOT_FILE_SYSTEM + "/templates";
	    
	    static public final String JOB_SA_TEMPLATE = TEMPLATES + "/template_SA_job.py";
	    
	    static public final String JOB_SA_TEMPLATE_PARTIALDATA = TEMPLATES + "/template_SA_job_partialData.py";
	
	    static public final String NAME_FILE_PARTIAL_DATA = "partialData.txt";
	    
	    static public final String FILE_PARTIAL_DATA = TEMPLATES + "/" + NAME_FILE_PARTIAL_DATA;
	    
	    static public final String JOBS_FILES_DIR = ROOT_FILE_SYSTEM + "/jobs_files";
}
