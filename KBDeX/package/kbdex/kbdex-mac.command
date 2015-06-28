#!/bin/sh

cd `dirname $0`
java -Djsse.enableSNIExtension=false -Xmx1024m -jar kbdex.jar

