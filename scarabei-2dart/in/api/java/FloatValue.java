
package com.jfixby.scarabei.api.java;

public class FloatValue implements Comparable<FloatValue> {


	public String toString () {
		return "" + value;
	}

	public double value;


	public int hashCode () {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}


	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FloatValue other = (FloatValue)obj;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) return false;
		return true;
	}


	final public int compareTo (final FloatValue o) {
		return Double.compare(value, o.value);
	}

}
