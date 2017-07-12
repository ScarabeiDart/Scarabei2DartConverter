
package com.jfixby.scarabei.api.lang;

import java.util.Arrays;

import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.java.ByteArray;

public class RedByteArray implements ByteArray {

	final private byte[] bytes;


	public String toString () {
		return "ByteArray(" + this.bytes.length + ") " + Arrays.toString(this.bytes) + "";
	}

	public RedByteArray (final byte[] bytes) {
		Debug.checkNull("bytes", bytes);
		this.bytes = bytes;
	}

	public RedByteArray (final int size) {
		this.bytes = new byte[size];
	}


	public byte[] toArray () {
		return this.bytes;
	}


	public long size () {
		return this.bytes.length;
	}


	public int getByte (final int i) {
		return this.bytes[i];
	}

}
