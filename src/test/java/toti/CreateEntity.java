package toti;

import java.io.File;
import java.io.IOException;

import ji.common.functions.FileExtension;
import ji.files.text.Text;
import ji.files.text.basic.ReadText;
import ji.files.text.basic.WriteText;

public class CreateEntity {

	
	public void generate(String destination, String ...entities) throws IOException {
		for (String entity : entities) {
			String domain = entity.substring(0, 1).toLowerCase() + entity.substring(1);
			String name = entity.substring(0, 1).toUpperCase() + entity.substring(1);
			String[] files = new String[] {
				"/toti/entity/api/EntityApiController.txt",
				"/toti/entity/api/EntityDao.txt",
				"/toti/entity/api/EntityDaoDatabase.txt",
				"/toti/entity/api/EntityValidator.txt",
				"/toti/entity/web/EntityPageController.txt"
			};
			for (String file : files) {
				createFile(
					file,
					destination + "/"
							+ new FileExtension(new File(file).getName()).getName().replace("Entity", name) 
							+ ".java",
					name, domain);
			}
			createFile("/toti/entity/web/control.jsp", destination + "/" + name + ".jsp", name, domain);
		}
	}
	
	private void createFile(String fileName, String destination, String name, String domain) throws IOException {
		String content = Text.get().read((br)->{
			return ReadText.get().asString(br);
		}, getClass().getResourceAsStream(fileName)).replaceAll("__X__", name).replaceAll("__Y__", domain);
		Text.get().write((bw)->{
			WriteText.get().write(bw, content);
		}, destination, false);
	}
	
	public static void main(String[] args) {
		CreateEntity e = new CreateEntity();
		try {
			e.generate("src/test/java/toti/entity", "Entity");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
