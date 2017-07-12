
package com.jfixby.scarabei.red.graphs;

import com.jfixby.scarabei.api.collections.EditableCollection;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.floatn.Float2;
import com.jfixby.scarabei.api.floatn.ReadOnlyFloat2;
import com.jfixby.scarabei.api.graphs.PathInGraph;
import com.jfixby.scarabei.api.graphs.PolyGraph;

public class Ploy2DGraph<EdgeType> extends MultiGraphImpl implements PolyGraph<EdgeType> {

	public Ploy2DGraph () {
		super();
	}

	@Override
	public List<PathInGraph> extractSimpleCycles () {
		List<PathInGraph> cycles = PolyGraphUtils.extractSimpleCycles(this);
		return cycles;
	}

	public static  PolyGraph<EdgeType> newPloy2DGraph (EditableCollection<ReadOnlyFloat2> points) {
		Ploy2DGraph<EdgeType> returl = PolyGraphUtils.newMultiGraph(points);
		return returl;
	}

}
