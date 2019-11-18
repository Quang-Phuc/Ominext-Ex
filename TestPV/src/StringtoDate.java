import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringtoDate {
	public static void main(String[] args) {
		StringtoDate stringtoDate = new StringtoDate();
		System.out.println(stringtoDate.StringDates());
		stringtoDate.learnMap();
	}

	public List<String> StringDates() {
		List<String> dateList = new ArrayList<>();
		String startDate = "20191012";
		String endDate = "20191112";
		LocalDate localstartDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		LocalDate localendDate = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE);
		// Su dung LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)
		long numOfDaysBetween = ChronoUnit.DAYS.between(localstartDate, localendDate);
		// Su dung ChronoUnit.DAYS.between(localstartDate, localendDate);
		IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> localstartDate.plusDays(i))
				.collect(Collectors.toList()).forEach(e -> {
					dateList.add(e.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
				});

		
		return dateList;
	}
	public void learnMap()
	{
		Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(1, "A");
		map.put(1, "B");

		
 	}
}
