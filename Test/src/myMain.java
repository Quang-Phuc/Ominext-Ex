import java.util.List;
import java.util.stream.Collectors;

public class myMain {
    Time time = new Time();
    Users users = new Users();
    List<Time> times = time.insertTime();
    List<Users> usersList = users.insertUser();

    public static void main(String[] args) {


    }

    public void showTime(List<String> dateList, List<String> user) {
        for (String use : user) {
            for (String date : dateList) {
                List<String> timeList = times.stream().filter(x -> date.equals(x.getStartDate())).map(Time::getStartDate).collect(Collectors.toList());
             for(String time: timeList)
             {
                 if(!use.equals(time))
                 {
                     timeList.add(use);
                 }
             }
            }
        }
    }
}
