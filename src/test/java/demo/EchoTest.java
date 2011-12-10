package demo;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import demo.Echo;
import demo.EchoRequest;
import demo.EchoResponse;
import demo.EchoSysOut;

/**
 * Cheat Sheet Test for Mockito
 *  
 * More details about mockito see http://code.google.com/p/mockito/
 * or goto the perfect mockito documentation page  
 * http://docs.mockito.googlecode.com/hg/latest/org/mockito/Mockito.html
 */
public class EchoTest {
	
	@Rule
	public MockitoRule mockitoRule = new MockitoRule(this);
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock Echo mockEcho;

	@Test
	public void stubbingReturnValue() {
		when(mockEcho.echo("Hello Mockito World")).thenReturn(echoResponse("0042"));
		assertEquals(echoResponse("0042"), mockEcho.echo("Hello Mockito World"));
	}
	
	@Test
	public void stubbingWithArgumentMatchers() {
		when(mockEcho.echo(anyString())).thenReturn(echoResponse("0001"));
		when(mockEcho.echo(eq("Hello"))).thenReturn(echoResponse("0002"));
		when(mockEcho.echo(argThat(isValidAnswer()))).thenReturn(echoResponse("0003"));	
		assertEquals(echoResponse("0001"), mockEcho.echo("Any String"));
		assertEquals(echoResponse("0002"), mockEcho.echo("Hello"));
		assertEquals(echoResponse("0003"), mockEcho.echo("42"));
		
		when(mockEcho.echo(any(EchoRequest.class), eq("0042"))).thenReturn(echoResponse("0042"));
		when(mockEcho.echo(any(EchoRequest.class), eq("0000"))).thenReturn(echoResponse("0000"));
		assertEquals(echoResponse("0042"), mockEcho.echo(new EchoRequest(), "0042"));
		assertEquals(echoResponse("0000"), mockEcho.echo(new EchoRequest(), "0000"));
	}
	
	@Test
	public void stubbingThrowExceptiton() {
		when(mockEcho.echo(null)).thenThrow(new IllegalArgumentException());
		thrown.expect(IllegalArgumentException.class);
		mockEcho.echo(null);
	}
	
	@Test
	public void stubbingVoidMethods() {
		doThrow(IllegalStateException.class).when(mockEcho).echo();
		thrown.expect(IllegalStateException.class);
		mockEcho.echo();
	}
	
	@Test
	public void stubbingIteratorStyle() {
		when(mockEcho.echo("42"))
			.thenReturn(echoResponse("0042"))
			.thenThrow(IllegalStateException.class);
		assertEquals(echoResponse("0042"), mockEcho.echo("42"));
		thrown.expect(IllegalStateException.class);
		mockEcho.echo("42");
	}
	
	@Test
	public void stubbingIteratorStyleReturnDifferentValues() {
		when(mockEcho.echo("42"))
			.thenReturn(echoResponse("0042"), echoResponse("0043"));
		assertEquals(echoResponse("0042"), mockEcho.echo("42"));
		assertEquals(echoResponse("0043"), mockEcho.echo("42"));
	}
	
	@Test
	public void stubbingChangingDefaultReturnValues(){
		mockEcho = mock(Echo.class, new DefaultReturnValues());
		assertThat("42", is(mockEcho.echo("Hello").getReturnCode()));
	}
	
	@Test
	public void stubbingWithAnswer() {
		when(mockEcho.echo(any(EchoRequest.class), anyString())).thenAnswer(echoAnswer());
		assertEquals("02", mockEcho.echo(new EchoRequest(), "02").getReturnCode());
	}
	
	@Test
	public void verifySomeBehaviour() {
		mockEcho.echo("Hello Mockito World");
		mockEcho.echo();
		verify(mockEcho).echo("Hello Mockito World");
		verify(mockEcho).echo();
	}
	
	@Test
	public void verifyingExactNumberOfInvocations() {
		mockEcho.echo();
		mockEcho.echo();
		mockEcho.echo("1");
		mockEcho.echo("2");
		mockEcho.echo("3");
		
		verify(mockEcho).echo("1");
		verify(mockEcho, times(2)).echo();
		verify(mockEcho, atLeastOnce()).echo(eq("1"));
		verify(mockEcho, atLeast(2)).echo(anyString());
		verify(mockEcho, atMost(3)).echo(anyString());
		verify(mockEcho, never()).echo(any(EchoRequest.class), anyString());
	}
	
	@Test
	public void verificationInOrder() {
		mockEcho.echo("1");
		mockEcho.echo("2");
		mockEcho.echo("3");
		
		// verify without order
		verify(mockEcho).echo("3");
		verify(mockEcho).echo("2");
		verify(mockEcho).echo("1");

		// verify order
		InOrder inOrder = inOrder(mockEcho);
		inOrder.verify(mockEcho).echo("1");
		inOrder.verify(mockEcho).echo("2");
		inOrder.verify(mockEcho).echo("3");
		
		// more then one mock
		Echo echoA = mock(Echo.class);
		Echo echoB = mock(Echo.class);
		echoB.echo();
		echoA.echo();
		
		// verify without order
		verify(echoA).echo();
		verify(echoB).echo();
		
		// verify the order
		inOrder = inOrder(echoA, echoB);
		inOrder.verify(echoB).echo();
		inOrder.verify(echoA).echo();
	}
	
	@Test
	public void resettingMocks() {
		when(mockEcho.echo("42")).thenThrow(IllegalArgumentException.class);
		reset(mockEcho);
		mockEcho.echo("42");
	}

	@Test
	public void makingSureInteractionNeverHappened() {
		verifyZeroInteractions(mockEcho);
	}
	
	@Test
	public void findingRedundantInvocations() {
		mockEcho.echo("Hello Mockito");
		mockEcho.echo("Hello EasyMock");
		verify(mockEcho).echo("Hello Mockito");
		thrown.expect(NoInteractionsWanted.class);
		verifyNoMoreInteractions(mockEcho);
	}
	
	@Test
	public void spying() {
		Echo echo = new EchoSysOut();
		Echo spyEcho = spy(echo);
		spyEcho.echo("Hello Mockito Spy");
		
		// verify the spy
		verify(spyEcho).echo("Hello Mockito Spy");
		
		// stub a spy method
		when(spyEcho.echo("42")).thenThrow(IllegalArgumentException.class);
		thrown.expect(IllegalArgumentException.class);
		spyEcho.echo("42");
	}
	
	@Test
	public void capturingArguments() {
		ArgumentCaptor<EchoRequest> captorRequest = ArgumentCaptor.forClass(EchoRequest.class);
		EchoRequest echoRequest = new EchoRequest();
		echoRequest.id = "23";
		mockEcho.echo(echoRequest, "0000");
		verify(mockEcho).echo(captorRequest.capture(), eq("0000"));
		assertEquals("23", captorRequest.getValue().id);
	}
	
	@Test
	public void bddStyleMockitoTest() {
		// given
		given(mockEcho.echo("Hello Mockito")).willReturn(echoResponse("BDD"));
		// when
		EchoResponse echoResponse = mockEcho.echo("Hello Mockito");
		// then
		assertThat(echoResponse, is(echoResponse("BDD")));
	}
	
	Answer<EchoResponse> echoAnswer() {
		return new Answer<EchoResponse>() {
			public EchoResponse answer(InvocationOnMock invocation) throws Throwable {
				String returnCode =  (String) invocation.getArguments()[1];
				return new EchoResponse(returnCode);
			}
		};
	}
	
	EchoResponse echoResponse(String code) {
		return new EchoResponse(code);
	}
	
	Matcher<String> isValidAnswer() {
		return new BaseMatcher<String>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("The msg is valid when not null and equals 42");
			}

			@Override
			public boolean matches(Object item) {
				return item != null && item.equals("42");
			}
		};
	}
}
