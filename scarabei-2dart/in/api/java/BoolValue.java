
package com.jfixby.scarabei.api.java;

public class BoolValue {

	public String toString () {
		return "" + value;
	}

	public boolean value;


	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
		return result;
	}


	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BoolValue other = (BoolValue)obj;
		if (value != other.value) return false;
		return true;
	}

}
