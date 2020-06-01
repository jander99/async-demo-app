#!/usr/bin/env bash

# Set the current Gatling installation directory
# This should probably an envvar
PROJECT_DIR=$(pwd)

GATLING_DL_LOC="https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/3.3.1/gatling-charts-highcharts-bundle-3.3.1-bundle.zip"
GATLING_ZIP_FILE="gatling-charts-highcharts-bundle-3.3.1-bundle.zip"
GATLING_TMP_LOC="${PROJECT_DIR}/tmp/gatling"
GATLING_TMP_REPORTS_LOC="${GATLING_TMP_LOC}/reports"

function download_tmp_gatling() {
  if [ ! -d "${GATLING_TMP_LOC}" ]; then
    echo "Temp Locations doesn't exist, creating"
    mkdir -p "${GATLING_TMP_LOC}"
  fi

  cd "${GATLING_TMP_LOC}" || exit 1

  if [ ! -f "${GATLING_ZIP_FILE}" ]; then
    echo "Gatling zip file not found, downloading."
    curl -O ${GATLING_DL_LOC} >/dev/null 2>&1
    unzip ${GATLING_ZIP_FILE} -d . >/dev/null 2>&1
  fi
  echo "$(pwd)/gatling-charts-highcharts-bundle-3.3.1"
}

if [ -z "$GATLING_HOME" ]; then
  echo "Gatling Home not set, creating temporary version"
  GATLING_HOME=$(download_tmp_gatling)
  cd "${PROJECT_DIR}"
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
  exit 1
fi

rm -rf "${REPORT_AGG_PATH}"
mkdir -p "${REPORT_AGG_PATH}"

if [ ! -d "${GATLING_TMP_REPORTS_LOC}" ]; then
  echo "Creating temporary reports location"
  mkdir -p "${GATLING_TMP_REPORTS_LOC}"
fi

# Build find command to find all simulation.log files
# Need to have full directory listing on them
FIND_CMD="find ${SIMULATION_PATH} -name simulation.log"

for file in $(${FIND_CMD}); do
  sim_name=$(basename "$(dirname "${file}")" | cut -d- -f1)
  filename=$(basename "${file}")
  cp "${file}" "${REPORT_AGG_PATH}/${sim_name}-$filename" >/dev/null 2>&1
done

echo "Aggregating Reports"
eval "$GATLING_RUNNER -ro reports"

if [ ! -d "${GATLING_TMP_REPORTS_LOC}" ]; then
  mkdir "${GATLING_TMP_REPORTS_LOC}"
fi


if [ ! -z "$1" ]; then
  echo "Detected named execution $1"
  eval "rsync -a ${REPORT_AGG_PATH}/* ${GATLING_TMP_REPORTS_LOC}/$1/"
  # eval "mv -f ${GATLING_TMP_REPORTS_LOC}/index.html ${GATLING_TMP_REPORTS_LOC}/$1/index.html"
fi

