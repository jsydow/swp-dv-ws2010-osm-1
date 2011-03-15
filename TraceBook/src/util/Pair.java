package util;

/**
 * a generic Tuple element to store two values.
 * 
 * @author benpicco
 * 
 * @param <A>
 *            first element type
 * @param <B>
 *            second element type
 */
public class Pair<A, B> {
    /**
     * first value stored in the Pair
     */
    public A first;

    /**
     * second value stored in the Pair
     */
    public B second;

    /**
     * Create a new Pair.
     * 
     * @param first
     *            first element
     * @param second
     *            second element
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

}
