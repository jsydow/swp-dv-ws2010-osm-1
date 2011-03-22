#!/bin/bash
TARGET="../mapsforge"

svn checkout http://mapsforge.googlecode.com/svn/trunk/mapsforge $TARGET
cd $TARGET
ant clean
mapsforge-map-javadoc-create
ant mapsforge-map-jar-create
cd -
cp $TARGET/dist/mapsforge-map-0.2.1.jar lib/
