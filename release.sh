#!/bin/bash

version=$(grep 'mod_version = ' "./gradle.properties" | cut -d'=' -f2 | tr -d ' ')

git tag -s -a v"$version" -m "$1"

git push origin main v"$version"