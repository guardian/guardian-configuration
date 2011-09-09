#!/bin/sh
if test -f ~/.sbtconfig; then
  . ~/.sbtconfig
fi
exec java -Xmx2048M ${SBT_OPTS} -jar ./dev/sbt-launch-0.10.1.jar "$@"
