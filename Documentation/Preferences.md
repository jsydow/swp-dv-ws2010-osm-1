GPS
===

` * requestLocationUpdates parameters (minimum distance/time until next location) (Array)`\
` * Check GPS by start (Checkbox) - android:key="check_GPSbyStartTracking" , android:defaultValue="true" , getBool();`\
` * Stop GPS signal while inactiv after seconds/minutes (Array) -  android:key="lst_stopGPSafterSeconds" , android:defaultValue="10" , getInt();`

Map view
========

` * Colors for several item groups (ways, areas, POIs) (Colorpicker)`\
` * Map file to be used (for !MapsForge) (EditText)`\
` * Trackfilter, with some SeekBar (CheckBox) android:key="check_setMapFilter" android:default="true"`\
` * Default map from SDCard or online (Array)`\
` * Enable/Disable local tile cache (Checkbox) - android:key="check_activateLocalTitleMapCache" , android:defaultValue="false" , getBool();`\
` * Online tile style (select between OSMARENDER, MAPNIK and OPENCYCLEMAP) - android:key="lst_setOnlineTitleStyle" , android:defaultValue="OSMA" , getString();`

MetaTags
========

` * The max. history size. (Array)- android:key="lst_setHistorySize" , android:defaultValue="30" , getInt();`

OSM Account
===========

` * Accountname (Edittext) - android:key="et_OSMNickname" , android:defaultValue="" , getString();`\
` * Accountpassword (Edittext) - android:key="et-OSMPassword" , android:defaultValue="" , getString();`

UI
==

` * Statusbar Visible / Invisible (Checkbox) - android:key="check_visbilityStatusbar" , android:defaultValue="false" , getBool();`\
` * Themes (Array) - android:key="lst_switchTheme" , android:defaultValue="0" , getInt();`\
` * Display always on (Checkbox) - android:key="check_displayAlwaysOn" , android:defaultValue="false" , getBool(); `\
` * Show GPS signal quality (Checkbox) - android:key="check_showGPSsignalQuality" , android:defaultValue="true" getBool();`

Track
=====

` * Default dataname for tracks (TT.MM.JJJJ or JJJJ.MM.TT etc.) (Array)`

Media
=====

` * Maximum seconds/minutes for video and audio recording (Array)`

android:key="lst\_maxVideoRecording" android:defaultValue="0" ,
getInt();

android:key="lst\_maxAudioRecording" android:defaultValue="0" ,
getInt();

Export
======

` * Mailexport, only GPX file or whole folder of the track(?) (Array)`\
` * Export Email (EditText) - android:key="et_exportMail" , `\
`           android:defaultValue="" , getString(); `
