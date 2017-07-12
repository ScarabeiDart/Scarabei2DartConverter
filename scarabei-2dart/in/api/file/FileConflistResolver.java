
package com.jfixby.scarabei.api.file;

import java.io.IOException;

public interface FileConflistResolver {

	public boolean overwrite (File fileToCopy, File existing) throws IOException;

	public static final FileConflistResolver OVERWRITE_ALL = new OVERWRITE_ALL();
		
//		new FileConflistResolver() {
//		@Override
//		final public boolean overwrite (final File fileToCopy, final File existing) {
//			return true;
//		}
//	};

	public static final FileConflistResolver OVERWRITE_ON_HASH_MISMATCH = new OVERWRITE_ON_HASH_MISMATCH();
	
//	new FileConflistResolver() {
//		@Override
//		final public boolean overwrite (final File fileToCopy, final File existing) throws IOException {
//			return !fileToCopy.calculateHash().equals(existing.calculateHash());
//		}
//	};

	public static final FileConflistResolver OVERWRITE_IF_NEW = new OVERWRITE_IF_NEW();
	
//	new FileConflistResolver() {
//		@Override
//		final public boolean overwrite (final File fileToCopy, final File existing) throws IOException {
//			return fileToCopy.lastModified() > existing.lastModified();
//		}
//	};

}
