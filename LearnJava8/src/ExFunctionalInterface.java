
@FunctionalInterface
public interface ExFunctionalInterface {

	public void sayHello(String helloMessage);
	default public void defaultMethod()
	{
		System.out.println("s");
	}
}
