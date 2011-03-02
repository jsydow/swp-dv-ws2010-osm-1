package core.logger;

import core.data.LogParameter;

interface ILoggerService {
	/**
	* @param param logging parameters including the id of the track to continue or -1 if a new track is to be started 
	*/
	int addTrack(in LogParameter param, boolean do_one_shot); 
	
	/**
	* stops logging of the current track
	* @return id of the track
	*/
	int stopTrack();
	
	/**
	* create a new Point of interest
	* @param onWay whether the POI is on the current track
	* @return id of the new POI, -1 if creating of the POI has failed (e.g. there is no current track and onWay is true)
	*/
	int createPOI(boolean onWay);
	
	/**
	* @return true if logging is ongoing
	*/
	boolean isLogging();
	
}
