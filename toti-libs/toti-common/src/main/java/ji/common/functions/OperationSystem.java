package ji.common.functions;

// TODO improve and add to documentation
public enum OperationSystem {
	
	NEW_LINE("\r\n", "\n"),
	PATH_SEPARATOR("\\", "/"),
	PRE_COMMAND("cmd /c ", ""),
	PRE_FILE("", "./"),
	CLI_EXTENSION(".bat", ""); //.sh
	
	enum Supported {
		LINUX,
		WINDOWS,
		MAC
	}
	
	private final String linux;
	
	private final String windows;
	
	private OperationSystem(String windows, String linux) {
		this.linux = linux;
		this.windows = windows;
	}
	
	@Override
	public String toString() {
		Supported type = getActualOs();
		switch (type) {
		case LINUX: return linux;
		case WINDOWS: return windows;
		default:
			throw new RuntimeException("Unsupported operation system type: " + type);
		}
	}	
	
	public static Supported getActualOs() {
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("win") >= 0) {
			return Supported.WINDOWS;
		}
		if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
			return Supported.LINUX;
		}
		if (OS.indexOf("mac") >= 0) {
			return Supported.MAC;
		}
		return Supported.WINDOWS;
	}
	
}

/*
	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
*/
