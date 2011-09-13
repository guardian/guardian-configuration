#!/bin/sh
if test -f ~/.sbtconfig; then
  . ~/.sbtconfig
fi
exec java -Dfile.encoding=UTF8 -Xmx2048M -XX:+CMSClassUnloadingEnabled -XX:+UseCompressedOops -XX:MaxPermSize=512m \
        -jar ./dev/sbt-launch-0.10.1.jar "$@"
