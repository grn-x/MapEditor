package de.grnx.mapeditor.buildableConf;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class ExternalBlockLoaderIterable implements Iterable<JsonObject> {

    private JsonArray blocksArray;
    public int size;
    
    public ExternalBlockLoaderIterable(String path) {
        String jsonString = readAllLines(path).replace("\\", "/");//willing to fuck up later backslash-formatted information, for illiterate fucks like me who are too lazy to replace win file path backslashes with double escaped/ forward ones
        if (jsonString != null) {
        	try {
            JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
            this.blocksArray = jsonObject.getJsonArray("blocks");
            this.size = (blocksArray != null) ? blocksArray.size() : 1; //1 instead of 0 to not run into issues when instantiating external block array
        	} catch(Exception e) {
        		System.out.println("External Block Config not setup correctly!");
        		e.printStackTrace();
        		//TODO 
        	}
            
        }
    }

    @Override
    public Iterator<JsonObject> iterator() {
        return new JsonIterator();
    }

    private class JsonIterator implements Iterator<JsonObject> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return blocksArray != null && currentIndex < blocksArray.size();
        }

        @Override
        public JsonObject next() {
            return blocksArray.getJsonObject(currentIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private String readAllLines(String path) {
    	//fucking java 7 
     
    	try {
			return new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
		} catch (IOException e) {
			return "";
		}
    }

  
}
