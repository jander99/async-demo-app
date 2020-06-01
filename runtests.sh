#!/usr/bin/env bash

# shellcheck disable=SC2054
# shellcheck disable=SC2039
# shellcheck disable=SC2034
declare -a async_tests=("AsyncAsyncSimulation" "AsyncAsyncPooledSimulation" "AsyncParallelSimulation" "AsyncSequentialSimulation")
declare -a pooled_tests=("AsyncPooledAsyncPooledSimulation" "AsyncPooledAsyncSimulation" "AsyncPooledParallelSimulation" "AsyncPooledSequentialSimulation")
declare -a parallel_tests=("ParallelAsyncPooledSimulation" "ParallelAsyncSimulation" "ParallelParallelSimulation" "ParallelSeqentialSimulation")
declare -a sequential_tests=("SequentialAsyncPooledSimulation" "SequentialAsyncSimulation" "SequentialParallelSimulation" "SequentialSequentialSimulation")

runTest() {
  type=$1
  path=$2
  gatling_cmd="./gradlew gatlingRun-$1.$2 --rerun-tasks"
  echo "Running Gatling for ${type} ${path}"
  eval "nice sh $gatling_cmd > /dev/null 2>&1"
}

echo "Cleaning up from previous tests"
eval "./gradlew clean"
PROJECT_DIR=$(pwd)
SIMULATION_PATH="${PROJECT_DIR}/build/reports/gatling"
rm -rf "${SIMULATION_PATH}"

if [ "$1" = "async" ]; then
  echo "Executing async tests"
  for i in "${async_tests[@]}"
  do
    runTest "async" $i
  done
elif [ "$1" = "pooled" ]; then
  echo "Executing pooled async tests"
  for i in "${pooled_tests[@]}"
  do
    runTest "pooled" $i
  done
elif [ "$1" = "parallel" ]; then
  echo "Executing parallel tests"
  for i in "${parallel_tests[@]}"
  do
    runTest "parallel" $i
  done
elif [ "$1" = "sequential" ]; then
  echo "Executing sequential tests"
  for i in "${sequential_tests[@]}"
  do
    runTest "sequential" $i
  done
else
  echo "I'm sorry, I didn't understand your command."
  exit 1
fi

./gatling-stitch.sh "$1"