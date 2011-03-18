module OsmMapFeatures
    require 'net/http'
    require 'rubygems'
    require 'builder'

    @number_regexps = [
        /^Nombre$/i,
        /^Num$/i,
        /^Number$/i,
        /^Zahl$/i,
        /^-?[0-9]+$/,
        /^[0-9]+-[0-9]+$/,
    ]

    @any_text_regexps = [
        /\s+/,
        /^\*$/
    ]

    @flags = %w{ Node Way Area }

    @data = Hash.new
    @language = 'EN'
    @base_uri = 'http://wiki.openstreetmap.org'
    @cache_dir = 'cache/'

    private

    def remove_html_tags(s)
        s.gsub(/<.*?>/, '')
    end

    def get_useful_tags(key, value)
        return if (value == '')
    	useful_page = get_page("Tag:#{key}%3D#{value}", false)
    	useful_tags = useful_page.scan(/<dl><dt>Useful combination(.*?)<dl><dt>/m).to_s.scan(/<li>(.*?)<\/li>/m)

    	useful_tags.map { |x| x.to_s.gsub(/<.*?>/m, '').gsub(/=.*/, '').gsub(/Key:/i, '').strip }
    end

    def get_real_value(value)
        value = (value ? value.gsub(/[%°]/, '').gsub(/&.+?/, '').strip : '')
        value = '' if (is_any_text?(value) or is_number?(value) or value.match(/\.\.\./))

        value
    end

    def get_image_uri(a)
        uri = ''

        if (a and a.match(/<img.*?src=".*?".*?\/>/))
            uri = a.scan(/<img.*?src="(.*?)".*?\/>/).to_s.strip
            uri = @base_uri + uri unless (uri.match(/^http/))
        end

        uri
    end

    def get_value_type(value)
        is_number?(get_real_value(value)) ? 'number' : 'text'
    end

    def get_page(page, add_language_to_uri = true)
        uri = URI.parse(get_uri(page, add_language_to_uri))
        cache_path = @cache_dir + uri.host + uri.path.gsub(/[%:]/, '_')

        if (File.exists?(cache_path) and (Time.now.strftime('%s').to_i < File.mtime(cache_path).strftime('%s').to_i + 604800))
            File.open(cache_path) { |fp| fp.readlines.join("\n") }
        else
            content = Net::HTTP.get(uri)
            cache_dir = File.dirname(cache_path)

            dir_parts = []
            cache_dir.split('/').each do |dir_name|
                dir_parts << dir_name
                tmp_dir = dir_parts.join('/')
                Dir.mkdir(tmp_dir) unless File.directory?(tmp_dir)
            end
            File.open(cache_path, 'w') { |fp| fp.puts(content) }

            content
        end
    end

    def get_page_name(a)
        page = ''

        if (not(a.match(/class="new"/)) and a.match(/href="\/wiki\//))
            page = a.scan(/href="\/wiki\/.*?:?(.*?)"/).to_s.strip
        end

        page
    end

    def get_uri(page, add_language_to_uri = true)
        base_uri  = "#{@base_uri}/wiki/"
        base_uri += "#{@language}:" if (add_language_to_uri and (@language != 'EN'))
        base_uri += page

        URI.escape(base_uri).gsub('%25', '%')
    end

    def is_any_text?(s)
		@any_text_regexps.each { |regexp| return true if (s and s.match(regexp)) }
        false
    end

    def is_number?(s)
    	@number_regexps.each { |regexp| return true if (s and s.match(regexp)) }
        false
    end

    public

    def settings=(opts = {})
        opts.keys.each do |key|
            instance_variable_set("@#{key.to_s}".to_sym, opts[key])
        end
    end

    def parse_useful_tags
        @data.keys.sort.each do |key|
            @data[key].keys.sort.each do |value|
                @data[key][value]['useful'] = get_useful_tags(key, value)
            end
        end
    end

    def to_xml
        result = ''
        xml = Builder::XmlMarkup.new(:target => result, :indent => 2)

        xml.map_features(:lang  => @language.downcase,
                         :xmlns => "http://code.google.com/p/swp-dv-ws2010-osm-1/OSM_Tags") do
            @data.keys.sort.each do |key|
                xml.key(:v => @data[key]['attributes'][:v]) do
                    @data[key].keys.sort.each do |value|
                        next if (value == 'attributes')
                        xml.value(@data[key][value]['attributes']) do
                            xml.uri(@data[key][value]['uri'])
                            xml.img(@data[key][value]['img'])
                            xml.description(@data[key][value]['description'])
                            if (@data[key][value]['useful'])
                                @data[key][value]['useful'].each { |useful_tag| xml.useful(:v => useful_tag) }
                            end
                        end
                    end
                end
            end
        end

        '<?xml version="1.0" encoding="utf-8" ?>' + "\n" + result
    end

    def parse
        page = get_page('Map_Features')
        entries = page.scan((/<tr.*?>\s+<td.*?>(.*?)<\/td><td.*?>(.*?)<\/td><td.*?>(.*?)<\/td><td.*?>(.*?)<\/td><td.*?>.*?<\/td><td.*?>(.*?)<\/td>/m))

        entries.each do |entry|
            key = remove_html_tags(entry[0]).strip
            next if (key.match(/\s+/))
            value = remove_html_tags(entry[1]).strip
            value = 'User defined' if (value == '')
            flags = entry[2].strip
            description = remove_html_tags(entry[3]).gsub(/[ ]+/, ' ').strip
            img = get_image_uri(entry[4])

            @data[key] ||= Hash.new
            @data[key]['attributes'] ||= { :v => key }

            value.split(/[\/,]/).sort.each do |v|
                v = get_real_value(v)

                @data[key][v] ||= Hash.new
                @data[key][v]['attributes'] ||= { :v => v }
                @data[key][v]['attributes']['type'] ||= get_value_type(v)
                @flags.each do |item|
                    @data[key][v]['attributes']["is#{item}"] = (flags.match(/title="#{item}"/) ? 'true' : 'false')
                end

                @data[key][v]['description'] ||= description
                @data[key][v]['uri'] = get_uri(get_page_name(entry[1])) unless (get_page_name(entry[1]) == '')
                @data[key][v]['img'] = img unless (img == '')
            end
        end
    end
end