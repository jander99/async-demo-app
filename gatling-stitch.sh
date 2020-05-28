#!/usr/bin/env bash

# Set the current Gatling installation directory
# This should probably an envvar

GATLING_DL_LOC="https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/3.3.1/gatling-charts-highcharts-bundle-3.3.1-bundle.zip"
GATLING_ZIP_FILE="gatling-charts-highcharts-bundle-3.3.1-bundle.zip"
GATLING_TMP_LOC="./tmp/gatling"

CURR_DIR=$(pwd)

function download_tmp_gatling() {
  echo "Downloading Gatling binary to ./tmp/gatling"
  if [ ! -d "${GATLING_TMP_LOC}" ]; then
    mkdir -p ${GATLING_TMP_LOC} && cd ${GATLING_TMP_LOC} || exit 1
  fi
  curl -O ${GATLING_DL_LOC} > /dev/null 2>&1
  if [ $? -eq 0 ]; then
    unzip ${GATLING_ZIP_FILE} -d .
#    GATLING_BIN=$(find )
  else
    echo "Unable to download Gatling"
  fi
}





if [ -z "$GATLING_HOME" ]; then
  echo "Gatling Home not set, downloading temporary version"
  download_tmp_gatling
else
  echo "Gatling installed in ${GATLING_HOME}"
fi

SIMULATION_PATH="${CURR_DIR}/build/reports/gatling"
REPORT_AGG_PATH="${CURR_DIR}/gatling-reports"

# Check if the Simulation Path exists
# If it does not exist, then exit since there's
# nothing else we can really do.
if [ ! -d "${SIMULATION_PATH}" ]; then
    echo "Simulation Path ${SIMULATION_PATH} not found"
fi

# Check if the Reports aggregation path exists
# If not, create it.
if [ ! -d "${REPORT_AGG_PATH}" ]; then
  echo "Report Aggregation Path does not exist. Creating."
    mkdir "${REPORT_AGG_PATH}"
fi

# Build find command to find all simulation.log files
# Need to have full directory listing on them
FIND_CMD="find ${SIMULATION_PATH} -name simulation.log"

for filename in $("${FIND_CMD}"); do
  echo "$filename"
done



# For each found simulation file,
# 1. Get last directory name
# 2. Copy and prepend the directory name to simulation.log
# 3. Move it to REPORT_AGG_PATH



# Try to run the gatling magic that will stitch the simulations
# into one report. Might not work on Windows/bash for...reasons?

