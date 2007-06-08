#!/bin/bash -ex
#
# Script that Kohsuke uses to push the new version to the web
ant clean release
dt=$(date +%Y%m%d)
jnupload poormans-installer / "$dt release" Stable build/installer-builder-$dt.zip
