# swp-dv-ws2010-osm-1
swp-dv-ws2010-osm-1 is the repository for a software project at the Freie Universität Berlin.

Goal of the software project was the development of an Android app that helps you contributing to the OpenStreetMap project.
Using your Android device you can record GPS traces or single points of interests and annotate them with tags, text notes, 
audio notes and photos. Using the collected data the OpenStreetMap can then be edited at home using jOSM.

The repository contains three projects with the main project being TraceBook. The subprojects are:

### OSMtags ###

A project for parsing the OpenStreetMap tags from the OSM wiki into a XML format. This project is written in ruby.

### TraceBook ###

The Android app for collecting GPS traces and points and annotate them with tags, text notes, 
audio notes and photos. It uses the mapsforge map renderer display the collected data on the OpenStreetMap.

**Note: This version is the one that resulted in the software project. The development switched to its own TraceBook repository afterwards.
The new repository can be found under links**

### TraceBookImport ###

The jOSM plugin for importing the data that were collected using the TraceBook app.

### Links ###

OpenStreetMap: http://www.openstreetmap.org/  
mapsforge: https://github.com/mapsforge/mapsforge  
jOSM: https://josm.openstreetmap.de/  
Freie Universität Berlin: http://www.mi.fu-berlin.de/inf/  
TraceBook: https://github.com/jsydow/tracebook
