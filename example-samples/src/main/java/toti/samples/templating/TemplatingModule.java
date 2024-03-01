package toti.samples.templating;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
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
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger) throws Exception {
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
