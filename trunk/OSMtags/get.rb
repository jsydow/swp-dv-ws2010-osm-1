#!/usr/bin/ruby

load 'OsmMapFeatures.rb'
include OsmMapFeatures

OsmMapFeatures.settings = { :language => ARGV[0] }
OsmMapFeatures.parse
OsmMapFeatures.parse_useful_tags
puts OsmMapFeatures.to_xml
