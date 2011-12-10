package demo;

public interface Echo {

	void echo();
	
	EchoResponse echo(String msg);
	
	EchoResponse echo(EchoRequest request, String code);
	
}
