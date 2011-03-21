package tracebook.core.logger;

interface ILoggerService {

	/**
	 * Starts a new {@link DataTrack}. 
	 */
	void startTrack(); 
	
	/**
	* @return id of the track
	* 
	* Stops logging of the current track and disabes gps.
	* In one_shot mode, a last point will be recorded after calling
	* this function, in continous mode loggig will stop instantly.
	*/
	int stopTrack();
	
	/**
	* @param on_way whether the POI is on the current track
	* @return id of the new POI, -1 if creating of the POI has failed
	* 
	* Adds a new Point of Interest, returning it's id. The location of
	* the poi will be updated next time a gps fix is availiable, if no
	* continuous way is recorded, gps is disabled after obtaining the fix.
	* 
	* This function will start gps, it currently no track is recorded but
	* on_way is true, the point will be added regardless.
	*/
	int createPOI(boolean on_way);
	
	/**
	 * Starts a new area.
	 * If oneShotMode is true, points are only added when calling beginArea() again, otherwise all gps fixes
	 * will be added to the way automatically.
	 * 
	 *  @param oneShotMode Start a way in oneShotMode or add a Point to the way when there is already a way started
	 *  
	 *  @return the ID of the new area
	 */
	int beginArea(boolean oneShotMode);
	
	/**
	 * Starts a new way.
	 * If oneShotMode is true, points are only added when calling beginWay() again, otherwise all gps fixes
	 * will be added to the way automatically.
	 * 
	 *  @param oneShotMode Start a way in oneShotMode or add a Point to the way when there is already a way started
	 *  
	 *  @return the ID of the new way
	 */
	int beginWay(boolean oneShotMode);
	
	/**
	* Stops the current way or area.
	*/
	int endWay();
	
	
	/**
	* @return true if currently a way is recorded
	*/
	boolean isWayLogging();
	
	/**
	* @return true if area loggin is in progress
	*/
	boolean isAreaLogging();
	
	/**
	 * Stops GPS logging.
	 */
	void pauseLogging();
	
	/**
	 * Resumes GPS logging.
	 */
	void resumeLogging();
	
	/**
	 * Returns the state of the GPS logging.
	 * @return true if gps logging is running
	 */
	boolean isLogging();
	
}
