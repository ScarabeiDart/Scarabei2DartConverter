
package com.jfixby.scarabei.scarabei2dart.api;

import java.io.IOException;

import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.log.L;

public class TestFS {

	public static void main (final String[] a) throws IOException {
		ScarabeiDesktop.deploy();

		L.d("home", LocalFileSystem.ApplicationHome().listDirectChildren());
		L.d("root", LocalFileSystem.ROOT().listDirectChildren());

	}

}
