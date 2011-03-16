module OsmMapFeatures
    require 'net/http'
    require 'rubygems'
    require 'builder'

    @tags = Hash.new
    @descriptions = Hash.new
    @flags = Hash.new
    @parse_usefuls = true

    @number_regexps = [
        /^Nombre$/i,
        /^Num$/i,
        /^Number$/i,
        /^Zahl$/i,
        /^-?[0-9]+$/,
        /^[0-9]+-[0-9]+$/,
    ]

    @anytext_regexps = [
        /\s+/,
    ]

    @blacklists = Hash.new
    @blacklists[:keys] = [
        "Hilfe für Kartennutzer"
    ]

    def parse_keys_and_values(lang = '')
        f = get_page("http://wiki.openstreetmap.org/wiki/#{lang}Map_Features")
        tags = f.scan(/<tr.*?>\s+<td.*?>(.*?)<\/td><td.*?>(.*?)<\/td><td.*?>(.*?)<\/td><td.*?>(.*?)<\/td>/m)

        tags.each do |tag|
            key = tag[0].gsub(/<.*?>/, '').strip
            value = tag[1].gsub(/<.*?>/, '').strip
            value_details = tag[1].strip
            flags = tag[2].strip
            description = tag[3].gsub(/<.*?>/, '').strip

            @tags[key] ||= Hash.new
            @flags[key] ||= Hash.new
            @descriptions[key] ||= Hash.new

            @tags[key][value] = value_details
            @flags[key][value] = Hash.new
            @descriptions[key][value] = description

            %w{ Node Way Area }.each do |item|
                @flags[key][value]["is#{item}"] = (flags.match(/title="#{item}"/) ? 'true' : 'false')
            end
        end
    end

    def p_xml(lang = '')
        xml = Builder::XmlMarkup.new(:target => STDOUT, :indent => 2)
        puts '<?xml version="1.0" encoding="utf-8" ?>'
        xml.map_features(:lang  => lang.downcase.sub(':', ''),
                         :xmlns => "http://code.google.com/p/swp-dv-ws2010-osm-1/OSM_Tags") do
            @tags.keys.sort.each do |key|
                next if is_blacklisted?(key, @blacklists[:keys]);

                xml.key(:v => key.gsub(/%/, '').gsub(/&.+?/, '').strip) do
                    @tags[key].keys.sort.each do |value|
                        empty_value_has_been_printed_before = false; 

                        (value == '' ? 'User defined' : value).split(/[\/,]/).sort.each do |v|
                            next if v.match(/\.\.\./)
                            v = v.gsub(/[%°]/, '').gsub(/&.+?/, '').strip
                            
                            value_type = (is_number?(v) ? 'number' : 'text')

                            if (value_type == 'number')
                            	v = '' 
                            else
                            	v = (is_any_text?(v) ? '' : v)
                            end

                            if (v == '')
                                next if empty_value_has_been_printed_before
                                empty_value_has_been_printed_before = true
                            end

                            xml.value(@flags[key][value].merge({:type => value_type, :v => v})) do
                                xml.description(@descriptions[key][value].gsub(/\s+/, ' ').gsub('&', '&amp;'))

                                if (not @tags[key][value].match(/class="new"/))
                                    href = @tags[key][value].scan(/href="(.*?)"/).to_s.strip
                                    xml.uri("http://wiki.openstreetmap.org#{(lang == '' ? href : href.gsub(/Tag:/, "#{lang}Tag:"))}") if href != ''

                                    if (@parse_usefuls and href != '')
                                        useful_tags = get_useful_tags("http://wiki.openstreetmap.org#{href.to_s.strip}")
                                        
                                        useful_tags.sort.each do |useful_tag|
                                            xml.useful(:v => useful_tag) unless (useful_tag == '' or useful_tag.match(/ /))
                                        end
                                    end
                                end
                            end
                        end
                    end
                end
            end
        end
    end
    
    def is_blacklisted?(s, blacklist)
    	blacklist.each { |blacklisted_item| return true if s.match(blacklisted_item) }
    	false
    end
    
    def is_any_text?(s, anytext_regexps = @anytext_regexps)
		anytext_regexps.each { |regexp| return true if s.match(regexp) }        
        false    	
    end
    
    def is_number?(s, number_regexps = @number_regexps)    	
    	number_regexps.each { |regexp| return true if s.match(regexp) }        
        false
    end
    
    def get_useful_tags(uri)
    	useful_page = get_page(uri)
    	useful_tags = useful_page.scan(/<dl><dt>Useful combination(.*?)<dl><dt>/m).to_s.scan(/<li>(.*?)<\/li>/m)
    	
    	useful_tags.map { |x| x.to_s.gsub(/<.*?>/m, '').gsub(/=.*/, '').gsub(/Key:/i, '').strip }
    end
    
    def get_page(uri)
        Net::HTTP.get(URI.parse(uri))
    end
end
