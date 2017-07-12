
package com.jfixby.scarabei.red.graphs;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.graphs.PathInGraph;
import com.jfixby.scarabei.api.log.L;

public class CyclePathImpl implements PathInGraph {

	@Override
	public String toString () {
		return "GraphCycle" + this.states + "";
	}

	private final List<StateImpl> states = Collections.newList();
	private final List<StepImpl> steps = Collections.newList();

	@Override
	public int numberOfSteps () {
		return this.states.size();
	}

	@Override
	public int numberOfStates () {
		return this.states.size();
	}

	@Override
	public StateImpl getState (final int state_number) {
		return this.states.getElementAt(state_number);
	}

	@Override
	public StepImpl getStep (final int step_number) {
		return this.steps.getElementAt(step_number);
	}

	public void setNumberOfVetices (final int n) {
		if (n < 0) {
			Err.reportError("Negative size graph: " + n);
		}
		if (n == 0) {
			this.states.clear();
			return;
		}
		int current_size = this.states.size();
		if (current_size == n) {
			return;
		}
		// n>0, n!=current_size;
		if (current_size < n) {
			do {
				current_size = this.addVertex(current_size);
			} while (current_size < n);
		} else {
			do {
				current_size = this.removeVertex(current_size);
			} while (current_size > n);
		}

	}

	private int removeVertex (final int current_size) {// current_size>1;
		final StateImpl removed = this.states.removeElementAt(current_size - 1);

		final StateImpl first = this.states.getElementAt(0);
		final StateImpl last = this.states.getElementAt(current_size - 2);

		final StepImpl edge = first.getLeftEdge();
		edge.setLeftState(last);
		last.setRightEdge(edge);

		final StepImpl removed_left_edge = removed.getLeftEdge();
		removed.setLeftEdge(null);
		removed.setRightEdge(null);
		removed_left_edge.setLeftState(null);
		removed_left_edge.setRightState(null);

		return this.states.size();
	}

	private int addVertex (final int current_size) {
		if (current_size == 0) {
			final StateImpl new_vertex = this.newVertex();
			final StepImpl new_edge = new StepImpl();
			new_edge.setLeftState(new_vertex);
			new_edge.setRightState(new_vertex);
			new_vertex.setRightEdge(new_edge);
			new_vertex.setLeftEdge(new_edge);
			this.states.add(new_vertex);
			return 1;
		}

		final StateImpl first = this.states.getElementAt(0);
		final StateImpl last = this.states.getElementAt(current_size - 1);

		final StateImpl new_vertex = this.newVertex();
		final StepImpl new_edge = new StepImpl();

		last.setRightEdge(new_edge);
		new_edge.setLeftState(last);

		new_edge.setRightState(new_vertex);
		new_vertex.setLeftEdge(new_edge);

		final StepImpl first_left_edge = first.getLeftEdge();
		new_vertex.setRightEdge(first_left_edge);
		first_left_edge.setLeftState(new_vertex);

		this.states.add(new_vertex);

		return this.states.size();

	}

	private StateImpl newVertex () {
		return new StateImpl().setVertex(new VertexImpl());
	}

	public void print () {
		L.d("---GraphCycle---");
		if (this.states.size() == 0) {
			return;
		}

		for (int i = 0; i < this.states.size(); i++) {
			final StateImpl ver = this.states.getElementAt(i);
			L.d("  [" + i + "]", ver.toString());
		}

	}

	@Override
	public List<VertexType> toVerticesList () {
		final List<VertexType> vertices = Collections.newList();
		for (int i = 0; i < this.numberOfSteps(); i++) {
			final VertexType object = this.getState(i).getVertex().getVertexObject();
			vertices.add(object);
		}

		return vertices;
	}

}
