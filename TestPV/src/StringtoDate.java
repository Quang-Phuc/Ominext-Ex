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
	/*public ATWorkingTimeCSVReponse getWorkingTimeCSV(ATWorkingTimeCSVRequest request) {
        ATWorkingTimeCSVReponse.Builder response = ATWorkingTimeCSVReponse.newBuilder();
        String checkStart ;
        String checkEnd = "checkEnd";
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        List<String> stringDate = getListDate(startDate, endDate);
        List<CheckingLog> checkingLogs = checkingLogRepository.findByOfficeUserIdInAndTargetDateIn(request.getOficeUserIdList(), stringDate);
        List<CheckingLog> staffLogs;
        List<String> officeUserIds = request.getOficeUserIdList();
        List<String> stringListDate = checkingLogs.stream().map(CheckingLog::getTargetDate).collect(Collectors.toList());
        for (String dateNow : stringListDate) {
            for (String officeUserId : officeUserIds) {
                staffLogs = checkingLogs.stream()
                        .filter(log -> log.getOfficeUserId().equals(officeUserId) && log.getTargetDate().equals(dateNow))
                        .collect(Collectors.toList());
                checkStart = DatesUtils.convertDateToString(staffLogs.get(0).getCheckedAt());
                if (staffLogs.size() % 2 == 0) {
                    checkEnd = DatesUtils.convertDateToString(staffLogs.get(staffLogs.size() - 1).getCheckedAt());
                }
                String finalCheckStart = checkStart;
                String finalCheckEnd = checkEnd;
                List<ATWorkingTimeCSVReponse.WorkingTimeCSV> workingTimeCSVList = checkingLogs
                        .stream().map(CheckingLog -> CheckingLog.asWorkingTimeCSV(finalCheckStart, finalCheckEnd)).collect(Collectors.toList());
                response.addAllWorkingtimeCSV(workingTimeCSVList);
                response.addAllWorkingtimeCSV(workingTimeCSVList);
            }
        }
        return response.build();*/
	public void learnMap()
	{
		Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(1, "A");
		map.put(1, "B");

		
 	}
}
