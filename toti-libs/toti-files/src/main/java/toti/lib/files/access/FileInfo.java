package toti.lib.files.access;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInfo {

	private final FileType type;
	private final FileMode mode;
	private final String name;
	private final String extension;
	private final String relativePath;
	private final String absolutePath;
	private final long lastModificationTime;

	//private final String search;
	private final String fullName;

    public FileInfo(FileType type, FileMode mode, String name, String relativePath, String absolutePath, long lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
		this.type = type;
		this.mode = mode;

        this.relativePath = relativePath;
        this.absolutePath = absolutePath;
		this.fullName = name;
		// TODO this need tests
	//	this.search = relativePath.replace(name, "");
		FileName ext = FileUtils.parseName(name);
		this.name = ext.name();
		this.extension = ext.extension();
    }

	public String getFullName() {
		return fullName;
	}

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public FileMode getMode() {
        return mode;
    }

    public FileType getType() {
        return type;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

	public boolean isDirectory() {
		return type == FileType.DIRECTORY;
	}

	public boolean isFile() {
		return type == FileType.FILE;
	}

    public long getLastModificationTime() {
        return lastModificationTime;
    }

	public InputStream createInputStream() throws IOException {
		if (!isFile()) {
			throw new IOException("Path: " + absolutePath + " is no file. Probable it is dirrectory");
		}
		if (mode == FileMode.JAR || mode == FileMode.RESOURCE) {
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath);
		}
		return new FileInputStream(relativePath);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + ((relativePath == null) ? 0 : relativePath.hashCode());
		result = prime * result + ((absolutePath == null) ? 0 : absolutePath.hashCode());
		result = prime * result + (int) (lastModificationTime ^ (lastModificationTime >>> 32));
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (partialEquals(obj)) {
			FileInfo other = (FileInfo) obj;
			return lastModificationTime != other.lastModificationTime;
		}
		return false;
	}

	protected boolean partialEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (type != other.type)
			return false;
		if (mode != other.mode)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (relativePath == null) {
			if (other.relativePath != null)
				return false;
		} else if (!relativePath.equals(other.relativePath))
			return false;
		if (absolutePath == null) {
			if (other.absolutePath != null)
				return false;
		} else if (!absolutePath.equals(other.absolutePath))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileInfo [T=" + type + ", M=" + mode + ", Name=" + fullName + ", RelPath=" + relativePath + ", AbsPath="
				+ absolutePath + ", LMT=" + lastModificationTime + "]";
	}

	



}
