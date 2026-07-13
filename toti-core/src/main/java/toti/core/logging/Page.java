package toti.core.logging;

import java.util.function.Consumer;

public class Page {

	private final String title;
	private final String primaryColor;
	private final PageBuilder builder;
	private final Block body;

	public static Page primary(String title, Consumer<Block> createPage) {
		return new Page(title, PageBuilder.PRIMARY, createPage);
	}

	public static Page success(String title, Consumer<Block> createPage) {
		return new Page(title, PageBuilder.SUCCESS, createPage);
	}

	public static Page waring(String title, Consumer<Block> createPage) {
		return new Page(title, PageBuilder.WARNING, createPage);
	}

	public static Page error(String title, Consumer<Block> createPage) {
		return new Page(title, PageBuilder.ERROR, createPage);
	}

	private Page(String title, String primaryColor, Consumer<Block> createPage) {
		this.title = title;
		this.primaryColor = primaryColor;
		this.builder = new PageBuilder();
		this.body = new Block(builder, "%s");
		createPage.accept(body);
	}

	public String create() {
		return builder.getRootTemplate(title, primaryColor, body);
	}

}
