# Streaming App

A Java server/client application to stream videos using FFMPEG.

## Streaming Server

Once the server starts it will generate any missing variants for each video file in the `videos` folder.
A video variant is distinguish by its format and its resolution, the server supports 3 formats (avi, mp4, mkv)
and 5 resolutions (240p, 360p, 480p, 720p, 1080p). Both the formats and the resolutions are extendable just by adding the
new format/resolution to the appropriate enum (and its functions). The server will not create greater resolutions than
those that are present.

After the first step the server will start waiting for clients to connect. When a client connects to the server it must
inform the server about its connection speed (in kbps) and the video format they want to watch, then the server
will send a list of videos that match the desirable format and resolutions depending on the client's internet speed.

Afterwards the server waits for the client to pick a video and a communication protocol (TCP, UDP, RTP/UDP).

Finally the server starts the video streaming by creating a new process to run the `ffmpeg` binary.

In order for the RTP/UDP protocol to work the server will start a second server socket on a different thread that will
act as a simple file server to send to the clients the generated sdp file for their stream.

## Streaming Client

When the client starts a five seconds internet speed test will begin, after the speed test the main window will be
available to the user.

Afterwards the user must select a file format, then a list of videos will be available.

When the user picks a video and a communication protocol the client will start playing the stream using the `ffplay` executable.

## Notes

- The server is capable of serving multiple clients at once.
- Both the server and the client assume that the ffmpeg and the ffplay binaries respectively are in the path.
- The log4j xml config files don't scale, as it is now I have add each class I want to have a logger to the config file.
This is because I couldn't find a way to stop JavaFX from logging stuff.
