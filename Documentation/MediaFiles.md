1.  summary Acquiring audio, video and image files.
2.  labels Phase-Design

<wiki:toc max_depth="2" />

Introduction
============

One of the features the product owner asked for at the beginning of the
second sprint is the capability of recording audio and video files, as
well as taking photos for both ways/areas and points of interest.

Ideally, we would not have to deal with the details of how acquiring
such files is done, because we would just use intents for those tasks.
But as the world is not a place where wishes come true that easily, we
will actually have to put some work into this.

Common concepts
===============

Internal representation, data structure
---------------------------------------

For every single media file, there is a !DataMedia object that is
"attached" to a !DataMediaHolder (see ConceptualDesignDataStorage)
object. To do so, every recorder class has a method

that does exactly that, whereas said !DataMediaHolder object is passed
to this method as a reference.

Internal representation, file system
------------------------------------

Since each track has its own folder (in /sdcard/!TraceBook/), we can
just put new media files into the folder of the track. The filenames
contain the media type (audio|image|video), and a human readable
timestamp (yyyy-MM-dd\_HH-mm-ss). The suffixes are chosen according to
their file types (.jpg, .m4a, or .mp4).

Pictures
========

Taking pictures is the easiest of the three media types, because this
can be done reliably on all devices using intents, usually sent to the
default camera application on the device.

Upon successfully taking a picture, \`appendFileToObject\` has to be
invoked manually during the \`onActivityResult\` callback of the
activity that had sent the intent.

Videos
======

Recording a video can be done using intents, as well, in theory. With
intents, however, we faced a multitude of problems on several different
devices running different releases of Android. The camcorder application
did not work at all (and caused a full system crash), created empty
video files, or worked fine, except for saving videos to the wrong
location. Each type of behaviour seems to be typical for each
manufacturer, respectively, and the only thing that works constantly
using intents is that it is not working the way we want it to. ;-/

As such, we record videos using the
[MediaRecorder](http://developer.android.com/reference/android/media/MediaRecorder.html)
class, which we have to create a "video recording" activity for. This
activity provides the surface GUI item necessary for the !MediaRecorder
class to show the camera preview in, as well as start/stop buttons to
control recording. But even when using !MediaRecorder, we have come
across problems with several different devices, unfortunately.

Video data is encoded by the native MPEG-4 codec, whereas audio data has
to be encoded by AMR narrow-band, because the other two possibilities
(AAC and AMR wide-band) are not supported at API level 4. Both the video
track and the audio track lie within an MPEG-4 container.

Audio files
===========

Unfortunately, Android does not provide a standard action for recording
audio files using intents. Therefore, we use !MediaRecorder for this
task. It does not require any certain GUI elements, except for a button
to actually stop recording.

We have opted against using the
[AudioRecord](http://developer.android.com/reference/android/media/AudioRecord.html)
class, despite the possibility to record files of higher quality than
!MediaRecorder. Handling !AudioRecord, however, is much more tedious
than using !MediaRecorder, because we would have to write the whole
audio stream !AudioRecord provides into a file (easy), and we would have
to make sure the headers for the .wav file we have created are correct
(not so easy), among other things.

Since we are developing at API level 4, we cannot utilize any other
audio codec than AMR narrow-band (see above). Like video files, MPEG-4
serves as a container for the audio track.
