/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author Goblom
 */
@AllArgsConstructor
public class Vector3D {
    
    public static Vector3D ORIGIN = new Vector3D(0, 0, 0);
    
    @Getter
    protected double x, y, z;
    
    public Vector3D(Location location) {
        this(location.toVector());
    }

    public Vector3D(Vector vector) {
        if (Utils.isNull(vector)) {
            throw new IllegalArgumentException("Vector cannot be null in Vector3D");
        }
        
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }
    
    public Vector toVector() {
        return new Vector(x, y, z);
    }
    
    public Vector3D add(Vector3D other) {
        if (Utils.isNull(other)) {
            throw new IllegalArgumentException("other cannot be added if it is null");
        }
        
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        
        return this;
    }
    
    public Vector3D add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        
        return this;
    }
    
    public Vector3D subtract(Vector3D other) {
        if (Utils.isNull(other)) {
            throw new IllegalArgumentException("other cannot be subtracted if it is null");
        }
        
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        
        return this;
    }
    
    public Vector3D subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        
        return this;
    }
    
    public Vector3D multiply(int factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        
        return this;
    }
    
    public Vector3D multiply(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        
        return this;
    }
    
    public Vector3D divide(int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Cannot divide by 0.");
        }
        
        this.x /= divisor;
        this.y /= divisor;
        this.y /= divisor;
        
        return this;
    }
 
    
    public Vector3D divide(double divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Cannot divide by 0.");
        }
        
        this.x /= divisor;
        this.y /= divisor;
        this.z /= divisor;
        
        return this;
    }
    
    public Vector3D abs() {
        return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
    }
    
    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
    
    @Override
    public String toString() {
        return String.format("Vector3D[x: %s, y: %s, z: %s]", x, y, z);
    }
}
