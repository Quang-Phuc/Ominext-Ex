import java.util.Arrays;
import java.util.List;

public class Users {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Users() {
    }

    public Users(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public  List<Users> insertUser()
    {
        List<Users> users = Arrays.asList(
                new Users("A", "Hanoi"),
                new Users("B", "Hanoi"),
                new Users("C", "Hanoi"));
        return  users;

    }
}
