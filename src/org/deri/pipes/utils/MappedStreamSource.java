package org.deri.pipes.utils;

import javax.xml.transform.stream.StreamSource;

/**
 * A StreamSource having a key and description. The key is used to
 * store/retrieve the StreamSource in a map. The description is used
 * for display.
 *
 */
public class MappedStreamSource {
	private String key;
	private String Description;
	private String location;
	/**
	 * Get the key for this StreamSource.
	 * @return
	 */
	public String getKey() {
		return key;
	}
	/**
	 * Set the key for this StreamSource. Once set the key should
	 * not be changed.
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * Set the StreamSource description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * Set the description of the StreamSource.
	 * @param description
	 */
	public void setDescription(String description) {
		Description = description;
	}
	/**
	 * Get the url of the stream source.
	 * @return the url location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * Set the location of the stream.
	 * @param location url containing the stream
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * Get the StreamSource for the given location.
	 * @return
	 */
	public StreamSource getStreamSource(){
		return new StreamSource(location);
	}
	
	public static MappedStreamSource newInstance(String key, String location,String description){
		MappedStreamSource x = new MappedStreamSource();
		x.setKey(key);
		x.setLocation(location);
		x.setDescription(description);
		return x;
	}
}
