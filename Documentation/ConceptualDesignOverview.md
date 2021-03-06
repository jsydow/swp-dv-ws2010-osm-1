Introduction
============

Here you will see a base overview of the main parts involved in
!TraceBook.

Details
=======

!TraceBook is an Android based Java application which has only one
external dependency, which is the
[MapsForge](http://code.google.com/p/mapsforge/) library which we use to
draw a map layer. We use only standard Java and Android methods.

Core
====

The core of !TraceBook consists of a main "!WayLoggerService" which is a
service of !TraceBook and is running continuously in background and a
"!DataStorage" engine which handles serialization, deserialization and
handling of the collected date in main memory. The "!WayLoggerService"
is accessible from all parts of the code and is detached form the GUI.
It provides all core functionality to start and track routes.

Data Storage
============

The !DataStorage architecture is based on the idea to handle a internal
representation of the data structure we use in main memory and an XML
based manifestation. We choose XML because it suits very well with its
tree structure, which a good mapping to our internal structure that is
also a tree structure.
