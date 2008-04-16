#!/bin/bash -ex
#
# Script that Kohsuke uses to push the new version to the web
mvn -B release:prepare || mvn install
mvn -B release:prepare release:perform
dt=$(date +%Y%m%d)
javanettasks uploadFile poormans-installer / "$dt release" Stable build/installer-builder-$dt.zip
