package core.data;

/**
 * Anything that is a SerializableContent will serialise itself 
 * if serialise() is invoked. The SerialisableContent will use 
 * the appropriate ContentProvider. The implementing class 
 * should also have a method deserialise() which returns such an
 * Object. Refer to the specific class for further information on
 * deserialise().
 * @author js
 *
 */
interface SerialisableContent {
	/**
	 * serialise() will save the Object on the device memory using
	 * a ContentProvider.
	 */
	void serialise();
}
