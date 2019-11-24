import java.util.Arrays;
import java.util.List;

public class Time {
    private String startDate;
    private String endDate;
    private String userDay;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getUserDay() {
        return userDay;
    }

    public void setUserDay(String userDay) {
        this.userDay = userDay;
    }

    public Time() {
    }

    public Time(String userDay) {
        this.userDay = userDay;
    }

    public Time(String startDate, String endDate, String userDay) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.userDay = userDay;
    }

    @Override
    public String toString() {
        return "Time{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", userDay='" + userDay + '\'' +
                '}';
    }
    public List<Time> insertTime()
    {
        List<Time> times = Arrays.asList(
                new Time("2019", "2019","A"),
                new Time("2018", "2019","B")
                );
        return  times;
    }
}
