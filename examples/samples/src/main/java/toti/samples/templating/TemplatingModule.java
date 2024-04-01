package toti.samples.templating;

import java.util.Arrays;
import java.util.List;

import ji.common.functions.Env;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

public class TemplatingModule implements Module {

	@Override
	public String getName() {
		return "templating";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		// TODO all tags
		// TODO variables
		// TODO code
		/*
		name: 'Template',
				description: '',
				links: [
					{
						name: 'Basic',
						description: ' Basics of templating',
						link: '/examples-templates/template/basics'
					},
					{
						name: 'Variables',
						description: 'How work with variables',
						link: '/examples-templates/template/variable'
					},
					{
						name: 'OWASP testing page',
						description: '',
						link: '/examples-templates/template/owasp-form'
					}
				]
		*/
		return Arrays.asList();
	}

}
