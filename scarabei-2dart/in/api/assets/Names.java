
package com.jfixby.scarabei.api.assets;

import com.jfixby.scarabei.api.ComponentInstaller;
import com.jfixby.scarabei.api.collections.Collection;

public class Names {

	static private ComponentInstaller<AssetsNamespaceComponent> componentInstaller = new ComponentInstaller<AssetsNamespaceComponent>(
		"Name");

	public static final void installComponent (final AssetsNamespaceComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static void installComponent (final String className) {
		componentInstaller.installComponent(className);
	}

	public static final AssetsNamespaceComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final AssetsNamespaceComponent component () {
		return componentInstaller.getComponent();
	}

	public static ID newID (final String asset_id_string) {
		return invoke().newID(asset_id_string);
	}

	public static String SEPARATOR () {
		return invoke().SEPARATOR();
	}

//	public static <T> NamespaceRegistry<T> newRegistry () {
//		return invoke().newRegistry();
//	}

	public static ID ROOT () {
		return invoke().ROOT();
	}

	public static ID newID (final Collection<String> list) {
		return invoke().newID(list);
	}

}
