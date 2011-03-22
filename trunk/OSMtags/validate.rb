#!/usr/bin/ruby

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

exit 1 unless ARGV[0]

require 'rubygems'
require 'xml'

schema_file = 'OSM_tags.xsd'

document = LibXML::XML::Document.file(ARGV[0])
schema = LibXML::XML::Schema.new(schema_file)

document.validate_schema(schema) do |message|
	puts message
	exit 2
end

puts "#{ARGV[0]} successfully validated against #{schema_file}!"
