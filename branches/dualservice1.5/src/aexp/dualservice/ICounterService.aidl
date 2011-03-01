package aexp.dualservice;

interface ICounterService {
	/**
	 * returns List of so far received fixes
	 */
	List<Location> getPoints();
	
	/**
	* deletes list of stored fixes
	*/
	void clearList();
}
