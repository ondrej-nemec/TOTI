var actionInputTests = [];
[
	{
		type: 'submit',
		src: false
	},
	{
		type: 'image',
		src: true
	}
].forEach((input)=>{
	function conf(base) {
		if (input.src) {
			base.src = 'icon.png';
		}
		return base;
	}
	actionInputTests.push({
		name: 'Input ' + input.type,
		type: 'group',
		tests: [
			{
				name: 'Basic',
				type: 'test',
				conf: conf({
					type: input.type
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					page.on('dialog', dialog => console.log(dialog.message()));

					assert.areSame('INPUT', await callContainer('tagName'));
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();
					await expect(locator).toHaveAttribute('type', input.type);
					if (input.src) {
						await expect(locator).toHaveAttribute('src', 'icon.png');
					} else {
						await expect(locator).not.toHaveAttribute('src');
					}
					await locator.click(); // with onDialog and no dialog handling, test fails if dialog appears
				}
			},
			{
				name: 'Not editable',
				type: 'test',
				conf: conf({
					type: input.type,
					editable: false
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('SPAN', await callContainer('tagName'));
					await expect(locator).toBeEnabled();
					await expect(locator).toBeHidden();
					await expect(locator).toBeAttached();
					await expect(locator).toHaveText('');
				}
			},
			{
				name: 'Disabled',
				type: 'test',
				conf: conf({
					type: input.type,
					disabled: true
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('INPUT', await callContainer('tagName'));
					await expect(locator).toBeDisabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();
				}
			},
			{
				name: 'Confirmation',
				type: 'test',
				conf: conf({
					type: input.type,
					confirmation: 'Confirmation message'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					page.on('dialog', async(dialog)=>{
						 await dialog.dismiss();
					});

					assert.areSame('INPUT', await callContainer('tagName'));
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					await locator.click(); // TODO verify confirm
					// await expect(locator).toBeDisabled();
				}
			},
			{
				name: 'Title',
				type: 'test',
				conf: conf({
					type: input.type,
					title: 'Send to clouds'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('INPUT', await callContainer('tagName'));
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();
					await expect(locator).toHaveValue('Send to clouds');
					assert.isNull(await callInstance('getValue'));
				}
			},
			{
				name: 'Value',
				type: 'test',
				conf: conf({
					type: input.type,
					value: 'OK'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('INPUT', await callContainer('tagName'));
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					await expect(locator).toHaveValue('');
					assert.areSame('OK', await callInstance('getValue'));
				}
			},
			// submit policy - no tests
			{
				name: 'Submit Sync',
				type: 'test',
				conf: conf({
					type: input.type,
					submitConfiguration: {
						url: '/inputs/submit'
					}
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					var pageUrl = page.url();

					await locator.click();
					// TODO
					assert.areSame(pageUrl.replace('test.html', "redirect.html#42"), page.url());
				}
			},
			{
				name: 'Submit Async Success',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit'
					},
					onSuccess: ['onSubmitSuccess']
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					await locator.click();
					await expect(locator).toBeDisabled();

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));

					await expect(locator).toBeEnabled();

					assert.areSame(1, await callInstance('success'));
					assert.areSame(0, await callInstance('failure'));
				}
			},
			{
				name: 'Submit Async Success Redirect',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit'
					},
					onSuccess: ['onSubmitSuccess'],
					redirect: 'redirect.html#{b}'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					var pageUrl = await page.url();

					await locator.click();
					await expect(locator).toBeDisabled();

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));
					/*await expect(locator).toBeDisabled();
					assert.areSame(1, await callInstance('success'));
					assert.areSame(0, await callInstance('failure'));*/
					await page.waitForURL(pageUrl.replace('test.html', "redirect.html#42"));
				}
			},
			{
				name: 'Submit Async Failure',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit-not-existing'
					},
					onFailure: ['onSubmitFailure']
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					await locator.click();
					await expect(locator).toBeDisabled();

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));

					await expect(locator).toBeEnabled();

					assert.areSame(0, await callInstance('success'));
					assert.areSame(1, await callInstance('failure'));
				}
			},
			{
				name: 'Submit Async Failure Redirect',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit-not-existing'
					},
					onFailure: ['onSubmitFailure'],
					redirect: 'redirect.html'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();

					await locator.click();
					await expect(locator).toBeDisabled();

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));

					await expect(locator).toBeEnabled();

					assert.areSame(0, await callInstance('success'));
					assert.areSame(1, await callInstance('failure'));
				}
			}
		]
	});
});

var buttonTests = [];
[
	{
		name: 'Basic',
		conf: {
			type: 'button'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// page.on('dialog', dialog => console.log(dialog.message()));
			page.on('dialog', dialog=>dialog.dismiss());

			assert.areSame('BUTTON', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();

			await locator.click();
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// page.on('dialog', dialog => console.log(dialog.message()));
			page.on('dialog', dialog=>dialog.dismiss());

			assert.areSame('A', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeHidden();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveAttribute('href', 'test.html');

			await locator.click();
		}
	},
	{
		name: 'Title',
		conf: {
			type: 'button',
			title: 'Magic button'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('BUTTON', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveText('Magic button');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('A', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveAttribute('href', 'test.html');
			await expect(locator).toHaveText('Magic button');
		}
	},
	{
		name: 'Not editable',
		conf: {
			type: 'button',
			title: 'Magic button',
			editable: false
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('SPAN', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeHidden();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveText('');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('SPAN', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeHidden();
			await expect(locator).toBeAttached();
			await expect(locator).not.toHaveAttribute('href');
			await expect(locator).toHaveText('');
		}
	},
	{
		name: 'Disabled',
		conf: {
			type: 'button',
			title: 'Magic button',
			disabled: true
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('BUTTON', await callContainer('tagName'));
			await expect(locator).toBeDisabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveText('Magic button');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('A', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveAttribute('href', '#');
			await expect(locator).toHaveText('Magic button');

			await callInstance('setDisabled', false);
			await expect(locator).toHaveAttribute('href', 'test.html');
			await callInstance('setDisabled', true);
			await expect(locator).toHaveAttribute('href', '#');
		}
	},
	{
		name: 'Confirmation',
		conf: {
			type: 'button',
			title: 'Magic button',
			confirmation: 'Confirmation message'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			page.on('dialog', dialog=>dialog.dismiss());

			assert.areSame('BUTTON', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveText('Magic button');

			await locator.click(); // TODO verify confirm
			// await expect(locator).toBeDisabled();
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			page.on('dialog', dialog=>dialog.dismiss());

			assert.areSame('A', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveAttribute('href', 'test.html');
			await expect(locator).toHaveText('Magic button');

			await locator.click(); // TODO verify confirm
			// await expect(locator).toBeDisabled();
		}
	},
	{
		name: 'Icon',
		conf: {
			type: 'button',
			icon: 'bi bi-pen'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('BUTTON', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();

			var icon = await locator.locator('i');
			await expect(icon).toBeAttached();
			await expect(icon).toHaveClass('bi bi-pen');

		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('A', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeHidden();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveAttribute('href', 'test.html');

			var icon = await locator.locator('i');
			await expect(icon).toBeAttached();
			await expect(icon).toHaveClass('bi bi-pen');
		}
	},
	{
		name: 'Tooltip',
		conf: {
			type: 'button',
			title: 'Magic button',
			tooltip: 'Magic button Tooltip'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('BUTTON', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveText('Magic button');
			await expect(locator).toHaveAttribute('title', 'Magic button Tooltip');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('A', await callContainer('tagName'));
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
			await expect(locator).toHaveAttribute('href', 'test.html');
			await expect(locator).toHaveText('Magic button');
			await expect(locator).toHaveAttribute('title', 'Magic button Tooltip');
		}
	}
].forEach((test)=>{
	buttonTests.push({
		name: test.name + " Async",
		type: 'test',
		conf: test.conf,
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			test.verifyAsync(expect, assert, page, callInstance, callContainer, locator);
		}
	});
	var conf = JSON.parse(JSON.stringify(test.conf));
	conf.link = 'test.html';
	buttonTests.push({
		name: test.name + " Sync",
		type: 'test',
		conf: conf,
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			test.verifySync(expect, assert, page, callInstance, callContainer, locator);
		}
	});
});
buttonTests.push({
	name: 'Submit Async Success',
	type: 'test',
	conf: {
		type: 'button',
		title: 'Magic button',
		requestConfiguration: {
			url: '/inputs/button'
		},
		onSuccess: ['onButtonSuccess']
	},
	verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toBeEnabled();
		await expect(locator).toBeVisible();
		await expect(locator).toBeAttached();

		await locator.click();
		await expect(locator).toBeDisabled();

		// sleep
		await new Promise(resolve => setTimeout(resolve, 6000));

		await expect(locator).toBeEnabled();

		assert.areSame(1, await callInstance('success'));
		assert.areSame(0, await callInstance('failure'));
	}
});
buttonTests.push({
	name: 'Submit Async Failure',
	type: 'test',
	conf: {
		type: 'button',
		title: 'Magic button',
		requestConfiguration: {
			url: '/inputs/button-not-existing'
		},
		onFailure: ['onButtonFailure']
	},
	verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toBeEnabled();
		await expect(locator).toBeVisible();
		await expect(locator).toBeAttached();

		await locator.click();
		await expect(locator).toBeDisabled();

		// sleep
		await new Promise(resolve => setTimeout(resolve, 6000));

		await expect(locator).toBeEnabled();

		assert.areSame(0, await callInstance('success'));
		assert.areSame(1, await callInstance('failure'));
	}
});
actionInputTests.push({
	name: 'Input button',
	type: 'group',
	tests: buttonTests
});


actionInputTests.push({
	name: 'Input reset',
	type: 'group',
	tests: [
		{
			name: 'Basic',
			type: 'test',
			conf: {
				type: 'reset'
			}/*,
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				throw new Error('Not implemented');
			}*/
		},
		{
			name: 'Not editable',
			type: 'test',
			conf: {
				type: 'reset',
				editable: false
			}/*,
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				throw new Error('Not implemented');
			}*/
		},
		{
			name: 'Disabled',
			type: 'test',
			conf: {
				type: 'reset',
				disabled: true
			}/*,
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				throw new Error('Not implemented');
			}*/
		}
	]
});

require('../spec.js')({
	name: 'Inputs: Actions inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: actionInputTests
});