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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteUtils {
	private static String MESSAGE_CHARSET = "UTF-8";
	
	public static String getString(ByteBuffer buffer) {
		int payload = buffer.remaining();
		return getString(buffer, payload);
	}
	
	public static String getString(ByteBuffer buffer, int payload) {
		try {
			byte[] bytes = new byte[payload];
			buffer.get(bytes);
			return new String(bytes, MESSAGE_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new Error("Required charset " + MESSAGE_CHARSET + " not found", e);
		}
	}

	public static ByteBuffer encodeString(String message) {
		try {
			return ByteBuffer.wrap(message.getBytes(MESSAGE_CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ByteBuffer toMessageBuffer(String str) {
		try {
			return ByteBuffer.wrap(str.getBytes(MESSAGE_CHARSET));
		} catch (UnsupportedEncodingException e) {
			throw new Error("Required charset " + MESSAGE_CHARSET + " not found", e);
		}
	}
	
	public static byte[] getBytes(byte b) {
		return new byte[] { b };
	}
	
	public static byte[] getBytes(char c) {
		return new byte[] { (byte) ((c >> 8) & 0xff), (byte) ((c >> 0) & 0xff) };
	}

	public static byte[] getBytes(char[] data) {
		if (data == null)
			return null;

		byte[] bytes = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			System.arraycopy(getBytes(data[i]), 0, bytes, i * 2, 2);
		}

		return bytes;
	}

	public static final byte[] getBytes(int data) {
		return new byte[] { (byte) (data >> 24), (byte) (data >> 16), (byte) (data >> 8), (byte) data };
	}
	
	public static byte[] getBytes(short data) {
		return new byte[] { (byte) (data >> 8), (byte)data } ;
	}
	
	public static byte[] getBytes(int[] data) {
		if (data == null)
			return null;

		byte[] bytes = new byte[data.length * 4];

		for (int i = 0; i < data.length; i++) {
			System.arraycopy(getBytes(data[i]), 0, bytes, i * 4, 4);
		}

		return bytes;
	}

	public static byte[] getBytes(long data) {
		return new byte[] { (byte) ((data >> 56) & 0xff), (byte) ((data >> 48) & 0xff), (byte) ((data >> 40) & 0xff), (byte) ((data >> 32) & 0xff), (byte) ((data >> 24) & 0xff), (byte) ((data >> 16) & 0xff), (byte) ((data >> 8) & 0xff), (byte) ((data >> 0) & 0xff), };
	}

	public static byte[] getBytes(long[] data) {
		if (data == null)
			return null;

		byte[] bytes = new byte[data.length * 8];

		for (int i = 0; i < data.length; i++) {
			System.arraycopy(getBytes(data[i]), 0, bytes, i * 8, 8);
		}

		return bytes;
	}

	public static byte[] getBytes(float data) {
		return getBytes(Float.floatToRawIntBits(data));
	}

	public static byte[] getBytes(float[] data) {
		if (data == null)
			return null;

		byte[] bytes = new byte[data.length * 4];

		for (int i = 0; i < data.length; i++) {
			System.arraycopy(getBytes(data[i]), 0, bytes, i * 4, 4);
		}

		return bytes;
	}

	public static byte[] getBytes(double data) {
		return getBytes(Double.doubleToRawLongBits(data));
	}

	public static byte[] getBytes(double[] data) {
		if (data == null)
			return null;

		byte[] bytes = new byte[data.length * 8];

		for (int i = 0; i < data.length; i++)

			System.arraycopy(getBytes(data[i]), 0, bytes, i * 8, 8);

		return bytes;
	}

	public static byte[] getBytes(boolean data) {
		return new byte[] { (byte) (data ? 0x01 : 0x00) };
	}

	/**
	 * The byte array contains information about how many boolean values are
	 * involved, so the exact array is returned when later decoded.
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getBytes(boolean[] data) {
		if (data == null)
			return null;

		int len = data.length;
		byte[] lenArray = getBytes(len); // int conversion; length array =
											// lenArray
		byte[] bytes = new byte[lenArray.length + (len / 8) + (len % 8 != 0 ? 1 : 0)];

		// (Above) length-array-length + sets-of-8-booleans +?
		// byte-for-remainder

		System.arraycopy(lenArray, 0, bytes, 0, lenArray.length);

		// (Below) algorithm by Matthew Cudmore: boolean[] -> bits -> byte[]
		for (int i = 0, j = lenArray.length, k = 7; i < data.length; i++) {
			bytes[j] |= (data[i] ? 1 : 0) << k--;
			if (k < 0) {
				j++;
				k = 7;
			}
		}

		return bytes;
	}

	public static byte[] getBytes(String data) {
		return (data == null) ? null : data.getBytes();
	}

	public static byte toByte(byte[] data) {
		return (data == null || data.length == 0) ? 0x0 : data[0];
	}

	public static short toShort(byte[] data) {
		if (data == null || data.length != 2)
			return 0x0;

		return (short) ((0xff & data[0]) << 8 | (0xff & data[1]) << 0);
	}

	public static short[] toShortArray(byte[] data) {
		if (data == null || data.length % 2 != 0)
			return null;

		short[] shts = new short[data.length / 2];

		for (int i = 0; i < shts.length; i++) {
			shts[i] = toShort(new byte[] { data[(i * 2)], data[(i * 2) + 1] });
		}
		return shts;
	}

	public static char toChar(byte[] data) {
		if (data == null)
			return 0x0;
		
		if (data == null || data.length != 2)
			return 0x0;
		
		return (char) ((0xff & data[0]) << 8 | (0xff & data[1]) << 0);
	}

	public static char[] toCharA(byte[] data) {
		if (data == null || data.length % 2 != 0)
			return null;
		
		char[] chrs = new char[data.length / 2];
		for (int i = 0; i < chrs.length; i++) {
			chrs[i] = toChar(new byte[] { data[(i * 2)], data[(i * 2) + 1], });
		}
		return chrs;
	}

	public static int toInt(byte[] data) {
		if (data == null || data.length != 4)
			return 0x0;

		// NOTE: type cast not necessary for int
		return (int) ((0xff & data[0]) << 24 | (0xff & data[1]) << 16 | (0xff & data[2]) << 8 | (0xff & data[3]) << 0);
	}

	public static int[] toIntArray(byte[] data) {
		if (data == null || data.length % 4 != 0)
			return null;

		int[] ints = new int[data.length / 4];
		for (int i = 0; i < ints.length; i++) {
			ints[i] = toInt(new byte[] { data[(i * 4)], data[(i * 4) + 1], data[(i * 4) + 2], data[(i * 4) + 3], });
		}
		return ints;
	}

	public static long toLong(byte[] data) {
		if (data == null || data.length != 8)
			return 0x0;
		
		// convert to longs before shift because digits
		// are lost with ints beyond the 32-bit limit
		return (long) ((long) (0xff & data[0]) << 56 | (long) (0xff & data[1]) << 48 | (long) (0xff & data[2]) << 40 | (long) (0xff & data[3]) << 32 | (long) (0xff & data[4]) << 24 | (long) (0xff & data[5]) << 16 | (long) (0xff & data[6]) << 8 | (long) (0xff & data[7]) << 0);
	}

	public static long[] toLongArray(byte[] data) {
		if (data == null || data.length % 8 != 0)
			return null;

		long[] longs = new long[data.length / 8];
		for (int i = 0; i < longs.length; i++) {
			longs[i] = toLong(new byte[] { data[(i * 8)], data[(i * 8) + 1], data[(i * 8) + 2], data[(i * 8) + 3], data[(i * 8) + 4], data[(i * 8) + 5], data[(i * 8) + 6], data[(i * 8) + 7], });
		}
		return longs;
	}

	public static float toFloat(byte[] data) {
		if (data == null || data.length != 4)
			return 0x0;
		
		return Float.intBitsToFloat(toInt(data));
	}

	public static float[] toFloatArray(byte[] data) {
		if (data == null || data.length % 4 != 0)
			return null;

		float[] floats = new float[data.length / 4];
		for (int i = 0; i < floats.length; i++) {
			floats[i] = toFloat(new byte[] { data[(i * 4)], data[(i * 4) + 1], data[(i * 4) + 2], data[(i * 4) + 3], });
		}
		return floats;
	}

	public static double toDouble(byte[] data) {
		if (data == null || data.length != 8)
			return 0x0;

		return Double.longBitsToDouble(toLong(data));
	}

	public static double[] toDoubleArray(byte[] data) {
		if (data == null)
			return null;

		if (data.length % 8 != 0)
			return null;
		
		double[] doubles = new double[data.length / 8];
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = toDouble(new byte[] { data[(i * 8)], data[(i * 8) + 1], data[(i * 8) + 2], data[(i * 8) + 3], data[(i * 8) + 4], data[(i * 8) + 5], data[(i * 8) + 6], data[(i * 8) + 7], });
		}
		return doubles;
	}

	public static boolean toBoolean(byte[] data) {
		return (data == null || data.length == 0) ? false : data[0] != 0x00;
	}

	/**
	 * Extract the boolean array's length
	 * from the first four bytes in the char array, and then
	 * read the boolean array.
	 */
	public static boolean[] toBooleanArray(byte[] data) {
		if (data == null || data.length < 4)
			return null;

		int len = toInt(new byte[] { data[0], data[1], data[2], data[3] });
		boolean[] bools = new boolean[len];

		for (int i = 0, j = 4, k = 7; i < bools.length; i++) {
			bools[i] = ((data[j] >> k--) & 0x01) == 1;
			if (k < 0) {
				j++;
				k = 7;
			}
		}
		return bools;
	}
}
