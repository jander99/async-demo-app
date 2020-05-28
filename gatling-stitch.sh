#!/usr/bin/env bash

# Set the current Gatling installation directory
# This should probably an envvar
PROJECT_DIR=$(pwd)

GATLING_DL_LOC="https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/3.3.1/gatling-charts-highcharts-bundle-3.3.1-bundle.zip"
GATLING_ZIP_FILE="gatling-charts-highcharts-bundle-3.3.1-bundle.zip"
GATLING_TMP_LOC="${PROJECT_DIR}/tmp/gatling"


function download_tmp_gatling() {
  if [ -d "${GATLING_TMP_LOC}" ]; then
    rm -rf "${GATLING_TMP_LOC}"
  fi
  mkdir -p "${GATLING_TMP_LOC}"
  cd "${GATLING_TMP_LOC}" || exit 1
  curl -O ${GATLING_DL_LOC} >/dev/null 2>&1
  if [ $? -eq 0 ]; then
    unzip ${GATLING_ZIP_FILE} -d . >/dev/null 2>&1
    echo "$(pwd)/gatling-charts-highcharts-bundle-3.3.1"
  else
    echo "Unable to download Gatling"
    exit 1
  fi
  cd "$PROJECT_DIR" || exit
}

if [ -z "$GATLING_HOME" ]; then
  echo "Gatling Home not set, downloading temporary version"
  GATLING_HOME=$(download_tmp_gatling)
fi

echo "Gatling installed in ${GATLING_HOME}"

GATLING_RUNNER="${GATLING_HOME}/bin/gatling.sh"
SIMULATION_PATH="${PROJECT_DIR}/build/reports/gatling"
REPORT_AGG_PATH="${GATLING_HOME}/results/reports"

# Check if the Simulation Path exists
# If it does not exist, then exit since there's
# nothing else we can really do.
if [ -d "${SIMULATION_PATH}" ]; then
  echo "Using Simulation Path ${SIMULATION_PATH}"
else
  echo "Simulation Path ${SIMULATION_PATH} not found"
fi

# Check if the Reports aggregation path exists
# If not, create it.
if [ -d "${REPORT_AGG_PATH}" ]; then
  echo "Report Aggregation Path exists. Removing and Recreating"
  rm -rf "${REPORT_AGG_PATH}"
fi
mkdir "${REPORT_AGG_PATH}"

# Build find command to find all simulation.log files
# Need to have full directory listing on them
FIND_CMD="find ${SIMULATION_PATH} -name simulation.log"

for file in $(${FIND_CMD}); do
  sim_name=$(basename $(dirname "${file}") | cut -d- -f1)
  filename=$(basename "${file}")
  cp "${file}" "${REPORT_AGG_PATH}/${sim_name}-$filename" >/dev/null 2>&1
done

echo "Aggregating Reports"
exec $GATLING_RUNNER -ro reports

open ${REPORT_AGG_PATH}/reports/index.html

