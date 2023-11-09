package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.Test;

import ji.common.structures.BooleanBuilder;
import ji.translator.Translator;
import toti.answers.request.Request;
import toti.ui.validation.ValidationItem;

public class SimpleRuleTest {
	
	@Test
	public void testCheckWithError() {
		Function<Translator, String> onError = (t)->"Expected error";
		ValidationItem item = mock(ValidationItem.class);
		when(item.getOriginValue()).thenReturn("originValue");
		
		BooleanBuilder isCalled = new BooleanBuilder(false);
		SimpleRule<String> rule = new SimpleRule<String>("ruleBond", onError) {
			
			@Override
			protected boolean isErrorToShow(String value, Object o) {
				assertEquals("ruleBond", value);
				assertEquals("originValue", o);
				isCalled.set(true);
				return true;
			}
		};
		rule.check(mock(Request.class), "property", "rule", item);
		
		verify(item, times(1)).addError("property", onError);
		verify(item, times(1)).getOriginValue();
		verifyNoMoreInteractions(item);
		assertEquals(true, isCalled.get());
	}
	
	@Test
	public void testCheckWithoutError() {
		Function<Translator, String> onError = (t)->"Expected error";
		ValidationItem item = mock(ValidationItem.class);
		when(item.getOriginValue()).thenReturn("originValue");
		
		BooleanBuilder isCalled = new BooleanBuilder(false);
		SimpleRule<String> rule = new SimpleRule<String>("ruleBond", onError) {
			
			@Override
			protected boolean isErrorToShow(String value, Object o) {
				assertEquals("ruleBond", value);
				assertEquals("originValue", o);
				isCalled.set(true);
				return false;
			}
		};
		rule.check(mock(Request.class), "property", "rule", item);
		
		verify(item, times(1)).getOriginValue();
		verifyNoMoreInteractions(item);
		assertEquals(true, isCalled.get());
	}

}
