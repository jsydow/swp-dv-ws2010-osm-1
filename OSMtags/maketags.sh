#!/bin/sh

for L in EN DE FR Tr Pl Vi; do
    ./tags.rb $L > xml/tags.$L.xml
    echo Done parsing $L.
done
tar cjvf xml.tar.bz2 xml && scp xml.tar.bz2 hdinh@login.zedat.fu-berlin.de:public_html/
