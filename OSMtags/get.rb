#!/usr/bin/ruby

load 'OsmMapFeatures.rb'
include OsmMapFeatures

lang = ''
if (ARGV[0] && ARGV[0] != 'EN')
    lang = "#{ARGV[0]}:"
end

OsmMapFeatures.parse_keys_and_values(lang)
OsmMapFeatures.p_xml(lang)