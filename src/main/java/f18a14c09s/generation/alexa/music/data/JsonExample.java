package f18a14c09s.generation.alexa.music.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonExample {
    private String jsonValue;
    private String description;

    public JsonExample() {
    }

    public JsonExample(String jsonValue, String description) {
        this.jsonValue = jsonValue;
        this.description = description;
    }
}
