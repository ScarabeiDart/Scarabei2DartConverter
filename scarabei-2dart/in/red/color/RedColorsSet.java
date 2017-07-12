
package com.jfixby.scarabei.red.color;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.CollectionFilter;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.EditableCollection;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.color.Color;
import com.jfixby.scarabei.api.color.ColorDistance;
import com.jfixby.scarabei.api.color.ColorSet;
import com.jfixby.scarabei.api.color.Colors;
import com.jfixby.scarabei.api.err.Err;

public class RedColorsSet implements ColorSet {

	private final ColorDistance distance;

	public RedColorsSet (final ColorDistance distance) {
		this.distance = distance;
	}

	
	public boolean containsAll (final Collection list) {
		for (int i = 0; i < list.size(); i++) {
			if (!this.contains(list.getElementAt(i))) {
				return false;
			}
		}
		return true;
	}

	final Set<Color> colors = Collections.newSet();

	
	public int size () {
		return this.colors.size();
	}

	
	public Color getElementAt (final long i) {
		return this.colors.getElementAt(i);
	}

	
	public Color findClosest (final Color original) {
		if (this.colors.size() == 0) {
			Err.reportError("ColorsSet is empty.");
		}
		Color closest = this.colors.getElementAt(0);// assuming size >0;
		float current_distance = Colors.distanceRGB().measure(original, closest);

		for (int i = 1; i < this.colors.size(); i++) {
			if (current_distance == 0) {
				return closest;
			}
			final Color candidate_i = this.colors.getElementAt(i);
			final float new_distance = this.distance.measure(candidate_i, original);
			if (new_distance < current_distance) {
				current_distance = new_distance;
				closest = candidate_i;
			}
		}
		return closest;

	}

	
	public void print (final String tag) {
		this.colors.print(tag);
	}

	
	public void sort () {
		this.colors.sort();
	}

	
	public boolean remove (final Object element) {
		return this.colors.remove(element);
	}

	
	public void clear () {
		this.colors.clear();
	}

	
	public boolean add (final Color element) {
		return this.colors.add(element);
	}

	
	public int indexOf (final Object element) {
		return this.colors.indexOf(element);
	}

	
	public void reverse () {
		this.colors.reverse();
	}

	
	public Color removeElementAt (final long i) {
		return this.colors.removeElementAt(i);
	}

	
	public Color removeLast () {
		return this.colors.removeLast();
	}
	

	
	public RedColorsSet addJavaCollection (final java.util.Collection< Color> java_collection) {
		this.colors.addJavaCollection(java_collection);
		return this;
	}

	
	public void addAllArrayElements (final Color[] array) {
		this.colors.addAllArrayElements(array);
	}

	
	public RedColorsSet addAll (final Iterable< Color> list) {
		this.colors.addAll(list);
		return this;
	}

	
	public void removeAll (final com.jfixby.scarabei.api.collections.Collection< Object> list) {
		this.colors.removeAll(list);
	}

	
	public void sort (final Comparator<Color> comparator) {
		this.colors.sort(comparator);
	}

	
	public EditableCollection<Color> splitAt (final int index) {
		return this.colors.splitAt(index);
	}

	
	public boolean contains (final Object element) {
		return this.colors.contains(element);
	}

	
	public List<Color> toJavaList () {
		return this.colors.toJavaList();
	}

	
	public Iterator<Color> iterator () {
		return this.colors.iterator();
	}

	
	public Color getLast () {
		return this.colors.getLast();
	}

	
	public com.jfixby.scarabei.api.collections.List<Color> toList () {
		return this.colors.toList();
	}

	
	public boolean isEmpty () {
		return this.colors.isEmpty();
	}

	
	public void print (final String tag, final int from_index, final int to_index) {
		this.colors.print(tag, from_index, to_index);
	}

	
	public boolean beginsWith (final com.jfixby.scarabei.api.collections.Collection<Color> steps) {
		return this.colors.beginsWith(steps);
	}

	
	public com.jfixby.scarabei.api.collections.List<Color> filter (final CollectionFilter<Color> filter) {
		return this.colors.filter(filter);
	}

}
