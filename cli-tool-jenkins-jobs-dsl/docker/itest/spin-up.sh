#!/usr/bin/env bash

# build image
docker build --rm -t cli-tool:centos7 .

# bring up containers
docker-compose up -d