Introduction
============

It is important that the UI can tell the core what stuff to save, what
interval the points should have and other stuff. To achieve this we have
to use effective inter-process-communication.

Details
=======

` * To allow getting GPS data only when needed, it would be good to have the GPS service as a bound service, to allow it to deliver its data via RPC.`\
` * If the GPS device supports updating in interval via callback, those information has to be send to the core/db for editing and saving`\
` * A bound service typically lives until all clients have unbound`\
` * The core should be also a bind service, so it can accept commands from the ui (start/stop track, attach photo, create POI)`\
` * the core should start GPS service with the correct parameters to get enough information AND use as few ressources as possible `\
` * *Avoid busy waiting!!*`

other possibility: Messengers, e.g. for telling the core to attach a
photo to the next/last node

` * `[`http://developer.android.com/guide/topics/fundamentals/bound-services.html#Messenger`](http://developer.android.com/guide/topics/fundamentals/bound-services.html#Messenger)\
` * Pro: Messages dont wait for an answer, the message gets queued and processed when ready to`
