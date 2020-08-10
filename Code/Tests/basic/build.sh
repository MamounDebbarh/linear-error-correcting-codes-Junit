#!/bin/bash
JUNITPATH=$TESTDIR/../lib
FILES=$(find . -name '*.java' )
   javac -cp $JUNITPATH/junit-platform-commons.jar:$JUNITPATH/junit-jupiter-api.jar:$JUNITPATH/apiguardian-api-1.0.0.jar:. $FILES $TESTDIR/ECCPublicTests.java
