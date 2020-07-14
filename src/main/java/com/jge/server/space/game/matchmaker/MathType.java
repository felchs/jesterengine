package com.jge.server.space.game.matchmaker;

import java.util.HashSet;

import com.jge.server.utils.ByteUtils;

public enum MathType {
	ONE_TO_ONE((byte)0);
	
	private byte id;
	
	private static HashSet<Byte> byteList;
	
	private MathType(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return this.toString();
	}

	public static boolean contains(byte matchType) {
		if (byteList == null) {
			byteList = new HashSet<Byte>();
			MathType[] values = MathType.values();
			for (MathType mathType : values) {
				byteList.add(mathType.id);
			}
		}

		return byteList.contains(matchType);
	}
}
