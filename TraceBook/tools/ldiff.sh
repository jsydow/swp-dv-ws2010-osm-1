#!/bin/sh

LANGUAGES="de en fr gr pl tr"

PROJECTDIR="$HOME/development/TraceBook"
BASEFILE="res/values/strings.xml"

if [ "x$USER" != "xdd" ]
then
    PROJECTDIR="."
fi

if [ ! -r "$PROJECTDIR/$BASEFILE" ]
then
    echo "$PROJECTDIR/$BASEFILE" does not exist.
    exit 1
fi

mksort() {
    BASEFILE=$1
    TMPFILE=$2

    if [ -r "$BASEFILE" ]
    then
        grep 'string name="' "$BASEFILE" | sed -e 's/>.*//g' -e 's/.*name=//g' -e 's/"//g' | sort -f | uniq > $TMPFILE
    fi
}

BASETMP=`mktemp`

mksort "$PROJECTDIR/$BASEFILE" "$BASETMP"
for LANG in $LANGUAGES
do
    NEWFILE="$PROJECTDIR/res/values-$LANG/strings.xml"

    if [ -r $NEWFILE ]
    then
        NEWTMP=`mktemp`

        mksort "$NEWFILE" "$NEWTMP"

        echo "For $NEWFILE:"
        diff -w -U 0 "$NEWTMP" "$BASETMP" | grep -v '\-\-\-' | grep -v '+++' | grep ^\[+-\]
        echo

        rm -f $NEWTMP
    fi
done

rm -f $BASETMP
