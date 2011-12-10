package demo;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.MockitoAnnotations;

public class MockitoRule extends TestWatcher {

	private final Object testObject;

	public MockitoRule(Object testObject) {
		this.testObject = testObject;
	}
	
	@Override
	protected void starting(Description description) {
		MockitoAnnotations.initMocks(testObject);
	}
	
}
