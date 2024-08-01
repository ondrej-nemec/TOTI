package ji.common.functions;

public class FilesListEndToEndTest {

	public static void main(String[] args) {
		for (String folder : new String[] {"tests", "json"}) {
			try {
				System.out.println("-- Folder: " + folder);
				FilesList l = FilesList.get(folder, true);
				System.out.println(l.getURL());
				l.getFiles().forEach((file)->{
					System.out.println("* " + file);
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
