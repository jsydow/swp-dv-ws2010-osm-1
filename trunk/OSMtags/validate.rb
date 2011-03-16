#!/usr/bin/ruby

exit 1 unless ARGV[0]

require 'rubygems'
require 'xml'

schema_file = 'OSM_tags.xsd'

document = LibXML::XML::Document.file(ARGV[0])
schema = LibXML::XML::Schema.new(schema_file)

document.validate_schema(schema) do |message, flag|
	puts message
	exit 2
end

puts "#{ARGV[0]} successfully validated against #{schema_file}!"
