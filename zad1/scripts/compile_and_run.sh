#!/bin/bash

mvn clean install

javac -cp "model/target/classes:service/target/classes" App.java

java -cp "model/target/classes:service/target/classes" App.java