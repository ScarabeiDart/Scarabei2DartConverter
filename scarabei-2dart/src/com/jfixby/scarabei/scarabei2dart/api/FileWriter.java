
package com.jfixby.scarabei.scarabei2dart.api;

import java.io.IOException;

import com.jfixby.scarabei.api.file.File;

public class FileWriter {
	StringBuilder b = new StringBuilder();
	String NEW_LINE = "\n";

	public void addLine (final String string) {
		this.b.append(string);
		this.b.append(this.NEW_LINE);
	}

	public void addLine () {
		this.addLine("");
	}

	public void writeTo (final File pubsecFile) throws IOException {
		pubsecFile.writeString(this.b.toString());
	}

}
