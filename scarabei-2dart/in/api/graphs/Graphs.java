
package com.jfixby.scarabei.api.graphs;

import com.jfixby.scarabei.api.ComponentInstaller;
import com.jfixby.scarabei.api.collections.EditableCollection;
import com.jfixby.scarabei.api.floatn.ReadOnlyFloat2;

public class Graphs {

	static private ComponentInstaller<GraphsComponent> componentInstaller = new ComponentInstaller<GraphsComponent>("Graphs");

	public static final void installComponent (final GraphsComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static void installComponent (final String className) {
		componentInstaller.installComponent(className);
	}

	public static final GraphsComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final GraphsComponent component () {
		return componentInstaller.getComponent();
	}

//	public static <VertexType, EdgeType> MultiGraph<VertexType, EdgeType> newUndirectedGraph () {
//		return invoke().newUndirectedGraph();
//	}
//
//	public static <EdgeType> PolyGraph<EdgeType> newPolyGraph (final EditableCollection<? extends ReadOnlyFloat2> vertices) {
//		return invoke().newPolyGraph(vertices);
//	}

}
