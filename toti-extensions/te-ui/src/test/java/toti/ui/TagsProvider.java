package toti.ui;

import java.util.List;

import toti.lib.templating.Tag;

public class TagsProvider {

	public List<Tag> getTags() {
		return new UiExtension().getTags();
	}
	
}
