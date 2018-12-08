package f18a14c09s.integration.alexa.music.testing;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ExampleMessage {

    private Double myDouble;

    private ArrayList<?> myArray;

    private String message;

    private Integer myInt;

    private Boolean myBoolean;

    private Object nullValue;

    private ExampleSubObject myObject;

}
