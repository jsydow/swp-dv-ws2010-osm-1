#!/bin/sh

LANG="EN DE FR TR Pl"

for L in $LANG ; do
    ./get.rb $L > xml/tags.$L.xml
    echo Done parsing $L.
done
tar cjvf xml.tar.bz2 xml && scp xml.tar.bz2 hdinh@login.zedat.fu-berlin.de:public_html/
