package com.core.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Encryptor {
	
	public static void init(){
		System.setProperty("jasypt_password", "jasyptkey");
    	String passwordEncryptor = System.getProperty("jasypt_password");
		StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
        strongEncryptor.setPassword(passwordEncryptor);
        org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry registry =
                org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry.getInstance();
        registry.registerPBEStringEncryptor("STRING_ENCRYPTOR", strongEncryptor);
	}

}
