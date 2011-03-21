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
     * first value stored in the Pair.
     */
    A first;

    /**
     * second value stored in the Pair.
     */
    B second;

    // I hate you, checkstyle, I hate you.

    /**
     * Gets the first element.
     * 
     * @return the first element
     */
    public A getFirst() {
        return first;
    }

    /**
     * set the first element.
     * 
     * @param first
     *            the first element
     */
    public void setFirst(A first) {
        this.first = first;
    }

    /**
     * Gets the second element.
     * 
     * @return the second element
     */
    public B getSecond() {
        return second;
    }

    /**
     * set the second element.
     * 
     * @param second
     *            the first element
     */
    public void setSecond(B second) {
        this.second = second;
    }

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
