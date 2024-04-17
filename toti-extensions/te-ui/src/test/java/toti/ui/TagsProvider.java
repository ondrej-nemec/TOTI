package toti.ui;

import java.util.List;

import toti.templating.Tag;

public class TagsProvider {

	public List<Tag> getTags() {
		return new UiExtension().getTags();
	}
	
}
