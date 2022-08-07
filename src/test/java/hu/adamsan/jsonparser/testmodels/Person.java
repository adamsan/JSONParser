package hu.adamsan.jsonparser.testmodels;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Person {
    private Long age;
    private String firstName;
    private String id;
    private Items items;
    private List<Integer> lastName;
    private List<String> friends;
}
