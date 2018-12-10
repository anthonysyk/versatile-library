#!/usr/bin/env bash

CURR_VERSION=$(cat version)
RELEASE_VERSION=$(sed 's/-.*//' version)
NEXT_VERSION=$(echo "$(sed 's/-.*//' version) + 0.01" | bc -l)-SNAPSHOT

echo $CURR_VERSION
echo $RELEASE_VERSION
echo $NEXT_VERSION

echo "==== STEP Update Project Version"
git checkout master
git pull --rebase master
echo "$RELEASE_VERSION" > 'version'
git add .
git commit -m "Setting version to $(echo $RELEASE_VERSION)" && git branch && git status
git pull --rebase master
git push
git tag | xargs git tag -d
git tag -a v$RELEASE_VERSION -m "Releasing $(echo $RELEASE_VERSION)"
git push --tags

echo "==== STEP Update Project Version to SNAPSHOT"
git checkout master
echo "$NEXT_VERSION" > 'version'
git add .
git commit -m "Setting version to $(echo $NEXT_VERSION)" && git branch && git status
git pull --rebase master
git push
