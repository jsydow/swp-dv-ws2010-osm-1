package core.data.db;

/**
 *
 * 
 */
public class TagSearchResult {
    private String key;
    private String value;
    private String name;
    private String description;
    private String link;
    private String image;
    private String valueType;
    private String language;

    /**
     * @param key
     *            The key part of the tag.
     * @param value
     *            The value part of the tag.
     * @param name
     *            The name of the text in the language specified by language.
     * @param description
     *            The description of the tag.
     * @param link
     *            The link to the
     *            http://wiki.openstreetmap.org/wiki/Map_Features article
     * @param image
     *            The resource id of the image which can be used for.
     * @param valueType
     *            The type of the value which can be number, string etc.
     * @param language
     *            The language in which the tag is described.
     */
    TagSearchResult(String key, String value, String name, String description,
            String link, String image, String valueType, String language) {
        this.key = key;
        this.value = value;
        this.name = name;
        this.description = description;
        this.link = link;
        this.image = image;
        this.valueType = valueType;
        this.language = language;
    }

    /**
     * Getter method.
     * 
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter method.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter method.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter method.
     * 
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Getter method.
     * 
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Getter method.
     * 
     * @return the valueType
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Getter method.
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }
}
