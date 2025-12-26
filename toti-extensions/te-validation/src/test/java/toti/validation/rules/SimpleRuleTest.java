package toti.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ji.common.structures.BooleanBuilder;
import toti.extensions.Translator;
import toti.validation.ValidationItem;
import toti.answers.request.Request;

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
