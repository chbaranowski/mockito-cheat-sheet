package demo;

public class EchoSysOut implements Echo
{

	@Override
	public void echo() {
		System.out.println("Echo");
	}

	@Override
	public EchoResponse echo(String msg) {
		System.out.println("Echo " + msg);
		return new EchoResponse("0000");
	}

	@Override
	public EchoResponse echo(EchoRequest request, String code) {
		System.out.println("Echo " + request.id);
		return new EchoResponse(code);
	}
	
}
