#!/usr/bin/env bash


echo "Running all tests"

./runtests.sh async
./runtests.sh pooled
./runtests.sh parallel
./runtests.sh sequential
