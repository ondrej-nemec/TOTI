totiForm = {
	init: function(elementIdentifier, uniqueName, config) {
		$(document).ready(function() {
			var html = $(elementIdentifier).html();
			$(elementIdentifier).html(totiForm.print(uniqueName, config, $(elementIdentifier)));
			if (config.hasOwnProperty('bind')) {
				totiForm.bind(config.bind, uniqueName, function() {
					if (config.hasOwnProperty("beforeBind")) {
						window[config.beforeBind]();
					}
				}, function() {
					if (config.hasOwnProperty("afterBind")) {
						window[config.afterBind]();
					}
				});
			}
		});
	},
	print: function(uniqueName, config, element) {
		var formId = uniqueName;
		var form;
		if (config.editable) {
			var errors = $('<div>').attr("id", config.uniqueName + "-errors-form").append($('<span>'));
			form = $('<form>')
				.attr("id", formId)
				.attr("action", config.action)
				.attr("method", config.method)
				.append(errors);
		} else {
			form = $('<div>')
				.attr("id", formId);
		}
		var table = $('<table>');

		var printSelectFunc = function (field, optionsName) {
			var input = $('<div>');
			var options = field[optionsName];
			delete field[optionsName];
			for ([key, name] of Object.entries(field)) {
				input.attr(key, name);
			}
			options.forEach(function(option) {
				input.append($('<span>').attr("value", option.value).text(option.title).hide());
			});
			return input;
		}
		config.fields.forEach(function(field, index) {
			field.id = uniqueName + "-" + field.id;
			field.form = formId;
			var label = null;
			if (field.hasOwnProperty('title') && field.type !== 'button') {
				label = totiControl.inputs.label(field.id, field.title, {
					id: uniqueName +  "-" + field.id + "-label"
				});
			}
			var input;
			if (!config.editable && field.type !== 'button') {
				if (field.type === 'select') {
					input = printSelectFunc(field, 'options');
				} else if (field.type === 'radio') {
					input = printSelectFunc(field, 'radios');
				} else if (field.type === 'checkbox') {
					input = printSelectFunc(field, 'values');
				} else if (field.type !== 'submit' && field.type !== 'hidden') {
					input = $('<div>');
					for ([key, name] of Object.entries(field)) {
						input.attr(key, name);
					}
				}
			} else if (field.type === 'submit') {
				input = totiControl.inputs.submit(
					field.ajax, function(data) {
						if (field.hasOwnProperty("confirmation")) {
							return totiControl.display.confirm(field.confirmation, data);
						}
						return true;
					}, field
				);
			} else if (field.type === 'select') {
				var options = [];
				field.options.forEach(function(option) {
					var params = {};
					if (option.hasOwnProperty('params')) {
						params = option.params;
					}
					options.push(totiControl.inputs.option(option.value, option.title, params));
				});
				delete field.options;
				input = totiControl.inputs[field.type](options, field);
			} else if (field.type === 'button') {
				var onClick = {
						href: field.href,
						method: field.method,
						async: field.ajax,
						submitConfirmation: function() {
							if (field.hasOwnProperty('confirmation')) {
								return totiControl.display.confirm(field.confirmation, row);
							}
							return true;
						} 
					};
				if (field.hasOwnProperty('style')) {
					onClick.type = field.style;
				}
				input = totiControl.inputs.button(onClick, field.title, field.params, field.hasOwnProperty('renderer') ? field.renderer : null, field.preventDefault);
			} else if (field.type === 'radio') {
				input = $("<div>");
				field.radios.forEach(function(radio) {
					var item = $('<div>');
					var id = formId + "-" + radio.id;
					item.attr('id', id + "-block");
					if (radio.hasOwnProperty('title')) {
						item.append(totiControl.inputs.label(field.id, radio.title, {
							id: id + "-label"
						}));
					}
					var settings = {
							id: id,
							name: field.name,
							form: formId,
							value: radio.value
						};
					if (radio.value === field.value) {
						settings.checked = "checked";
					}
					if (field.hasOwnProperty('required')) {
						settings.required = field.required;
					}
					if (field.hasOwnProperty('disabled')) {
						settings.disabled = field.disabled;
					}
					item.append(totiControl.inputs.radio(settings));
					input.append(item);
				});
			} else {
				if (field.type === 'file') {
					form.attr("enctype", "multipart/form-data");
				}
				var fieldType = field.type;
				delete field.type;
				input = totiControl.inputs[fieldType](field);
			}

			var error = $('<div>').attr('id', uniqueName + '-errors-' + field.name);
			if (element.html().length > 0) {
				var labelElement = element.find("#form-label-" + field.name);
				if (labelElement.length > 0) {
					labelElement.html(label);
				}
				var inputElement = element.find("#form-input-" + field.name);
				if (inputElement.length > 0) {
					inputElement.html(input);
				}
				var errorElement = element.find("#form-error-" + field.name);
				if (config.editable && errorElement.length > 0) {
					errorElement.html(error);
				}
			} else {
				table.append($('<tr>')
					.append($('<td>').attr('class', 'toti-form-label').append(label))
					.append($('<td>').attr('class', 'toti-form-input').append(input))
					.append($('<td>').attr('class', 'toti-form-error').append(config.editable ? error : ""))
				);
			}
		});
		if (element.html().length > 0) {
			form.append(element.html());
		} else {
			form.append(table);
		}
		
		return form;
	},
	bind: function(bind, formId, beforeBind, afterBind) {
		totiControl.load.ajax(
			bind.url, 
			bind.method, 
			bind.params, 
			function(values) {
				beforeBind();
				for (const[key, value] of Object.entries(values)) {
					var val = value; /* TODO IMPROVEMENT escape */
					var id = '#' + formId + ' [name=' + key + ']';
					var element = $(id);
					if (element.attr("type") === 'datetime-local' && val !== null) {
						val = val.replace(" ", "T");
					}
					if (element.children('span').length > 0) { /* detail:select, checkbox, radio */
						element.children('span[value="' + val + '"]').show();
					} else if (element.length > 1) {
						$('#' + formId + ' #' + formId + '-id-' + val + '[name=' + key + ']').prop('checked', true); /* form: radio list */
					} else {
						element.val(val); /* form */
						if (element.text().length == 0) {
							element.text(val); /* detail */
						}
						element.prop('checked', val); /* form: checkbox */
					}
				}
				afterBind();
			}, 
			function(xhr) {
				if (bind.hasOwnProperty('onFailure')) {
					bind.onFailure(xhr);
				} else {
					totiControl.display.flash('error', totiLang.formMessages.bindError);
				}
			},
			totiControl.getHeaders()
		);
	}
};