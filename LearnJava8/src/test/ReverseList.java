package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.omi.datetime.common;

public class ReverseList extends ArrayList<String> {
	

	public int outside = 0;

	private void forCounter(List<Integer> integers) {
	    for(int ii = 0; ii < integers.size(); ii++) {
	        Integer next = integers.get(ii);
	        outside = next*next;
	    }
	}

	private void forEach(List<Integer> integers) {
	    for(Integer next : integers) {
	        outside = next * next;
	    }
	}

	private void iteratorForEach(List<Integer> integers) {
	    integers.forEach((ii) -> {
	        outside = ii*ii;
	    });
	}
	private void iteratorStream(List<Integer> integers) {
	    integers.stream().forEach((ii) -> {
	        outside = ii*ii;
	    });
	}
	
	public static void main(String[] args) {
		List<Integer> listInt = Arrays.asList(1,2,3);
		ReverseList reverseList = new ReverseList();
		reverseList.iteratorForEach(listInt);
		common.print(listInt);
		reverseList.iteratorStream(listInt);
		common.print(listInt);
	}
}
