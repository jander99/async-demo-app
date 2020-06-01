#!/usr/bin/env sh

CURR_DIR=$(pwd)

ulimit -Sn 10000

cd server || exit 1
go build
nice ./goserver

cd "${CURR_DIR}" || exit 1