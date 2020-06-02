#!/usr/bin/env bash


echo "Running all tests"

./gradlew gatlingRun-core.WarmupSimulation --rerun-tasks

./runtests.sh async
./runtests.sh pooled
./runtests.sh parallel
# ./runtests.sh sequential
