package aexp.dualservice;

interface ICounterService {
	/**
	 * Starts GPS Tracking, enables GPS
	 */
	void startTracking();
	
	/**
	 * Stops GPS Tracking, disables GPS (TODO)
	 */
	void stopTracking();
	
	/**
	 * returns List of so far received fixes
	 */
	List<Location> getPoints();
	
	/**
	* deletes list of stored fixes
	*/
	void clearList();
}
