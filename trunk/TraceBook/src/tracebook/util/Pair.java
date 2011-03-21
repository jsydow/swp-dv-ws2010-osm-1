package tracebook.util;

/**
 * a generic Tuple element to store two values.
 * 
 * @param <A>
 *            first element type
 * @param <B>
 *            second element type
 */
public class Pair<A, B> {
    /**
     * First value stored in the Pair.
     */
    A first;

    /**
     * Second value stored in the Pair.
     */
    B second;

    /**
     * Creates a new Pair.
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
