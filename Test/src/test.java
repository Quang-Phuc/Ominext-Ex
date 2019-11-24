import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class test {
    public static void main(String[] args) {
        List<Item> items = Arrays.asList(
                new Item("A", "Hanoi", 5),
                new Item("B", "NinhBinh", 5),
                new Item("C", "HaNoi", 6),
                new Item("A", "HaNoi", 6)
                );

        Map<String, Map<Integer, List<Item>>> checkingLogMap =
                items.stream().collect(Collectors.groupingBy(Item::getName, Collectors.groupingBy(Item::getAge)));
        System.out.println(checkingLogMap);
        /*for (Entry<String, Map<Integer, List<Item>>> userEntry : checkingLogMap.entrySet()) {

        }*/
    }



}
