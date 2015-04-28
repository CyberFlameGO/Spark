/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.policy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor( access = AccessLevel.PROTECTED )
public abstract class Policy<T> {   
    
    public final Class<T> forClass;
    
    public abstract boolean isValid();
}
