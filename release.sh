#!/bin/bash -ex
#
# Script that Kohsuke uses to push the new version to the web
mvn -B release:prepare || mvn install
mvn -B release:prepare release:perform
dt=$(date +%Y%m%d)
id=$(show-pom-version target/checkout/pom.xml)
javanettasks uploadFile poormans-installer /installer-builder-$id.jar "$dt release" Stable target/checkout/installer-builder/target/installer-builder-*.jar
