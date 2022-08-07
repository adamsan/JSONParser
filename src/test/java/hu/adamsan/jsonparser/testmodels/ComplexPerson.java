package hu.adamsan.jsonparser.testmodels;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ComplexPerson {
    private int id;
    private String name;
    private List<AnnotatedItem> items;
}
