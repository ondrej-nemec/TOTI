package mvc.control.gridColumns;

import java.util.LinkedList;
import java.util.List;

import mvc.control.Html;

public class GridButton {
	
	private boolean defaultEdit = true;
	private boolean defaultDelete = true;
	private boolean modeSwitchButton = true;
	private boolean clearFilterButton = true;
	private String deleteConfirmMessage = "Do you really wish delete this item?";
			// "The client ' + item.Name + ' will be removed. Are you sure?";
	private String width = "50";
	
	private Html itemTemplate = null;
	private Html headerTemplate = null;
	private List<Html> buttons = null;
	
	public boolean isDefaultEdit() {
		return defaultEdit;
	}
	
	public boolean isDefaultDelete() {
		return defaultDelete;
	}
	
	public boolean isModeSwitchButton() {
		return modeSwitchButton;
	}
	
	public boolean isClearFilterButton() {
		return clearFilterButton;
	}
	
	public String getWidth() {
		return width;
	}
	
	public Html getItemTemplate() {
		if (itemTemplate != null) {
			return itemTemplate;
		}
		if (buttons != null) {
			StringBuilder htmls = new StringBuilder();
			buttons.forEach((button)->{
				htmls.append(button + " ");
			});
			return Html.element("span").html(htmls);
		}
		return null;
	}
	
	public Html getHeaderTemplate() {
		return headerTemplate;
	}
	
	public void setDefaultEdit(boolean defaultEdit) {
		this.defaultEdit = defaultEdit;
	}
	
	public void setDefaultDelete(boolean defaultDelete) {
		this.defaultDelete = defaultDelete;
	}
	
	public void setModeSwitchButton(boolean modeSwitchButton) {
		this.modeSwitchButton = modeSwitchButton;
	}
	
	public void setClearFilterButton(boolean clearFilterButton) {
		this.clearFilterButton = clearFilterButton;
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
	public void setItemTemplate(Html itemTemplate) {
		this.itemTemplate = itemTemplate;
	}
	
	public void setHeaderTemplate(Html headerTemplate) {
		this.headerTemplate = headerTemplate;
	}

	public String getDeleteConfirmMessage() {
		return Html.replaceItemValues(deleteConfirmMessage);
	}

	public void setDeleteConfirmMessage(String deleteConfirmMessage) {
		this.deleteConfirmMessage = deleteConfirmMessage;
	}
	
	public void addButton(Html button) {
		if (buttons == null) {
			buttons = new LinkedList<>();
		}
		buttons.add(button);
	}
	
}
