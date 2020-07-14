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

import java.util.Random;

/**
 * By now just replaces the Java Random After to implement more specialized
 * randomizer
 */
public class Randomizer {
	public static Random random = new Random();

	public static int nextInt(int n) {
		return random.nextInt(n);
	}

	public static float nextFloat() {
		return random.nextFloat();
	}

	public static void shuffle(byte[] array) {
		for (int i = array.length; i > 1; i--) {
			byte temp = array[i - 1];
			int randIx = (int) (Math.random() * i);
			array[i - 1] = array[randIx];
			array[randIx] = temp;
		}
	}
}
