General
=======

We have following hierarchy:

`* a *Track* is a "session", it contains a set of POI's, Ways, Areas and Media the user collects in one or multiple app-uses.`\
`* *Media* are textmemos, photos, audiorecordings and videos that help to remember things`\
`* *POI*'s are a gps-node with certain tags`\
`* *Ways* are a list of gps-nodes with certain tags. Mind that the list can have some specially tagged nodes like traffic lights and barriers.`\
`* *Areas* are closed Ways.`

Dataformat
==========

All data have a unique id.

Node
----

In general every node we get from the gps has:

` * location`\
` * timestamp`

These are the data every node has at a minimum. There are also the data
a Node in osm xml format has. Additionally osm-nodes have tags.

The dataformat for all these metadata according to osm-specificaion is:

``  * latitude: Interval `[`-90, 90`]`, 7 digits precision => float ``\
``  * longitude: Interval `[`-180, 180`]`, 7 digits precision => float ``\
` * timestamp: `[`http://www.w3.org/TR/NOTE-datetime`](http://www.w3.org/TR/NOTE-datetime)\
``  * tags: key (k) and value (v) are Strings: ` ``<tag k="str" v="str" />`` ` ``

Way
---

Ways are a list of Nodes + Timestamp + Tags according to osm format.

Areas
-----

Areas are in osm simply Ways with a landuse-tag and possibly a
area=yes-tag

Implementation
==============

The User should be able to:

`* Start/End a Way`\
`* Add a special point to the currently tracked way. (Should only be usable if there is a Way which is currently tracked)`\
`* Create a POI`\
`* Start/End an Area`\
`* Record Media (photos, audio recordings, text) and then assign it to one or more previously created POI's or Ways or Areas`

On the devices memory there is a folder which contains all data. Each
track gets a folder. In this folder all Media are saved plus the way
data (POI, Way, Area).

Operations
==========

`* +: insert()`\
`* -: delete()`\
`* ~: update()`\
`* #: query()`

Tracks
------

`* + new Track`\
`* - delete Track`\
`* ~ change Track-information (date, name)`\
`* # list all Tracks`\
`* # get Trackinformation (date, name, #entries?)`\
`* # get all Nodes, Ways, Areas, Media`

Ways / Areas
------------

All Way-Operation apply also to Areas:

`* # list all Media`\
`* # list all Nodes`\
`* # get Way information (tags, name)`\
`* + new Way`\
`* - delete Way`\
`* ~ change Way-Information (tags, name) (delete, change, add)`\
`* ~ change Media (delete, add)`\
`* ~ change Nodes (delete, add)`

Node
----

`* + add Node`\
`* # view information (tags, name, lat, lon, time)`\
`* # list all Media`\
`*`

Media
-----

`* + add Media`\
`* # get Media information (name, time, path)`\
`* - delete Media`\

