	<div id="${gridName}"></div>
	<div>
		<div id="${gridName}-pager" style="float:left"></div>
		<div id="${gridName}-actions"></div>
	</div>
	
	<script>
		jsGrid.locales.locale = {
		    grid: {
	            noDataContent: "Not found",
	            deleteConfirm: "Are you sure?",
	            pagerFormat: "Pages: {first} {prev} {pages} {next} {last} &nbsp;&nbsp; {pageIndex} to {pageCount}",
	            pagePrevText: "Previous",
	            pageNextText: "Next",
	            pageFirstText: "First",
	            pageLastText: "Last",
	            loadMessage: "Please, wait...",
	            invalidMessage: "Invalid data entered!"
	        },

	        loadIndicator: {
	            message: "Please, wait 2 ..."
	        },

	        fields: {
	            control: {
	                searchModeButtonTooltip: "Switch to searching",
	                insertModeButtonTooltip: "Switch to inserting",
	                editButtonTooltip: "Edit",
	                deleteButtonTooltip: "Delete",
	                searchButtonTooltip: "Search",
	                clearFilterButtonTooltip: "Clear filter",
	                insertButtonTooltip: "Insert",
	                updateButtonTooltip: "Update",
	                cancelEditButtonTooltip: "Cancel edit"
	            }
	        },

	        validators: {
	            required: { message: "Field is required" },
	            rangeLength: { message: "La longitud del valor está fuera del intervalo definido" },
	            minLength: { message: "La longitud del valor es demasiado corta" },
	            maxLength: { message: "La longitud del valor es demasiado larga" },
	            pattern: { message: "El valor no se ajusta al patrón definido" },
	            range: { message: "Valor fuera del rango definido" },
	            min: { message: "Valor demasiado bajo" },
	            max: { message: "Valor demasiado alto" }
	        }
		};
		jsGrid.locale("locale");
		
		var DateTime = function(config) {
		    jsGrid.Field.call(this, config);
		};		 
		DateTime.prototype = new jsGrid.Field({
		    align: "center",
		    sorter: function(date1, date2) {
		        return new Date(date1) - new Date(date2);
		    },
		    itemTemplate: function(value) {
		        return new Date(value).toLocaleDateString(); // can be parametrized with lang code
		    },
		    insertTemplate: function(value) {
		        return this._insertPicker = $("<input>").attr("type", "datetime-local");
		    },
		    filterTemplate: function() {
		    	return this._insertPicker = $("<input>").attr("type", "datetime-local");
    		},
		    editTemplate: function(value) {
		        return this._editPicker = $("<input>").attr("type", "datetime-local").val(new Date(value).toISOString());
		    },
		    insertValue: function() {
		        return this._insertPicker.val();
		    },
		    filterValue: function() {
		    	return this._insertPicker.val();
		    },
		    editValue: function() {
		        return this._editPicker.val();
		    }
		});
		jsGrid.fields.datetime = DateTime;
		
		var Actions = function(config) {
		    jsGrid.Field.call(this, config);
		};
		Actions.prototype = new jsGrid.Field({
		    align: "center",
		    sorting: false,
		    width: 10,
		    itemTemplate: function(item) {
		        return $("<input>").attr("type", "checkbox").attr("class", "jsgrid-action");
		    },
		    filterTemplate: function() {
		    	var element = $("<input>").attr("type", "checkbox").attr("id", "jsgrid-action");
		    	element.click(function() {
		    		$('.jsgrid-action').prop('checked', $(this).prop('checked'));
		    	});
		    	return this._insertPicker = element;
    		},
    		headerTemplate: function() {
    			return "";
    		}
		});
		jsGrid.fields.actions = Actions;
				
		/*************************/
	
		$("#${gridName}").jsGrid({
	        width: "${width}",
	        height: "${height}",
	
	        filtering: ${filtering},
	        inserting: ${inserting},
	        editing: ${editing},
	    	autoload: ${autoload},
	        sorting: ${sorting},
	        paging: ${paging},
			selecting: ${selecting},
			pageLoading: ${pageLoading}, // every page loaded separately
			
			pageSize: ${pageSize},
	        pageButtonCount: ${pageButtonCount},
	        pagerContainer: '#${gridName}-pager',
	        // TODO parametrized
	        // rowRenderer: function(item) {},
	        // TODO parametrized
	        // rowClick: function(args) {}, // disable row editing
	        confirmDelete: true,
	        <t:if cond='/*${buttons} != null && */((mvc.control.GridButtons)${buttons}).getDeleteConfirmMessage() != null'>
				deleteConfirm: function(item) {
				    return "<t:out var='((mvc.control.GridButtons)${buttons}).getDeleteConfirmMessage()' nonescape />";
				},
	        </t:if>
	        fields: [
	        	<t:if cond='${actions} != null'>
	        	{
	                type: "control",
	    		    align: "center",
	    		    sorting: false,
	    		    width: 10,
	    		    itemTemplate: function(item) {
	    		    	console.log(item);
	    		        return $("<input>").attr("type", "checkbox").attr("class", "jsgrid-action");
	    		    },
	    		    filterTemplate: function() {
	    		    	return "";
	        		},
	        		headerTemplate: function() {
	        			var element = $("<input>").attr("type", "checkbox").attr("id", "jsgrid-action");
	    		    	element.click(function() {
	    		    		$('.jsgrid-action').prop('checked', $(this).prop('checked'));
	    		    	});
	    		    	return this._insertPicker = element;
	        		}
	            },
	            </t:if>
	        	<t:foreach item="mvc.control.Column column" collection="${fields}">
		        	{
		        		name: "<t:out var="column.getName()"/>", // name in data
		        		type: "<t:out var="column.getType()"/>", // text, number, checkbox, select, control
		        		title: "<t:out var="column.getTitle()"/>", // show name
		        		width: "<t:out var="column.getWidth()"/>",
			        	<t:if cond='column.getItemTemplate() != null'>
				        	itemTemplate: function (item) {
			        			return <t:out var='column.getItemTemplate()' nonescape />;
			        		},
			        	</t:if>
			        		<t:if cond='column.getHeaderTemplate() != null'>
				        	headerTemplate: function () {
			        			return <t:out var='column.getHeaderTemplate()' nonescape />;
			        		},
			        	</t:if>
			        	<t:if cond='column.getFilterTemplate() != null'>
			        		filterTemplate: function (item) {
			        			return <t:out var='column.getFilterTemplate()' nonescape />;
			        		},
			        	</t:if>
			        	<t:if cond='column.getInsertTemplate() != null'>
			        		insertTemplate: function (item) {
			        			return <t:out var='column.getInsertTemplate()' nonescape />;
			        		},
			        	</t:if>
			        	<t:if cond="column instanceof mvc.control.SelectColumn">
			        		<t:var type='mvc.control.SelectColumn' name="select" value="(mvc.control.SelectColumn)column"/>
			        		items: <t:out var='select.getOptions()' nonescape />,
			        		valueField: "value",
			        		textField: "text",
			        	</t:if>
			        	sorting: <t:out var='column.isSorting()' />,
				        filtering: <t:out var='column.isFiltering()' />
			        	<%--
		        		// validate: "required",
		        		validate: {
		        			message: "TODO",
		        			// validator: function(value) {return true|false}
		        			// validator: "range", param: [18, 80]
		        			// validator: "rangeLength", param: [10, 250]
		        		},
			        	--%>
		        	},
	        	</t:foreach>
	        	/********/
	        	<t:if cond='${buttons} != null'>
	        		<t:var type='mvc.control.GridButtons' name="buttons" value="(mvc.control.GridButtons)${buttons}"/>
	        		{
		                type: "control",
		        		/*****/
		        		editButton: <t:out var='buttons.isDefaultEdit()' />,
					    deleteButton: <t:out var='buttons.isDefaultDelete()' />,
					    clearFilterButton: <t:out var='buttons.isClearFilterButton()' />,
					    modeSwitchButton: <t:out var='buttons.isModeSwitchButton()' />,
					    width: <t:out var='buttons.getWidth()' />,
		        		/****/
		        		itemTemplate: function (item) {
		    		    	console.log(item);
		        			return "";
		        		},
		        		<t:if cond='buttons.getItemTemplate() != null'>
				        	itemTemplate: function (item) {
			        			return <t:out var='buttons.getItemTemplate()' nonescape />;
			        		},
			        	</t:if>
		        		<t:if cond='buttons.getHeaderTemplate() != null'>
				        	headerTemplate: function () {
			        			return <t:out var='buttons.getHeaderTemplate()' nonescape />;
			        		},
			        	</t:if>
		            }
	        	</t:if>
	        ],
    		controller: {
    		    loadData: function(filter) {
    		    	var d = $.Deferred();
    				 
	                $.ajax({
	                    dataType: "json",
        		        type: "GET",
        		        url: "${apiUrl}",
        		        data: filter
	                }).done(function(response) {
	                	<t:if cond='Boolean.parseBoolean(${pageLoading} + "" )'>
	                    	d.resolve(response);
	                    <t:else />
	                		d.resolve(response.data);
	                    </t:if>
	                });
	 
	                return d.promise();
    		    },
    		    insertItem: function(item) {
    		        return $.ajax({
    		            type: "POST",
    		            url: "${apiUrl}",
    		            data: item
    		        });
    		    },
    		    updateItem: function(item) {
    		        return $.ajax({
    		            type: "PUT",
    		            url: "${apiUrl}",
    		            data: item
    		        });
    		    },
    		    deleteItem: function(item) {
    		        return $.ajax({
    		            type: "DELETE",
    		            url: "${apiUrl}",
    		            data: item
    		        });
    		    }
    		}
		});
		<t:if cond='${actions} != null'>
			$(document).ready(function() {
				var select = $('<select>').attr("id", "action-select");			
				<t:foreach item="common.structures.Tuple2 action" collection="${actions}">
					select.append($("<option>")
							.attr('value', "<t:out var='action._2()'/>")
							.text("<t:out var='action._1()'/>")
					);
				</t:foreach>
				var button = $("<a>").attr("id", "action-select-button").text("TODO send").hide();
				$('#${gridName}-actions').append(select).append(button);
				
				$('#action-select').change(function(){
					$("#action-select-button")
						.attr("href", $(this).children(":selected").val())
						.show();
				});
			});
		</t:if>
	</script>
