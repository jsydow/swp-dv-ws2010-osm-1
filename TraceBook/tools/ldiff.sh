#!/bin/sh

OLDFILE=$1
NEWFILE=$2

OLDTMP=`mktemp`
NEWTMP=`mktemp`

grep 'string name="' "$OLDFILE" | sed -e 's/>.*//g' -e 's/.*name=//g' -e 's/"//g' | sort -u > $OLDTMP
grep 'string name="' "$NEWFILE" | sed -e 's/>.*//g' -e 's/.*name=//g' -e 's/"//g' | sort -u > $NEWTMP

diff -w -U 0 $OLDTMP $NEWTMP
rm -f $OLDTMP $NEWTMP
