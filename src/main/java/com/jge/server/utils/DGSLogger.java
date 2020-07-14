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
package com.jge.server.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DGSLogger {
	public static boolean doLogging = true;

	public static void log(String message, boolean logAnyway) {
		if (doLogging && logAnyway) {
			Logger.getLogger("DGELogger").log(Level.INFO, message);
		}
	}

	public static void log(String message) {
		if (doLogging) {
			Logger.getLogger("DGELogger").log(Level.INFO, message);
		}
	}
	
	public static void log(Level level, String message) {
		if (doLogging) {
			Logger.getLogger("DGELogger").log(level, message);
		}
	}
	
	public static void log(Level level, Exception e) {
		
	}
}
