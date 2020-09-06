package mvc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FlashMessagesTest {

	@Test
	public void testRemovingFlash() {
		FlashMessages flash = new FlashMessages();
		flash.addMessage("text 1", "sev");
		flash.addMessage("text 2", "sev");
		flash.addMessage("text 3", "sev");
		flash.addMessage("text 4", "sev");
		
		int i = 1;
		for (FlashMessages.Message message : flash) {
			assertEquals("text " + i, message.getMessage());
			assertEquals("sev", message.getSeverity());
			i++;
		}
		assertEquals(0, flash.getMessages().size());
	}
	
}
