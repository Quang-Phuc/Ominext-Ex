
public class InterfaceMain {
	public static void main(String[] args) {
		InterfaceFuntion interfaceFuntion = new InterfaceImpl();
		interfaceFuntion.tinh(1);
		int a = interfaceFuntion.Sum(1, 2);
		System.out.println(a);
	}

}
