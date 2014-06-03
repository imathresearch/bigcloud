package com.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	}

}
