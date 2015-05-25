/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.reflection.safe;

//import codes.goblom.core.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author Goblom
 */
public class SafeField<R> implements Safe<Field> {

    private final Field unsafe;
    
    public SafeField(Field f) {
        this.unsafe = f;
    }
    
    public SafeField(Class<?> coreClass, String fieldName) {
        Field field = getField(coreClass, fieldName);

        while (field == null && coreClass != null) {
            coreClass = coreClass.getSuperclass();
            field = getField(coreClass, fieldName);
        }
        
        if (field == null) {
//            Log.severe("Class [%s] does does not have a superclass that has field [%s]", coreClass.getSimpleName(), fieldName);
        }
        
        this.unsafe = field;
    }
    
    private Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) { }
        
        try {
            return clazz.getField(name);
        } catch (Exception e) { }
        
        return null;
    }
    
    @Override
    public Field unsafe() {
        return this.unsafe;
    }

    @Override
    public String getName() {
        return unsafe().getName();
    }

//    @Override
    public R get(Object instance) {
        if (!isStatic() && instance == null) {
            throw new UnsupportedOperationException("Non-static fields require a non-null instance passed in!");
        }
        try {
            return (R) unsafe().get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public SafeObject getSafe(Object instance) {
        return new SafeObject(get(instance));
    }
    
//    @Override
    public boolean set(Object instance, R value) {
        if (!isStatic() && instance == null) {
            throw new UnsupportedOperationException("Non-static fields require a valid instance passed in!");
        }

        try {
            unsafe().set(instance, value);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(unsafe().getModifiers());
    }

    @Override
    public boolean isReadOnly() {
        return Modifier.isFinal(unsafe().getModifiers());
    }

//    @Override
    /**
     * Please test this way. It might be genius and work. Or it might not. PLEASE TEST
     * 
     * TODO: TEST
     */
    public void setReadOnly(boolean readOnly) {
        SafeField<Integer> modifiers = new SafeField<Integer>(Field.class, "modifiers");
                           modifiers.setAccessible(true);
                           
        if (readOnly) {
            modifiers.set(unsafe(), unsafe().getModifiers() | Modifier.FINAL);
        } else {
            modifiers.set(unsafe(), unsafe().getModifiers() & ~Modifier.FINAL);
        }
    }
    
    @Override
    public boolean isStatic() {
        return Modifier.isStatic(unsafe().getModifiers());
    }
    
    @Override
    public String toString() {
        if (!isOk()) {
            return super.toString();
        }
        
        StringBuilder string = new StringBuilder(75);
        int mod = unsafe().getModifiers();
        if (Modifier.isPublic(mod)) {
            string.append("public ");
        } else if (Modifier.isPrivate(mod)) {
            string.append("private ");
        } else if (Modifier.isProtected(mod)) {
            string.append("protected ");
        }

        if (isStatic()) {
            string.append("static ");
        }

        if (isReadOnly()) {
            string.append("final ");
        }
        
        string.append(unsafe().getType().getSimpleName());
        string.append(" ");
        string.append(getName());

        return string.toString();
    }

    @Override
    public void setAccessible(boolean flag) {
        if (unsafe() == null) {
            return;
        }
        
        unsafe().setAccessible(flag);
    }
    
//    public static void main(String[] args) {
//        SafeField<SafeField> safeField = new SafeField(SafeField.class, "unsafe");
//                             safeField.setAccessible(true);
//                             
//        safeField.setReadOnly(true);
//        System.out.println(safeField.toString());
//        
//        safeField.setReadOnly(false);
//        System.out.println(safeField.toString());
//    }
}
