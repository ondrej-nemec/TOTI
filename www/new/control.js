var totiControl = {
	inputs: {
		_createInput: function (type, attributes) {
			var input = document.createElement("input");
			input.setAttribute("type", type);
			for ([key, name] of Object.entries(attributes)) {
				input.setAttribute(key, name);
			}
			return input;
		},
		label: function (forInput, title, params = {}) {
			var label = document.createElement("label");
			label.innertHTML = title;
			label.setAttribute("for", forInput);
			for ([key, name] of Object.entries(params)) {
				label.setAttribute(key, name);
			}
			return label;
		},
		datetime: function (params = {}) {
			return totiControl.inputs._createInput("datetime-local", params);
		},
		/* sugested params: cols, rows */
		textarea: function(params = {}) {
			var textarea = document.createElement('textarea');
			for ([key, name] of Object.entries(params)) {
				if (key === "value") {
					textarea.innertHTML = name;
				} else {
					textarea.setAttribute(key, name);
				}
			}
			return textarea;
		},
		select: function (options, params = {}) {
			var select = document.createElement('select');
			for ([key, name] of Object.entries(params)) {
				select.setAttribute(key, name);
			}
			options.forEach(function(option, index) {
				select.appendChild(option);
			});
			return select;
		},
		option: function(value, title, params = {}) {
			var option = document.createElement('option');
			option.setAttribute("value", value);
			option.innertHTML = title;
			for ([key, name] of Object.entries(params)) {
				option.setAttribute(key, name);
			}
			return option;
		},
		/* onClick: function | object with settings: href, method, async, submitConfirmation (onSuccess, onFailure¨, type) */
		button: function (onClick, title = "", params = {}, renderer = null) {
			if (renderer === null) {
				//renderer = totiControl.inputs._createInput("button", params);
				renderer = document.createElement('button');
				renderer.innertHTML = title;
			}
			var button = renderer;
			for ([key, name] of Object.entries(params)) {
				button.setAttribute(key, name);
			}
			if (typeof onClick === 'object') {
				var originalClass = button.getAttribute("class");
				if (originalClass === undefined) {
					originalClass = "";
				}
				button.setAttribute("class", originalClass + " toti-button-" + onClick.type);
				var clickSettings = onClick;
				onClick = function(event) {
					event.preventDefault();
					if (clickSettings.submitConfirmation !== null
						 && clickSettings.submitConfirmation !== undefined 
						 && !clickSettings.submitConfirmation()) {
						return false;
					}
					if (clickSettings.async) {
						totiLoad.ajax(clickSettings.href, clickSettings.method, {}, totiLoad.getHeaders(), function(res) {
							if (clickSettings.hasOwnProperty('onSuccess')) {
								window[clickSettings.onSuccess](res);
							} else {
								totiDisplay.flash("success", res);
							}
						}, function(xhr) {
							if (clickSettings.hasOwnProperty('onError')) {
								window[clickSettings.onError](xhr);
							} else {
								totiDisplay.flash("error", xhr);
							}
						});
					} else {
						/* totiControl.load.link(href, method, {}, totiControl.getHeaders());*/
						window.location = clickSettings.href;
					}
				};
			}
			button.onclick = onClick;
			return button;
		},
		submit: function (async = true, submitConfirmation = null, params = {}) {
			var submit = totiControl.inputs._createInput("submit", params);
			submit.onclick = function(event) {
				document.getElementByClass('error-list').remove();
				var form = document.forms[submit.getAttribute("form")];
				if (!form[0].reportValidity()) {
					return false;
				}

				var data = {};
				$.each(form.serializeArray(), function(index, item) {
					var input = form.find('[name="' + item.name + '"]');
					var value = item.value;
					if (input.attr('type') === 'datetime-local') {
						value = value.replace("T", " ");
					}
					data[item.name] = value;
				});
				var formConfig = {};
				var useFiles = false;
				form.find('[type="file"]').each(function() {
					useFiles = useFiles ? true : $(this).val().length > 0;
				});
				if (form.attr("enctype") !== undefined && useFiles) {
					var formData = new FormData(form[0]);
					for (var key of formData.keys()) {
						if (form.find('input[name="' + key + '"]').attr('type') === 'datetime-local') {
							formData.set(key, formData.get(key).replace("T", " "));
						}
					}
					/*formData.append("thefile", file);*/
					
				}

				if (submitConfirmation !== null && !submitConfirmation(data)) {
					event.preventDefault();
					return false;
				}
				if (async) {
					event.preventDefault();
					var header = totiControl.getHeaders();
					if (form.attr("enctype") !== undefined) {
						header.enctype = form.attr("enctype");
					}
					totiControl.load.ajax(
						form.attr("action"), 
						form.attr("method"), 
						data, 
						function(response) {
							if (element.attr("onSuccess") != null) {
								window[element.attr("onSuccess")](response);
							} else {
								totiControl.display.flash('success', response.message);
							}
							if (element.attr("redirect") != null) {
								totiControl.display.storedFlash('success', response.message);
								window.location = element.attr("redirect").replace("{id}", response.id);
							}
						}, 
						function(xhr) {
							if (xhr.status === 400) {
								for (const[key, list] of Object.entries(JSON.parse(xhr.responseText))) { /* xhr.responseJSON*/
									var ol = $('<ul>').attr("class", "error-list");
									list.forEach(function(item) {
										ol.append($('<li>').text(item));
									});
									$('#' + form.attr('id') + '-errors-' + key + '').html(ol);
								}
							} else if (element.attr("onFailure") != null) {
								window[element.attr("onFailure")](xhr);
							} else {
								totiControl.display.flash('error', totiLang.formMessages.saveError);
							}
						}, 
						header,
						formConfig
					);
				}
			};
			return submit;
		}
	}
};