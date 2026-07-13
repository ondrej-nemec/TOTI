package toti.core.logging;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import toti.lib.common.functions.Implode;

public class Block {

	private final List<Supplier<String>> data = new LinkedList<>();

	private final String template;
	private final PageBuilder pageBuilder;

	public Block(PageBuilder pageBuilder, String  template) {
		this.template = template;
		this.pageBuilder = pageBuilder;
	}

	public Block addH1(String text) {
		return addBlock(pageBuilder.createH1(), b->b.addText(text));
	}

	public Block addH2(String text) {
		return addBlock(pageBuilder.createH2(), b->b.addText(text));
	}

	public Block addH3(String text) {
		return addBlock(pageBuilder.createH3(), b->b.addText(text));
	}

	public Block addText(String text) {
		data.add(()->text);
		return this;
	}

	public Block addParagraph(String text) {
		return addParagraph(b->b.addText(text));
	}

	public Block addParagraph(Consumer<Block> onBlock) {
		return addBlock(pageBuilder.createParagraph(), onBlock);
	}

	public Block addCard(Consumer<Block> onBlock) {
		return addBlock(pageBuilder.createCard(), onBlock);
	}

	public Block addCode(String text) {
		return addBlock(pageBuilder.createCode(), b->b.addText(text));
	}

	public Block addSection(String title, int level, boolean collapsed, Consumer<Block> onBlock) {
		return addBlock(pageBuilder.createSection(title, level, collapsed), onBlock);
	}

	private Block addBlock(Block block, Consumer<Block> onBlock) {
		onBlock.accept(block);
		data.add(()->block.create());
		return this;
	}

	public String create() {
		return String.format(template, Implode.implode(b->b.get(), "", data));
	}

}
