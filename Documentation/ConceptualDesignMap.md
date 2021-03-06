Introduction
============

We use [MapsForge](http://code.google.com/p/mapsforge/) to draw our map
and overlay Ways and Points of interest. We use an \_ItemizedOverlay\_
to display all POIs, the current position and waypoints. A
\_WayOverlay\_ serves as representation for all recorded ways and areas.

Details
=======

MapsForgeActivity
-----------------

Our \_MapsForgeActivity\_ extends the MapsForge \_MapActivity\_, not
only to display a context menu but also to overwrite
\`dispatchTouchEvent()\`, so that we can move Points around. We send
notifications about different possible actions like adding way nodes,
change of the current position, etc. as broadcast Intent, \_GpsMessage\_
provides methods for composing such Intends. MapsForgeActivity therefore
registers a \_GPSReceiver\_, a \_BroadcastReceiver\_ that will receive
these Intends. It will however only receive messages when the
MapsForgeActivity is active. So we clear all overlays on resume an
populate them again with all POIs and ways, this way we make sure not to
miss anything. (It actually saves us from handling the case of adding a
POI to the overlay by BroadCast intent as we have no means of adding a
POI without rendering the MapActivity inactive. Every DataNode
references an OverlayItem, it will automatically update the OverlayItems
position if it's position changes, it's OverlayItem should be added to
the list of invalid items when it is removed.

### GPSReceiver

` * For every new GPS fix we receive a UPDATE_GPS_POS Intent that contains the new Position, so the marker for the current position can be updated. In one_shot mode we also update the overlay current way, adding the new point to the WayOverlay only, to give a preview how the way will look like with the new point.`\
` * For every new way point (and theoretically for every new POI too, but as mentioned before, this never happens) we receive a UPDATE_OBJECT Intent. If it is a new way (we compare it with the value the stored new way) we add it to the DataPointsListArrayRouteOverlay, otherwise we simply request a redraw of the WayOverlay.`\
``  * The MOVE_POINT Intent signals that the user requested to edit the node with the given ID, we store this node, so `dispatchTouchEvent()` will update the position of this node to where the user clicks. (And also updates the way when the node is part of one) ``\
` * END_WAY signals the end of a way, mainly to change it's color, but it is also planned to give the user some options for adjusting smoothing that would be hooked here.`\
` * To clear the list of invalid OverlayItems, actions that did remove OverlayItems will send a REMOVE_INVALIDS Intent, so we can fetch the list of invalid Items and remove them from the Overlay.`

DataNodeArrayItemizedOverlay
----------------------------

The DataNodeArrayItemizedOverlay extends MapsForges ArrayItemizedOverlay
to overwrite the onTap() method and display a context menu (an
AlertDialog actually) for each POI. It gets the actual DataNode
corresponding to the OverlayItem by searching all Nodes of the
currentTrack. If the OverlayItem is not referenced by any DataNode, it
is assumed it is the marker marking the current position of the user.

DataPointsListArrayRouteOverlay
-------------------------------

The DataPointsListArrayRouteOverlay extends MapsForges
DataPointsListArrayRouteOverlay just for convenience, it doesn't
overwrite any methods but only offers some wrappers to add
DataPointsLists. Those store their OverlayWay just as the DataNode.
(They however do not update it's content automatically, but
\`updateOverlayRoute()\` is required to align the OverlayWay's data.)
