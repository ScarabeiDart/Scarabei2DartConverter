
package com.jfixby.scarabei.api.util;

import com.jfixby.scarabei.api.ComponentInstaller;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.collections.Sequence;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.util.path.AbsolutePath;
import com.jfixby.scarabei.api.util.path.MountPoint;
import com.jfixby.scarabei.api.util.path.RelativePath;

public class JUtils {

	static private ComponentInstaller<UtilsComponent> componentInstaller = new ComponentInstaller<UtilsComponent>("JUtils");

	public static final void installComponent (final UtilsComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static void installComponent (final String className) {
		componentInstaller.installComponent(className);
	}

	public static final UtilsComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final UtilsComponent component () {
		return componentInstaller.getComponent();
	}

	public static final RelativePath newRelativePath (final String path_string) {
		return invoke().newRelativePath(path_string);
	}

	public static final  AbsolutePath<T> newAbsolutePath (final T mount_point, final RelativePath relative) {
		return invoke().newAbsolutePath(mount_point, relative);
	}

	public static final  AbsolutePath<T> newAbsolutePath (final T mount_point) {
		return invoke().newAbsolutePath(mount_point);
	}

	public static final RelativePath newRelativePath (final Collection<String> steps_list) {
		return invoke().newRelativePath(steps_list);
	}

	public static final RelativePath newRelativePath (final java.util.List<String> steps_list) {
		return invoke().newRelativePath(steps_list);
	}

	public static final RelativePath newRelativePath () {
		return invoke().newRelativePath();
	}

	public static  StateSwitcher newStateSwitcher (final T default_state) {
		return invoke().newStateSwitcher(default_state);
	}

	public static List<String> split (final String input_string, final String splitter) {
		return invoke().split(input_string, splitter);
	}

	public static String newString (final ByteArray data) {
		return invoke().newString(data);
	}


	public static String truncated (final String data, final int begin_char, final int end_char) {
		return invoke().truncated(data, begin_char, end_char);
	}

	public static boolean equalObjects (final Object a, final Object b) {
		return invoke().equalObjects(a, b);
	}

	public static BinaryCode binaryCodeOf (final int bits, final int numberOfBits) {
		return invoke().binaryCodeOf(bits, numberOfBits);
	}

	public static EditableBinaryCode newBinaryCode () {
		return invoke().newBinaryCode();
	}

	public static ByteArray newByteArray (final int size) {
		return invoke().newByteArray(size);
	}

	public static ByteArray newByteArray (final byte[] bytes) {
		return invoke().newByteArray(bytes);
	}

	public static String newString (final char[] chars) {
		return invoke().newString(chars);
	}

	public static String newString (final byte[] bytes) {
		return invoke().newString(bytes);
	}

	public static String newString (final byte[] bytes, final String encoding) {
		return invoke().newString(bytes, encoding);
	}

	public static String newString (final ByteArray bytes, final String encoding) {
		return invoke().newString(bytes, encoding);
	}

	public static String replaceAll (final String input, final Map<String, String> termsMapping) {
		return invoke().replaceAll(input, termsMapping);
	}

	public static ProgressIndicator newProgressIndicator () {
		return invoke().newProgressIndicator();
	}

	public static String prefix (final String string, final int offset) {
		return invoke().prefix(string, offset);
	}

	public static String wrapSequence (final Sequence<String> seq, final int size, final String bracketLeft,
		final String bracketRight, final String separator) {
		return invoke().wrapSequence(seq, size, bracketLeft, bracketRight, separator);
	}

	

}
