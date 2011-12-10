package demo;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DefaultReturnValues implements Answer<EchoResponse> {

	@Override
	public EchoResponse answer(InvocationOnMock invocation) throws Throwable {
		return new EchoResponse("42");
	}

}
