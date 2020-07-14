/*
 * Jester Game Engine is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Jester Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author: orochimaster
 * @email: orochimaster@yahoo.com.br
 */
package com.jge.server;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * The regional options to be followed by this server
 * 
 */
public class RegionalOptions implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Gets the singleton instance of {@link RegionalOptions}
	 * @return the singleton instance of {@link RegionalOptions}
	 */
	public static RegionalOptions get() {
		if (instance == null) {
			instance = new RegionalOptions();
		}
		return instance;
	}
	
	/**
	 * The singleton instance
	 */
	private static RegionalOptions instance;
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Country option
	 */
	private String country = "EN";
	
	/**
	 * Language option
	 */
	private String language = "en";
	
	/**
	 * Timezone Option
	 */
	private TimeZone timezone = TimeZone.getTimeZone("UTC-0");
	
	/**
	 * Private instance for singleton
	 */
	private RegionalOptions() {
	}

	/**
	 * Gets the country option
	 * @return the country option
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Sets the country option
	 * @param country the country option
	 * @return the country option
	 */
	public RegionalOptions setCountry(String country) {
		this.country = country;
		return this;
	}
	
	/**
	 * Gets the language option
	 * @return the language option
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * Sets the language option
	 * @param language the language option
	 * @return this {@link RegionalOptions}
	 */
	public RegionalOptions setLanguage(String language) {
		this.language = language;
		return this;
	}
	
	/**
	 * Gets the timeZone option
	 * @return the {@link TimeZone} option
	 */
	public TimeZone getTimezone() {
		return timezone;
	}
	
	/**
	 * Sets the timeZone option
	 * @param timezone the {@link TimeZone} option
	 */
	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}
}
