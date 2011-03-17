#!/bin/sh

LANG="EN DE FR Tr Pl"

for L in $LANG ; do
    ./get.rb $L > xml/tags_`echo $L | tr A-Z a-z`.xml
    echo Done parsing `echo $L`.
done
tar cjvf xml.tar.bz2 xml && scp xml.tar.bz2 hdinh@login.zedat.fu-berlin.de:public_html/
cp xml/tags_*.xml $HOME/development/TraceBook/res/raw/
