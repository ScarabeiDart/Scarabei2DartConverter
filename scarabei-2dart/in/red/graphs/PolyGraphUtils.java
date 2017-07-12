
package com.jfixby.scarabei.red.graphs;

import com.jfixby.scarabei.api.angles.Angles;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.EditableCollection;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.floatn.Float2;
import com.jfixby.scarabei.api.floatn.ReadOnlyFloat2;
import com.jfixby.scarabei.api.geometry.Geometry;
import com.jfixby.scarabei.api.graphs.PathInGraph;
import com.jfixby.scarabei.api.math.CustomAngle;
import com.jfixby.scarabei.api.math.FloatMath;
import com.jfixby.scarabei.api.math.MathTools;
import com.jfixby.scarabei.api.math.VectorTool;

public class PolyGraphUtils {
	static private  void removeLoopEdges (final Ploy2DGraph multy_graph) {
		final List<EdgeImpl> edges_to_remove = Collections.newList();

		for (int k = 0; k < multy_graph.edges.size(); k++) {
			final EdgeImpl other = multy_graph.edges.getElementAt(k);
			if (other.getLeftNode() == other.getRightNode()) {
				edges_to_remove.add(other);
			}
		}

		multy_graph.edges.removeAll(edges_to_remove);
	}

	static public  boolean isDuplicatingEdge (final EdgeImpl one,
		final EdgeImpl other, final Ploy2DGraph multy_graph) {
		final VertexImpl AL = one.getLeftNode();
		final VertexImpl AR = one.getRightNode();
		final VertexImpl BL = other.getLeftNode();
		final VertexImpl BR = other.getRightNode();

		if (AL == BL) {
			if (AR == BR) {
				return true;
			} else {
				return false;
			}
		}

		if (AL == BR) {
			if (AR == BL) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	static private  void findIntersections (final Ploy2DGraph multy_graph) {
		final List<EdgeImpl> edges_to_remove = Collections.newList();
		final List<EdgeImpl> edges_to_add = Collections.newList();
		for (int i = 0; i < multy_graph.edges.size(); i++) {
			final EdgeImpl one = multy_graph.edges.getElementAt(i);
			for (int k = i + 1; k < multy_graph.edges.size(); k++) {
				final EdgeImpl other = multy_graph.edges.getElementAt(k);
				VertexImpl intersection = findIntersection(one, other);
				if (intersection != null) {
					// Log.d("intersection", toString(one), " vs",
					// toString(other));
					intersection = addIfNecessary(multy_graph, intersection);
					// Log.d(" at", intersection);

					markEdgeForReplacment(one, intersection, edges_to_add, edges_to_remove);
					markEdgeForReplacment(other, intersection, edges_to_add, edges_to_remove);
					multy_graph.edges.removeAll(edges_to_remove);
					multy_graph.edges.addAll(edges_to_add);
					edges_to_remove.clear();
					edges_to_add.clear();
					removeDuplicatingEdges(multy_graph);
					removeLoopEdges(multy_graph);
					break;
				}

			}
		}

	}

	private static  VertexImpl findNode (final VertexImpl node,
		final List<VertexImpl> list) {
		for (int i = 0; i < list.size(); i++) {
			final VertexImpl element = list.getElementAt(i);
			if (element.getVertexObject().isInEpsilonDistance(node.getVertexObject())) {
				return element;
			}
		}
		return null;
	}

	private static  VertexImpl findNode (final ReadOnlyFloat2 dot, final Ploy2DGraph graph) {
		// final VertexImpl<Point, EdgeType> n = new VertexImpl<Point,
		// EdgeType>(point);
		// return findNode(n, graph.vertices);

		for (int i = 0; i < graph.vertices.size(); i++) {
			final VertexImpl element = graph.vertices.getElementAt(i);
			if (element.getVertexObject().isInEpsilonDistance(dot)) {
				return element;
			}
		}
		return null;
	}

	static private  void resolveOverlaps (final Ploy2DGraph multy_graph) {
		for (int i = 0; i < multy_graph.vertices.size(); i++) {
			final VertexImpl node = multy_graph.vertices.getElementAt(i);
			resolveOverlaps(node, multy_graph);
		}
		removeLoopEdges(multy_graph);
		removeDuplicatingEdges(multy_graph);
	}

	static private  void resolveOverlaps (final VertexImpl node, final Ploy2DGraph graph) {
		final List<EdgeImpl> edges_to_remove = Collections.newList();
		final List<EdgeImpl> edges_to_add = Collections.newList();
		for (int i = 0; i < graph.edges.size(); i++) {
			final EdgeImpl edge = graph.edges.getElementAt(i);
			if (edge.getLeftNode() != node && edge.getRightNode() != node) {
				if (nodeLaysOn(node, edge)) {
					markEdgeForReplacment(edge, node, edges_to_add, edges_to_remove);
				}
			}
		}
		graph.edges.removeAll(edges_to_remove);
		graph.edges.addAll(edges_to_add);
	}

	static private  boolean nodeLaysOn (final VertexImpl node, final EdgeImpl edge) {
		double xN = 0;
		double yN = 0;
		double xA = 0;
		double yA = 0;
		double xB = 0;
		double yB = 0;

		xN = node.getVertexObject().getX();
		yN = node.getVertexObject().getY();

		xA = edge.getLeftNode().getVertexObject().getX();
		yA = edge.getLeftNode().getVertexObject().getY();

		xB = edge.getRightNode().getVertexObject().getX();
		yB = edge.getRightNode().getVertexObject().getY();

		final double A = FloatMath.distance(xN, yN, xA, yA);
		final double B = FloatMath.distance(xN, yN, xB, yB);
		final double AB = FloatMath.distance(xB, yB, xA, yA);

		return FloatMath.EPSILON() >= FloatMath.abs(AB - A - B);
	}

	public static  Ploy2DGraph newMultiGraph (final EditableCollection<? extends ReadOnlyFloat2> input) {

		final List<VertexImpl> nodes = Collections.newList();
		for (int i = 0; i < input.size(); i++) {
			final ReadOnlyFloat2 dot = input.getElementAt(i);
			VertexImpl node = new VertexImpl();
			final Float2 point = Geometry.newFloat2(dot);
			node.putVertexObject(point);
			final VertexImpl found = findNode(node, nodes);
			if (found != null) {
				node = found;
			}
			nodes.add(node);
		}

		final Ploy2DGraph multy_graph = new Ploy2DGraph();
		for (int i = 0; i < nodes.size(); i++) {
			final VertexImpl point = nodes.getElementAt(i);
			if (!multy_graph.vertices.contains(point)) {
				multy_graph.vertices.add(point);
			}
		}

		for (int i = 0; i < input.size(); i++) {
			final int index = i;
			int index_next = i + 1;
			if (index_next >= input.size()) {
				index_next = 0;
			}
			final ReadOnlyFloat2 point = input.getElementAt(index);
			final ReadOnlyFloat2 point_next = input.getElementAt(index_next);

			final VertexImpl node = findNode(point, multy_graph);
			final VertexImpl next_node = findNode(point_next, multy_graph);
			final EdgeImpl edge = multy_graph.createNewEdge(node, next_node);
		}

		removeLoopEdges(multy_graph);
		removeDuplicatingEdges(multy_graph);
		resolveOverlaps(multy_graph);
		findIntersections(multy_graph);
		multy_graph.establishLinks();
		removeSingleNodes(multy_graph);

		return multy_graph;
	}

	static public  int indexOf (final Ploy2DGraph graph, final VertexImpl l) {
		for (int i = 0; i < graph.vertices.size(); i++) {
			final VertexImpl e = graph.vertices.getElementAt(i);
			if (e.getVertexObject().isInEpsilonDistance(l.getVertexObject())) {
				return i;
			}
		}
		return -1;
	}

	static public  String toString (final Ploy2DGraph graph, final EdgeImpl n) {
		if (n == null) {
			return null;
		}
		final VertexImpl l = n.getLeftNode();
		final VertexImpl r = n.getRightNode();
		final String id = "Edge-#" + graph.indexOf(n) + "";
		final int iL = indexOf(graph, l);
		final int iR = indexOf(graph, r);
		if (iL >= 0) {
			if (iR >= 0) {
				return id + " [" + iL + "]-[" + iR + "]";
			} else {
				return id + " [" + iL + "]-[#" + r + "]";
			}
		} else {
			if (iR >= 0) {
				return id + " [#" + l + "]-[" + iR + "]";
			} else {
				return id + " [#" + l + "]-[#" + r + "]";
			}
		}
	}

	static private  VertexImpl addIfNecessary (final Ploy2DGraph graph,
		final VertexImpl intersection) {
		final VertexImpl result = findNode(intersection, graph.vertices);
		if (result == null) {
			graph.vertices.add(intersection);
			return intersection;
		}
		return result;
	}

	static private  void markEdgeForReplacment (final EdgeImpl edge,
		final VertexImpl node, final List<EdgeImpl> edges_to_add,
		final List<EdgeImpl> edges_to_remove) {
		// Log.d("node ", node, " cuts ", edge);
		final EdgeImpl new_edge_left = new EdgeImpl();
		new_edge_left.setRightNode(node);
		new_edge_left.setLeftNode(edge.getLeftNode());

		final EdgeImpl new_edge_right = new EdgeImpl();
		new_edge_right.setRightNode(edge.getRightNode());
		new_edge_right.setLeftNode(node);

		edges_to_add.add(new_edge_left);
		edges_to_add.add(new_edge_right);
		edges_to_remove.add(edge);

		// Log.d(" ", new_edge_left);
		// Log.d(" ", new_edge_right);
	}

	static private  VertexImpl findIntersection (final EdgeImpl one,
		final EdgeImpl other) {
		if (one.isNeighbour(other)) {
			return null;
		}

		final double one_a_x = one.getLeftNode().getVertexObject().getX();
		final double one_A_y = one.getLeftNode().getVertexObject().getY();
		final double one_B_x = one.getRightNode().getVertexObject().getX();
		final double one_B_y = one.getRightNode().getVertexObject().getY();
		final double other_A_x = other.getLeftNode().getVertexObject().getX();
		final double other_A_y = other.getLeftNode().getVertexObject().getY();
		final double other_B_x = other.getRightNode().getVertexObject().getX();
		final double other_B_y = other.getRightNode().getVertexObject().getY();

		final double d = (other_B_y - other_A_y) * (one_B_x - one_a_x) - (other_B_x - other_A_x) * (one_B_y - one_A_y);
		if (FloatMath.isWithinEpsilon(d)) {
			return null;
		}

		// i.intersectLines(x1, y1, x2, y2, x3, y3, x4, y4, intersection)
		final VertexImpl node = new VertexImpl(Geometry.newFloat2());
		final double ua = ((other_B_x - other_A_x) * (one_A_y - other_A_y) - (other_B_y - other_A_y) * (one_a_x - other_A_x)) / d;
		node.getVertexObject().setXY(one_a_x + (one_B_x - one_a_x) * ua, one_A_y + (one_B_y - one_A_y) * ua);

		if (nodeLaysOn(node, one) && nodeLaysOn(node, other)) {
			return node;
		}
		return null;
	}

	static private  void removeSingleNodes (final Ploy2DGraph multy_graph) {
		boolean remove = false;
		do {
			remove = false;
			final List<VertexImpl> nodes_to_remove = Collections.newList();
			// final List<Node> isolated_nodes = new List<Node>();
			for (int i = 0; i < multy_graph.vertices.size(); i++) {
				final VertexImpl node = multy_graph.vertices.getElementAt(i);
				// if (node.getLinks().size() == 0) {
				// isolated_nodes.add(node);
				// remove = true;
				// }
				if (node.getLinks().size() <= 1) {
					nodes_to_remove.add(node);
					remove = true;
				}
			}
			// this.nodes.removeAll(isolated_nodes);
			for (int i = 0; i < nodes_to_remove.size(); i++) {
				final VertexImpl removal = nodes_to_remove.getElementAt(i);
				multy_graph.vertices.remove(removal);
				final Set<EdgeImpl> brocken_links = removal.getLinks();
				for (int k = 0; k < brocken_links.size(); k++) {
					final EdgeImpl bad_link = brocken_links.getElementAt(k);
					bad_link.getOtherNode(removal).getLinks().remove(bad_link);
					multy_graph.edges.remove(bad_link);
				}
			}
		} while (remove);

	}

	static private  boolean noNewExitsFromHere (final EdgeImpl direction,
		final List<EdgeImpl> testedExits) {
		for (int i = 0; i < testedExits.size(); i++) {
			final EdgeImpl test = testedExits.getElementAt(i);
			if (test.getRightNode() == direction.getRightNode() && test.getLeftNode() == direction.getLeftNode()) {
				return true;
			}
		}

		return false;
	}

	static private  EdgeImpl findNextBorderEdge (final EdgeImpl current) {
		final EdgeImpl next = findNextBorderEdge(current.getRightNode(), current);
		return next;
	}

	static private  CustomAngle edgeDirection (final VertexImpl start,
		final VertexImpl end) {
		final VectorTool tool = MathTools.newVectorTool();
		tool.X = end.getVertexObject().getX() - start.getVertexObject().getX();
		tool.Y = end.getVertexObject().getY() - start.getVertexObject().getY();
		tool.XYtoAR();
		tool.A = tool.A;
		return Angles.newAngle(tool.A);
	}

	static private  EdgeImpl findNextBorderEdge (final VertexImpl start,
		final EdgeImpl ignore) {
		final Set<EdgeImpl> links = start.getLinks();
		if (links.size() == 0) {
			Err.reportError("Isolated node: " + start);
		}

		EdgeImpl candidate = null;
		final CustomAngle candidate_angle = Angles.ZERO();
		final CustomAngle other_candidate_angle = Angles.ZERO();
		for (int i = 0; i < links.size(); i++) {
			final EdgeImpl other_candidate = links.getElementAt(i);
			if (other_candidate == ignore) {
				// Log.d(" skip", toString(other_candidate));
				continue;
				// other_candidate_angle.setValue(Angle.TWICE_PI);
			}
			boolean switch_direction = false;
			// Log.d("cheking candidate", toString(other_candidate));
			final VertexImpl other_end = other_candidate.getOtherNode(start);
			if (other_candidate.getLeftNode() == other_end) {
				switch_direction = true;
				other_candidate.switchDirection();
			}
			other_candidate_angle.setValue(edgeToEdgeAngle(ignore, other_candidate));
			if (switch_direction) {
				other_candidate.switchDirection();
			}

			if (candidate == null) {
				candidate = other_candidate;
				// Log.d(" switching candidate to", toString(candidate));
				candidate_angle.setValue(other_candidate_angle);
				continue;
			} else {
				final boolean new_candidate_is_better = candidate_angle.getMagnitude() > other_candidate_angle.getMagnitude();

				// Log.d(" candidate_angle", candidate_angle);
				// Log.d(" other_candidate_angle", other_candidate_angle);

				if (new_candidate_is_better) {
					// Log.d(" switching candidate from",
					// toString(candidate));
					candidate = other_candidate;
					// Log.d(" to",
					// toString(candidate));
					candidate_angle.setValue(other_candidate_angle);
				} else {
					// Log.d(" reject candidate", toString(other_candidate));
				}
			}
		}
		final VertexImpl other_end = candidate.getOtherNode(start);
		if (candidate.getLeftNode() == other_end) {
			candidate.switchDirection();
		}
		return candidate;
	}

	static private  CustomAngle edgeToEdgeAngle (final EdgeImpl from,
		final EdgeImpl to) {
		CustomAngle direction_from = null;
		if (from == null) {
			direction_from = Angles.newAngle();
		} else {
			direction_from = edgeDirection(from.getRightNode(), from.getLeftNode());
		}
		final CustomAngle direction_to = edgeDirection(to.getLeftNode(), to.getRightNode());
		direction_to.makePositive();
		direction_from.makePositive();

		final CustomAngle delta = Angles.subtract(direction_to, direction_from, Angles.newAngle());
		delta.setValue(delta.toRadians() + FloatMath.VAL_2PI());

		return delta;
	}

	static private  VertexImpl findTheMostDistantNode (final Ploy2DGraph multy_graph) {
		VertexImpl result = multy_graph.vertices.getElementAt(0);
		for (int i = 0; i < multy_graph.vertices.size(); i++) {
			final VertexImpl element = multy_graph.vertices.getElementAt(i);
			if (element.getVertexObject().getX() >= result.getVertexObject().getX()) {
				result = element;
			}
		}
		return result;
	}

	static private  void moveTaleFromTo (final List<VertexImpl> from,
		final List<VertexImpl> to, final int index) {
		final int n = from.size();
		for (int i = index; i < n; i++) {
			final VertexImpl remov = from.removeElementAt(index);
			to.add(remov);
		}
	}

	static private  int indexOf (final List<VertexImpl> unpainted_nodes,
		final VertexImpl node) {
		for (int i = 0; i < unpainted_nodes.size(); i++) {
			final VertexImpl e = unpainted_nodes.getElementAt(i);
			if (e == node) {
				return i;
			}
		}
		return -1;
	}

	static private  void visit (final VertexImpl node,
		final List<VertexImpl> visited_nodes) {
		visited_nodes.add(node);
	}

	public static  List<PathInGraph> extractSimpleCycles (final Ploy2DGraph multy_graph) {
		final List<VertexImpl> order = findBorderNodes(multy_graph);

		final List<VertexImpl> visited_nodes = Collections.newList();
		final List<VertexImpl> not_painted_nodes = Collections.newList();
		not_painted_nodes.addAll(order);
		final List<List<VertexImpl>> colors = Collections.newList();
		List<VertexImpl> color;
		final List<VertexImpl> queue = Collections.newList();
		int current_index = 0;
		VertexImpl current = null;
		boolean processing = not_painted_nodes.size() > 0 && current_index < order.size();
		while (processing) {
			current = order.getElementAt(current_index);
			current_index++;
			if (indexOf(visited_nodes, current) == -1) {
				queue.add(current);
				visited_nodes.add(current);
				// Log.d("visit:", toString(current));
			} else {
				// Log.d(" loop", toString(current));
				final int index = indexOf(queue, current);
				color = Collections.newList();
				moveTaleFromTo(queue, color, index + 1);
				not_painted_nodes.removeAll(color);
				not_painted_nodes.remove(current);
				color.add(current);
				if (color.size() >= 3) {
					colors.add(color);
				}
				// printNodes("color#", color, this);
				// printNodes("queue#", queue, this);
				// printNodes("not_painted_nodes#", not_painted_nodes, this);
			}
			processing = not_painted_nodes.size() > 0 && current_index < order.size();
		}

		final List<PathInGraph> result = Collections.newList();
		for (int i = 0; i < colors.size(); i++) {
			color = colors.getElementAt(i);
			final int n = color.size();
			final PathInGraph cycle = toCycleGraph(multy_graph, color);
			result.add(cycle);
		}

		return result;

	}

	static  List<VertexImpl> findBorderNodes (final Ploy2DGraph multy_graph) {
		if (multy_graph.vertices.size() < 3) {
			return Collections.newList();
		}
		// if (this.nodes.size() < 3) {
		// this.nodes.clear();
		// this.edges.clear();
		// }

		final VertexImpl start_node = findTheMostDistantNode(multy_graph);
		// Log.d("start_node", toString(start_node));
		final List<VertexImpl> visiting_order = Collections.newList();
		visiting_order.add(start_node);

		final EdgeImpl start_direction = findNextBorderEdge(start_node, null);
		// Log.d("start_direction", toString(start_direction));

		final List<EdgeImpl> testedExits = Collections.newList();
		final List<EdgeImpl> transitions = Collections.newList();
		transitions.add(start_direction);
		testedExits.add(new EdgeImpl(start_direction));

		EdgeImpl current_direction = start_direction;
		VertexImpl currentNode = start_direction.getRightNode();

		visiting_order.add(currentNode);

		boolean cycle_closed = false;
		while (!cycle_closed) {
			// Log.d(" leaving", toString(currentNode));
			current_direction = findNextBorderEdge(current_direction);
			currentNode = current_direction.getRightNode();
			// Log.d(" taking course", toString(current_direction));
			// Log.d(" destination", toString(currentNode));

			if (noNewExitsFromHere(current_direction, testedExits)) {

				cycle_closed = true;

			} else {
				// not_visited_nodes.remove(currentNode);

			}
			visiting_order.add(currentNode);
			transitions.add(current_direction);
			testedExits.add(new EdgeImpl(current_direction));
			// printNodes("visiting_order", visiting_order, this);
			// printEdges("transitions", transitions, this);

		}
		// printNodes("visiting_order", visiting_order, this);
		// printEdges("transitions", transitions, this);

		return visiting_order;
	}

	static public  void removeDuplicatingEdges (final Ploy2DGraph multy_graph) {
		final List<EdgeImpl> edges_to_remove = Collections.newList();
		for (int i = 0; i < multy_graph.edges.size(); i++) {
			final EdgeImpl one = multy_graph.edges.getElementAt(i);
			for (int k = i + 1; k < multy_graph.edges.size(); k++) {
				final EdgeImpl other = multy_graph.edges.getElementAt(k);
				if (isDuplicatingEdge(one, other, multy_graph)) {
					// Log.d("reduction", toString(one), "is duplicated by ",
					// toString(other));

					edges_to_remove.add(other);
				}
			}
		}
		multy_graph.edges.removeAll(edges_to_remove);
	}

	static private  CyclePathImpl toCycleGraph (final Ploy2DGraph multy_graph,
		final List<VertexImpl> color) {
		final int n = color.size();
		final CyclePathImpl cycle = new CyclePathImpl();
		cycle.setNumberOfVetices(n);
		for (int i = 0; i < n; i++) {
			//
			cycle//
				.getState(i)//
				.getVertex().putVertexObject(Geometry.newFloat2());

			final Float2 point = cycle//
				.getState(i)//
				.getVertex()//
				.getVertexObject()//
			;

			point.set(//
				color//
					.getElementAt(i)//
					.getVertexObject());
		}
		return cycle;
	}
}
