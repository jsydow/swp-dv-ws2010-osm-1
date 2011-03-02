package core.logger;

import core.data.LogParameter;

interface ILoggerService {

	/**
	 * @param param GPS parameters
	 * @param do_one_shot start the track in one_shot mode/add current point
	 * @return ID of the new track
	 * 
	 * This will start logging of a new GPS track. In one_shot mode
	 * logging is disabled after recording one point, each call to
	 * this function will add the current position to the track, in
	 * continuous (default) mode, gps data will be logged in the interval
	 * specified in param and all so obtained points are automatically added
	 * to the track.
	 */
	int addTrack(in LogParameter param, boolean do_one_shot); 
	
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
	* Tells the service to begin a new way and to add way points to it
	*/	
	int beginWay();
	
	/**
	* Tells the service to end way point adding the a way 
	*/
	int endWay();
	
	/**
	* @return true if currently a way is recorded
	*/
	boolean isLogging();
	
}
