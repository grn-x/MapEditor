
package de.grnx.mapeditor.helper.filehandler;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Objects;

import com.badlogic.gdx.utils.SerializationException;


public class SerializationUtils {


 
    
   public static <T> T deserialize(final byte[] objectData) {
       Objects.requireNonNull(objectData, "objectData");
       return deserialize(new ByteArrayInputStream(objectData));
   }

 
   public static <T> T deserialize(final InputStream inputStream) {
       Objects.requireNonNull(inputStream, "inputStream");
       try (ObjectInputStream in = new ObjectInputStream(inputStream)) {
           @SuppressWarnings("unchecked")
           final T obj = (T) in.readObject();
           return obj;
       } catch (final ClassNotFoundException | IOException ex) {
           throw new SerializationException(ex);
       }
   }

  
   @SuppressWarnings("unchecked")
public static <T extends Serializable> T check(final T obj) {
       return (T) deserialize(serialize(obj));
   }


   public static byte[] serialize(final Serializable obj) {
       final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
       serialize(obj, baos);
       return baos.toByteArray();
   }

   public static void serialize(Serializable obj, OutputStream outputStream) {
       Objects.requireNonNull(outputStream, "outputStream");
       try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
           out.writeObject(obj);
       } catch (final IOException ex) {
           throw new SerializationException(ex);
       }
   }
   
   public static String bytesToHex(byte[] bytes) {
	   final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
   
}




























































