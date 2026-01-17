package toti.samples.ui;

import java.util.Arrays;
import java.util.List;

import toti.application.answers.router.Link;
import toti.application.application.Module;
import toti.application.application.Task;
import toti.application.application.register.Register;
import toti.lib.files.env.Env;
import toti.samples.ui.controllers.FormController;
import toti.samples.ui.controllers.GridController;
import toti.samples.ui.controllers.InputsController;
import toti.samples.ui.controllers.SelectController;

public class UiModule implements Module {

	@Override
	public String getName() {
		return "ui";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		/*
		grid
			- bez niceho
			- jen sortovani
			- jen fitrovani
			- tlacitka
			- strankovani
			- page size
			- group action
			- custom template
			- renderery
			- vsechno dohromady
			- tree
		form
			- inputy
			- submit a button
			- bind
			- placeholder
			- sync a async
			- exclude, disable, editable
			- editable
			- list
			- dynamic
		inputs
			- datetime: strict, non strict
			- optional
			- text: load
		selecty
			.....
		Odkazy - sync a async
			
		*/
		
		register.addController(FormController.class, ()->new FormController());
		register.addController(GridController.class, ()->new GridController());
		register.addController(InputsController.class, ()->new InputsController());
		register.addController(SelectController.class, ()->new SelectController());
		return Arrays.asList();
	}

}
