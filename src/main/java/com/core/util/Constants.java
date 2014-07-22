package com.core.util;

public class Constants {
				
		static public final String ROOT_FILE_SYSTEM = "/bigCloud"; //In Production, it must be changed
	    
	    static public final String TEMPLATES = ROOT_FILE_SYSTEM + "/templates";
	    
	    static public final String JOB_SA_TEMPLATE = TEMPLATES + "/template_SA_job.py";
	    
	    static public final String JOB_SA_TEMPLATE_PARTIALDATA = TEMPLATES + "/template_SA_job_partialData.py";
	
	    static public final String NAME_FILE_PARTIAL_DATA = "partialData.txt";
	    
	    static public final String FILE_PARTIAL_DATA = TEMPLATES + "/" + NAME_FILE_PARTIAL_DATA;
	    
	    static public final String JOBS_FILES_DIR = ROOT_FILE_SYSTEM + "/jobs_files";
	    
	    static public final String BIGCLOUD_HOST = "127.0.0.1";
	    //static public final String BIGCLOUD_HOST = "158.109.125.112";
	    
	    static public final String BIGCLOUD_PORT = "8080"; // in production it must be changed
	    //static public final String BIGCLOUD_PORT = "80"; // in production it must be changed
	    
	    static public final String ADD_USER_CLI = "./add-user.sh";
	    static public final String ADD_USER_LINUX = "useradd";
	    
	    static public final String ROLES_FILE = "../standalone/configuration/application-roles.properties";
	    static public final String USERS_FILE = "../standalone/configuration/application-users.properties";
	    static public final String ROLES_DOMAIN_FILE = "../domain/configuration/application-roles.properties";
	    static public final String USERS_DOMAIN_FILE = "../domain/configuration/application-users.properties";
	    static public final String SYSTEM_ROLE = "WebAppUser";
	    
}
