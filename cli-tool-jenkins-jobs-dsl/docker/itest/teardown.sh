#!/usr/bin/env bash

# spin down containers
docker-compose down

no_tag=$(docker images -qf "dangling=true");
if [ -n "$no_tag" ]; then
    echo "# Removing old cli-tool test environment images"
    echo "$no_tag"
    docker rmi ${no_tag}
fi

# TODO Do I need to clean up anything? images, volumes, networks