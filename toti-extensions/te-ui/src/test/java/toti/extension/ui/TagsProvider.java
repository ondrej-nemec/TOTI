package toti.extension.ui;

import java.util.List;

import toti.extension.ui.UiExtension;
import toti.lib.templating.Tag;

public class TagsProvider {

	public List<Tag> getTags() {
		return new UiExtension().getTags();
	}
	
}
