package com.core.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class MapUtils {
	
	/**
     * Generic HashMap Class that can be used to manage the params received as a json string
     * @throws IOException
     */
    static public class MyMap <T,A> {
    	
		Map <T,A> mymap;
	
		public MyMap(){
			mymap = new HashMap<T,A>();
		}
	
		public Map<T,A> getMap(){
			return this.mymap;
		}
	
		public A getValue(T key){
			return this.mymap.get(key);
		}
	
		public void setValue(T key, A value){
			this.mymap.put(key, value);
		}
	
		public void jsonToMap(String json){
			ObjectMapper mapper = new ObjectMapper();
			try {
				this.mymap = mapper.readValue(json, 
				    new TypeReference<HashMap<T,A>>(){});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		/**
		  * Method to convert map into json format
		  * @param map with data to be converted into json
		  * @return json string
		  */
		 public String createJsonString() throws IOException {
			 Writer writer = new StringWriter();
			 JsonGenerator jsonGenerator = new JsonFactory().createJsonGenerator(writer);
			 ObjectMapper mapper = new ObjectMapper();
			 mapper.writeValue(jsonGenerator, this.mymap);
			 jsonGenerator.close();
			 //System.out.println(writer.toString());
			 return writer.toString();
		 }
	}

}
