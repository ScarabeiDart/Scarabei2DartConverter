
package com.jfixby.scarabei.red.graphs;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.graphs.Edge;
import com.jfixby.scarabei.api.graphs.MultiGraph;
import com.jfixby.scarabei.api.graphs.PathInGraph;
import com.jfixby.scarabei.api.graphs.Vertex;
import com.jfixby.scarabei.api.log.L;

public class MultiGraphImpl implements MultiGraph {

	public MultiGraphImpl () {
		super();
	}

	void print (final MultiGraphImpl graph) {
		L.d("---MultiGraph---");
		L.d("Nodes:");
		for (int i = 0; i < graph.size(); i++) {
			final VertexImpl n = graph.getVertex(i);
			L.d("   [" + i + "] " + n);
			final Set<EdgeImpl> links = n.getLinks();
			for (int k = 0; k < links.size(); k++) {
				final EdgeImpl e = links.getElementAt(k);
				L.d("       " + graph.toString(e));
			}

		}
		// printEdges("Edges", graph.edges, graph);

	}

	void printEdges (final String string, final List<EdgeImpl> list,
		final MultiGraphImpl graph) {
		L.d(string + ":");
		for (int i = 0; i < list.size(); i++) {
			final EdgeImpl n = list.getElementAt(i);

			L.d("   [" + i + "] " + graph.toString(n));
			L.d("       " + n);
		}

	}

	static void printNodes (final String string, final List<VertexImpl> list,
		final MultiGraphImpl graph) {
		L.d(string + ":");
		for (int i = 0; i < list.size(); i++) {
			final VertexImpl n = list.getElementAt(i);

			L.d("   [" + i + "] " + graph.toString(n));
			L.d("       " + n);
		}

	}

	private String toString (final VertexImpl n) {
		return n.toString();
	}

	private VertexImpl getVertex (final int i) {
		return this.vertices.getElementAt(i);
	}

	final List<VertexImpl> vertices = Collections.newList();
	final List<EdgeImpl> edges = Collections.newList();

	public int size () {
		return this.vertices.size();
	}

	public void establishLinks () {
		for (int i = 0; i < this.edges.size(); i++) {
			final EdgeImpl e = this.edges.getElementAt(i);
			e.getLeftNode().addLink(e);
			e.getRightNode().addLink(e);
		}

	}

	public int numberOfEdges () {
		return this.edges.size();
	}

	public EdgeImpl getEdge (final int i) {
		return this.edges.getElementAt(i);
	}

	private String toString (final EdgeImpl current) {
		return "[" + this.indexOf(current) + "] " + current.toString();
	}

	public int indexOf (final EdgeImpl n) {
		return this.indexOf(n, this.edges);
	}

	int indexOf (final EdgeImpl n, final List<EdgeImpl> edges) {
		for (int i = 0; i < edges.size(); i++) {
			final EdgeImpl e = edges.getElementAt(i);
			if (e == n) {
				return i;
			}
		}
		return -1;
	}

	public void print () {
		// print(this);

	}

	@Override
	public Vertex<VertexType> newVertex () {
		final VertexImpl element = new VertexImpl();
		this.vertices.add(element);
		return element;
	}

	@Override
	public Vertex<VertexType> findVertexByObject (final VertexType vertex_object) {
		if (vertex_object == null) {
			Err.reportError("Null argument exception.");
		}
		for (int i = 0; i < this.vertices.size(); i++) {
			final VertexImpl vertex = this.vertices.getElementAt(i);
			if (vertex_object == vertex.getVertexObject()) {
				// if (vertex_object.equals(vertex.getObject())) {
				return vertex;
			}
		}
		return null;
	}

	@Override
	public Edge<EdgeType> newEdge (final Vertex<VertexType> vertex_a, final Vertex<VertexType> vertex_b) {
		final EdgeImpl edge = this.createNewEdge((VertexImpl)vertex_a,
			(VertexImpl)vertex_b);

		this.establishLinks();

		return edge;
	}

	public EdgeImpl createNewEdge (final VertexImpl left_node,
		final VertexImpl right_node) {
		if (left_node == null || right_node == null) {
			Err.reportError("left_node=" + left_node + " , right_node=" + right_node);
		}
		final EdgeImpl edge = new EdgeImpl();
		edge.setLeftNode(left_node);
		edge.setRightNode(right_node);
		this.edges.add(edge);
		return edge;

	}

	@Override
	public PathInGraph findPath (final Vertex<VertexType> from_vertex, final Vertex<VertexType> to_vertex) {
		final List<VertexImpl> visited = Collections.newList();
		final List<VertexImpl> states = Collections.newList();
		final List<EdgeImpl> steps = Collections.newList();

		visited.add((VertexImpl)from_vertex);
		states.add((VertexImpl)from_vertex);
		this.try_search((VertexImpl)from_vertex, (VertexImpl)to_vertex, visited, states,
			steps);
		// L.d("--------------------------------------------------");
		// L.d("from_vertex", from_vertex);
		// L.d(" to_vertex", to_vertex);
		// states.print("states");
		// steps.print("steps");

		final PathImpl path = new PathImpl();
		path.setup(states, steps);

		// path.print("path");
		// RedTriplane.exit();
		return path;
	}

	private boolean try_search (final VertexImpl from_vertex,
		final VertexImpl final_vertex, final List<VertexImpl> visited,
		final List<VertexImpl> states, final List<EdgeImpl> steps) {
		if (from_vertex == final_vertex) {
			return true;
		}
		for (int i = 0; i < from_vertex.getLinks().size(); i++) {
			final EdgeImpl edge = from_vertex.getLinks().getElementAt(i);
			final VertexImpl next = edge.getOtherNode(from_vertex);
			if (!visited.contains(next)) {
				states.add(next);
				steps.add(edge);
				visited.add(next);
				if (this.try_search(next, final_vertex, visited, states, steps)) {
					return true;
				} else {
					states.remove(next);
					steps.remove(edge);
				}
			}
		}
		return false;
	}

	@Override
	public void print (final String tag) {
		this.print(this);
	}
}
