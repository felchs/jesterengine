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


/**
 * This class is temporary.
 * This implementation is very unpleasant
 *
 */
public class ObjectPropertiesFetcher {
	private byte[] bytes;
	
	private int capacity;
	
	public ObjectPropertiesFetcher(byte[] bytes, int capacity) {
		this.bytes = bytes;
		this.capacity = capacity;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Get the size in byte of a object.
	 *  
	 * @param Object
	 * @return int size in bytes
	 */
	public static ObjectPropertiesFetcher getObjectProperties(Object o) {
		Class<? extends Object> clazz = o.getClass();
		if (clazz == Boolean.class) {
			return new ObjectPropertiesFetcher(ByteUtils.getBytes((Boolean)o), 1);
		}
		else if (clazz == Byte.class) {
			byte[] bytes = new byte[1];
			bytes[0] = (Byte)o;
			return new ObjectPropertiesFetcher(bytes, 1);
		}
		else if (clazz == Character.class) {
			char c = (Character)o;
			byte[] bytes = ByteUtils.getBytes(c);
			return new ObjectPropertiesFetcher(bytes, 1);
		}
		else if (clazz == Short.class) {
			short s = (Short)o;
			byte[] bytes = ByteUtils.getBytes(s);
			return new ObjectPropertiesFetcher(bytes, 2);
		}
		else if (clazz == Integer.class) {
			int i = (Integer)o;
			byte[] bytes = ByteUtils.getBytes(i);
			return new ObjectPropertiesFetcher(bytes, 4);
		}
		else if (clazz == Long.class) {
			long l = (Long)o;
			byte[] bytes = ByteUtils.getBytes(l);
			return new ObjectPropertiesFetcher(bytes, 8);
		}
		else if (clazz == Float.class) {
			float f = (Float)o;
			byte[] bytes = ByteUtils.getBytes(f);
			return new ObjectPropertiesFetcher(bytes, 4);
		}
		else if (clazz == Double.class) {
			double d = (Double)o;
			byte[] bytes = ByteUtils.getBytes(d);
			return new ObjectPropertiesFetcher(bytes, 8);
		}
		else if (clazz == String.class) {
			// String Protocol:
			// -> 2 bytes - Short (String length)
			// -> x bytes - String (the String itself)
			String strObj = (String)o;
			int length = strObj.length();
			int capacity = 2 + length;
			byte[] bytes = new byte[capacity];
			byte[] shortBytes = ByteUtils.getBytes((short)length);
			System.arraycopy(shortBytes, 0, bytes, 0, shortBytes.length);
			System.arraycopy(strObj.getBytes(), 0, bytes, 2, length);
			return new ObjectPropertiesFetcher(bytes, capacity);
		}

		throw new RuntimeException("The object: [" + o + "] is object is not supported");
	}
}
