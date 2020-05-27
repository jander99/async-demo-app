#!/usr/bin/env bash

# Set the current Gatling installation directory
# This should probably an envvar

if [ -z "$GATLING_HOME" ]; then
  echo "Need to set Gatling Home directory"
  exit 1
else
  echo "Gatling installed in ${GATLING_HOME}"
fi

SIMULATION_PATH="./build/reports/gatling"
REPORT_AGG_PATH="./gatling-reports"

# Check if the Simulation Path exists
# If it does not exist, then exit since there's
# nothing else we can really do.
if [ ! -d ${SIMULATION_PATH} ]; then
    echo "Simulation Path not found"
    exit 1
fi

# Check if the Reports aggregation path exists
# If not, create it.
if [ ! -d ${REPORT_AGG_PATH} ]; then
  echo "Report Aggregation Path does not exist. Creating."
    mkdir ${REPORT_AGG_PATH}
fi

# Build find command to find all simulation.log files
# Need to have full directory listing on them
FIND_CMD="find "$(pwd)" -name simulation.log"



# For each found simulation file,
# 1. Get last directory name
# 2. Copy and prepend the directory name to simulation.log
# 3. Move it to REPORT_AGG_PATH



# Try to run the gatling magic that will stitch the simulations
# into one report. Might not work on Windows/bash for...reasons?

