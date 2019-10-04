import java.util.Arrays;
import java.util.List;

public class DifferentForEach {
	public static void main(String[] args) {
		DifferentForEach differentForEach = new DifferentForEach();
		System.out.println("Stream :");
		differentForEach.DifferentStream();

		System.out.println("ParallelStream :");
		differentForEach.DifferentParallelStream();

	}

	List<String> list = Arrays.asList("A", "B", "C", "D");

	public void DifferentStream() {

		list.stream().forEach(System.out::println);
	}

	public void DifferentParallelStream() {

		list.parallelStream().forEach(System.out::println);
	}

}
