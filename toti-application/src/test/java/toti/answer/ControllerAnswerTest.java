package toti.answer;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class ControllerAnswerTest {

	@Test
	@Ignore
	public void testAnswer() {
		// TODO
		// projit correct state a overit, ze se vola, co se volat ma
		// stavy, kdy neco hazi vyjimky - chytat pouze server exception - mozna kontrolovat status code
	}

	@Test
	@Ignore
	public void testGetMappedAction() {
		// TODO
		/*
		router action
		ruzne pripady mapovani a vysledky
		*/
	}

	@Test
	@Ignore
	public void testRun() {
		// TODO
		/*
		prevalidate throws
		authorize throws + secured throws
			- mode je Header
			- neni nastaveno presmerovani
			- je nastaveno presmerovani bez backlinku
			- je nastaveno presmerovani s backlinkem
		validate throws
		create throws
		working state - check translator, body,....
		*/
	}

	@Test
	@Ignore
	public void testCheckSecured() {
		// TODO
		/*
		secured annotation - not secured - nic
		secured annotation - is secured
			identity is anonymous
			security mode vs identity security mode - asi vsechny stavy
		*/
	}
	
	@Test
	@Ignore
	public void testParseBody() {
		// TODO
		/*
		requst obsahuje map body - nic
		request obsahuje urlencoded data - povoleno - nic
		request obsahuje form data - povoleno - nic
		request obsahuje urlencoded data - nepovoleno - ??
		request obsahuje form data - nepovoleno - ??
		request je byte s xml - povoleno - pridat data
		request je byte s xml - nepovoleno - nic
		request je byte s json - povoeno - pridat
		request je byte s json - nepovoleno - nic
		request je byste s json list - povoleno - ?????
		*/
	}
	
}
