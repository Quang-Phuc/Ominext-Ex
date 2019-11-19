
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Java8Examples4 {

	public static void main(String[] args) {

		// 3 apple, 2 banana, others 1

		// Add phan tu
		List<Item> items = Arrays.asList(new Item("apple", 10, 5), new Item("apple", 10, 5), new Item("banana", 20, 6),
				new Item("orang", 10, 7),new Item("orang", 10, 12),new Item("orang", 10, 10));
		List<Integer> numbers = Arrays.asList(1, 2, 3, 3, 5, 1, 7, 2);
		// Duyet phan tu lambda
		items.forEach(i -> System.out.println(i));
		// Groupby Theo ten
		Map<String, Long> counting = items.stream()
				.collect(Collectors.groupingBy(Item::getName, Collectors.counting()));

		System.out.println(counting);
		// Filter loc phan tu
		List<Item> listFilter = items.stream().filter(o -> o.getName().equals("apple")).collect(Collectors.toList());
		System.out.println(listFilter);
		// Phan tu dau tienn
		Optional<Item> listSort = items.stream().findAny();
		System.out.println(listSort);
		// Phan tu dau tien
		Optional<Item> listFirt = items.stream().findFirst();
		System.out.println(listFirt);
		for (Item item : listFilter) {
			String a1 = item.getName();
			System.out.println(a1);

		}
		// Loai di cac phan tu trung nhau
		List<Item> listDistinct = items.stream().distinct().collect(Collectors.toList());
		System.out.println(listDistinct);
		// Get 3 phan tu dau tien
		List<Item> listLmit = items.stream().limit(3).collect(Collectors.toList());
		System.out.println(listLmit);
		// Tim phan tu cuoi cung

		/*
		 * long count = data.careas.count(); CArea last =
		 * data.careas.stream().skip(count - 1).findFirst().get();
		 * 
		 */
		// Bo 2 phan tu dau tien
		List<Item> listSkip = items.stream().skip(2).collect(Collectors.toList());
		System.out.println(listSkip);
		// get phan tu cuoi cung
		List<Item> listEnd = items.stream().skip(items.size() - 1).collect(Collectors.toList());
		System.out.println(listEnd);
		// Kiem tra phan tu co phan tu nao thoa man dieu kien khong
		boolean listAnyMatch = numbers.stream().anyMatch(o -> o > 3);
		System.out.println(listAnyMatch);
		// Kiem tra phan tu xem co all phan tu thoa man dieu kien k
		boolean listAllMatch = items.stream().allMatch(o -> o.getName().equals("a"));

		System.out.println(listAllMatch);
		// true khi khong co bat ky phan tu nao thoa man dieu kie
		boolean listOneMatch = items.stream().noneMatch(o -> o.getName().equals("a"));
		System.out.println();
		// Tinh toan
		int sum = numbers.stream().reduce(0, (a, b) -> a + b);
		System.out.println(sum);
		int max = numbers.stream().reduce(0, Integer::max);
		System.out.println(max);
		int min = numbers.stream().reduce(0, Integer::min);
		System.out.println(min);

		// creating a stream
		IntStream stream = IntStream.of(2, 3, 3, 4, 6, 8, 8);

		// OptionalDouble is a container object
		// which may or may not contain a
		// doouble value.
		OptionalDouble obj = stream.average();

		// If a value is present, isPresent() will
		// return true and getAsDouble() will
		// return the value
		if (obj.isPresent()) {
			System.out.println(obj.getAsDouble());
		} else {
			System.out.println("-1");
		}
		//
		List<Integer> b = items.stream().map(Item::getPrice).distinct().collect(Collectors.toList());
		//
		System.out.println(b);

		// Tinh gia tri trung binh use Java 8
			//+ Tinh sum
		System.out.println("===============Tinh gia tri trung binh ===========");
		List<Integer> getPrice = items.stream().map(Item::getPrice).collect(Collectors.toList());
		System.out.println("View :"+ getPrice);
		Integer getPriceSum = items.stream().map(Item::getPrice).reduce(0, Integer::sum);
			System.out.println("getPriceSum: "+getPriceSum);
			// + Tinh sl
		Long count = items.stream().map(Item::getPrice).count();
			System.out.println("getPriceCount: "+count);
			// Sum
			float tb = getPriceSum/count;
		System.out.println(tb);
	}
}
