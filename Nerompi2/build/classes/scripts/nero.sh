#!/bin/bash

CLASSPATH="."
for jar in $(find lib -iname "*.jar")
do
	CLASSPATH="${CLASSPATH}:${jar}"
done

export CLASSPATH="${CLASSPATH}:nero.jar"

java fi.helsinki.cs.nero.NeroApplication $@

