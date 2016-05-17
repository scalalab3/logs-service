#!/bin/env bash

sbt assembly main/assembly
java -jar target/scala-2.11/main-assembly-0.0.1.jar
