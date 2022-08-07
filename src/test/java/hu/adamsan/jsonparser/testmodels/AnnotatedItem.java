package hu.adamsan.jsonparser.testmodels;

import hu.adamsan.jsonparser.JsonPropertyName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotatedItem {
    private String name;
    private Double price;
    @JsonPropertyName(name = "tipus")
    private String type;
}
