package com.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name="seqResult", initialValue=5, allocationSize=1)
public class Result_Data {
	
		@Id
		@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqResult")
		private Long id;
		
		private Long idFile_Data; //iMathCloud id
		
		@Column(nullable=false)
		private String name_DataFile;
		
		
		private static final long serialVersionUID = 1L;

		public Result_Data() {
			super();
		}

		public Long getId(){
			return this.id;
		}
		
		public Long getIdFile(){
			return this.idFile_Data;
		}
		
		public String getNameFile(){
			return this.name_DataFile;
		}
		
		public void setId(Long id){
			this.id = id;
		}
		
		public void setIdFile(Long id){
			this.idFile_Data = id;
		}
		
		public void serNameFile(String name){
			this.name_DataFile = name;
		}
}
