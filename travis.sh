#!/bin/bash

if [[ ${TRAVIS_BRANCH} == 'master' || ${TRAVIS_BRANCH} == 'develop' || ${TRAVIS_BRANCH} == '2.1.1-paged' ]]
then
  echo "Deploying Jar to Maven Central"
  mvn deploy -DskipTests --settings settings.xml -Prelease
else
  echo "Not deploying jar"
fi
