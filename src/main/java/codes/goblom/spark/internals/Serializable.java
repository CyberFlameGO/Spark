/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Goblom
 */
public interface Serializable {
    
    default byte[] serialize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
                               oos.writeObject(this);
            return baos.toByteArray();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        
        return new byte[0];
    }
    
    public static <T extends Serializable> T deserialize(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        
        return (T) o.readObject();
    }
}
