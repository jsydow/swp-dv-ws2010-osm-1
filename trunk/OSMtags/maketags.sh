#!/bin/sh

########################################################################
 #
 # This file is part of TraceBook.
 #
 # TraceBook is free software: you can redistribute it and/or modify it
 # under the terms of the GNU General Public License as published by the
 # Free Software Foundation, either version 3 of the License, or (at
 # your option) any later version.
 #
 # TraceBook is distributed in the hope that it will be useful, but
 # WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 # General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with TraceBook. If not, see <http://www.gnu.org/licenses/>.
 #
########################################################################

LANG="EN DE FR Tr Pl"

for L in $LANG ; do
    ./get.rb $L > xml/tags_`echo $L | tr A-Z a-z`.xml
    echo Done parsing `echo $L`.
done
tar cjvf xml.tar.bz2 xml && scp xml.tar.bz2 hdinh@login.zedat.fu-berlin.de:public_html/
cp xml/tags_*.xml $HOME/development/TraceBook/res/raw/
