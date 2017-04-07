# NodeExample
This is an example Android app that uses Node.js. 
The application prints "Hello world!" message to logcat from node.
Additionally, it outputs the same message through http. The http server uses non-standard port 8080.

The github project doesn't contain Node.js shared library. You have to build it yourself and update the path in CMakeScripts, so that the build system can package it into the final .apk.

This example is related to my [blogpost](https://www.sisik.eu/blog/embedding_node_into_android_app) about embedding Node.js into Android apps.

Feel free to use and edit according to your liking.
