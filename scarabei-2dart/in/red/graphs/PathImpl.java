
package com.jfixby.scarabei.red.graphs;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.graphs.PathInGraph;
import com.jfixby.scarabei.api.graphs.PathState;
import com.jfixby.scarabei.api.graphs.PathStep;
import com.jfixby.scarabei.api.log.L;

public class PathImpl implements PathInGraph {

	final List<StateImpl> states = Collections.newList();
	final List<StepImpl> steps = Collections.newList();

	@Override
	public int numberOfStates () {
		return states.size();
	}

	@Override
	public PathState getState (int state_number) {
		return this.states.getElementAt(state_number);
	}

	@Override
	public int numberOfSteps () {
		return steps.size();
	}

	@Override
	public PathStep getStep (int step_number) {
		return this.steps.getElementAt(step_number);
	}

	public void setup (List<VertexImpl> states, List<EdgeImpl> steps) {
		for (int i = 0; i < states.size(); i++) {
			VertexImpl vertex = states.getElementAt(i);
			StateImpl state = new StateImpl();
			state.setVertex(vertex);
			this.states.add(state);
		}

		for (int i = 0; i < steps.size(); i++) {
			EdgeImpl edge = steps.getElementAt(i);
			StepImpl step = new StepImpl();
			step.setEdge(edge);
			StateImpl leftState = this.states.getElementAt(i);
			StateImpl rightState = this.states.getElementAt(i + 1);

			step.setLeftState(leftState);
			step.setRightState(rightState);

			this.steps.add(step);

		}
	}

	public void print (String tag) {
		String tmp = "Path[" + tag + "] ";
		if (this.states.size() > 0) {

			tmp = tmp + " " + this.states.getElementAt(0).getVertex() + "";
		}
		for (int i = 0; i < this.steps.size(); i++) {
			StepImpl step = this.steps.getElementAt(i);
			tmp = tmp + " -[" + step.getEdge() + "]-> "
				+ step//
					.getRightState()//
					.getVertex();
		}
		L.d(tmp);
	}

	@Override
	public List<VertexType> toVerticesList () {
		List<VertexType> vertices = Collections.newList();
		for (int i = 0; i < this.numberOfSteps(); i++) {
			VertexType object = this.getState(i).getVertex().getVertexObject();
			vertices.add(object);
		}

		return vertices;
	}
}
