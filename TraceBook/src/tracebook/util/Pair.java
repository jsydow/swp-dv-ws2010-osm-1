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

    // I hate you, Checkstyle, I hate you.

    /**
     * Gets the first element.
     * 
     * @return the first element
     */
    public A getFirst() {
        return first;
    }

    /**
     * Sets the first element.
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
     * Sets the second element.
     * 
     * @param second
     *            the first element
     */
    public void setSecond(B second) {
        this.second = second;
    }

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
