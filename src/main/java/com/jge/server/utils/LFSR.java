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

import java.util.Vector;

/**
 * Generates Linear Feedback Shift Registers which acts as 
 * pseudo ordered random numbers
 *
 */
public final class LFSR {
	private static final int M = 15;

    // hard-coded for 15-bits
    private static final int[] TAPS = {14, 15};
    
    private final boolean[] bits = new boolean[M + 1];
        
    public LFSR() {
        this((int)System.currentTimeMillis());
    }
    
    public LFSR(int seed) {
        for(int i = 0; i < M; i++) {
            bits[i] = (((1 << i) & seed) >>> i) == 1;
        }
    }
    
    /* generate a random int uniformly on the interval [-2^31 + 1, 2^31 - 1] */
    public short nextShort() {
        //printBits();
        
        // calculate the integer value from the registers
        short next = 0;
        for(int i = 0; i < M; i++) {
            next |= (bits[i] ? 1 : 0) << i;
        }
        
        // allow for zero without allowing for -2^31
        if (next < 0) next++;
        
        // calculate the last register from all the preceding
        bits[M] = false;
        for(int i = 0; i < TAPS.length; i++) {
            bits[M] ^= bits[M - TAPS[i]];
        }
        
        // shift all the registers
        for(int i = 0; i < M; i++) {
            bits[i] = bits[i + 1];
        }
        
        return next;
    }
    
    /** returns random double uniformly over [0, 1) */
    public double nextDouble() {
        return ((nextShort() / (Integer.MAX_VALUE + 1.0)) + 1.0) / 2.0;
    }
    
    /** returns random boolean */
    public boolean nextBoolean() {
        return nextShort() >= 0;
    }
    
//    public void printBits() {
//        System.out.print(bits[M] ? 1 : 0);
//        System.out.print(" -> ");
//        for(int i = M - 1; i >= 0; i--) {
//            System.out.print(bits[i] ? 1 : 0);
//        }
//        System.out.println();
//    }

	public static Short getNextShort(String keyName) {
		String objName = keyName + "_LFSRandom";
		LFSR lfsr = (LFSR)MappingUtil.getObject(objName);
		if (lfsr == null) {
			lfsr = new LFSR();
			MappingUtil.addObject(objName, lfsr);
		}
		return lfsr.nextShort();
	}
    
    public static void main(String[] args) {
    	for (int m= 0; m < 10000; m++) {
        LFSR rng = new LFSR();
        Vector<Short> vec = new Vector<Short>();
        for(int i = 0; i <= 32766; i++) {
            short next = rng.nextShort();
            if (vec.contains(next))
            	throw new RuntimeException("M " + i);
            vec.add(next);
//            System.out.println(next);
        }
        System.out.println("M: " + m);
    	}
    }    
}
