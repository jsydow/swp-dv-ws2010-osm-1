#!/bin/sh

if [ $# -eq 0 ]
then
    echo "USAGE: ldiff.sh LANG"
    echo "  LANG is the 2-letter language code representation."
    exit 1
fi

OLDFILE="res/values/strings.xml"
NEWFILE="res/values-$1/strings.xml"

if [ ! -r $OLDFILE ]
then
    echo $OLDFILE does not exist.
    exit 2
fi

if [ ! -r $NEWFILE ]
then
    echo $NEWFILE does not exist.
    exit 3
fi

OLDTMP=`mktemp`
NEWTMP=`mktemp`

grep 'string name="' "$OLDFILE" | sed -e 's/>.*//g' -e 's/.*name=//g' -e 's/"//g' | sort -u > $OLDTMP
grep 'string name="' "$NEWFILE" | sed -e 's/>.*//g' -e 's/.*name=//g' -e 's/"//g' | sort -u > $NEWTMP

diff -w -U 0 $OLDTMP $NEWTMP | grep ^\[+-\]
rm -f $OLDTMP $NEWTMP
