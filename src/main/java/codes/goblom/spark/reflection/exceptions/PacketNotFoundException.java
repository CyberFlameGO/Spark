/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.reflection.exceptions;

/**
 *
 * @author Goblom
 */
public class PacketNotFoundException extends ClassNotFoundException {

    public PacketNotFoundException(String string) {
        super(string);
    }
}
