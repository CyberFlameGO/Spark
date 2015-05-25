package codes.goblom.spark.misc;

/**
 *
 * @author Goblom
 */
public interface Sequence<T> {
    
    public T current();
    
    public T next();
    
    public T previous();
    
    public T get(int index);
    
    public void set(int index, T obj);
}
