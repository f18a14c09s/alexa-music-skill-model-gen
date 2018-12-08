package f18a14c09s.integration.alexa.music.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ExampleMessageTest {
    public static final String TEST_CASE =
            "{\"message\": \"Hello, World!\",\"myInt\": 123,\"myDouble\": 123.456,\"myBoolean\": false,\"myArray\": [1, 2, 3],\"myObject\": {\"otherMessage\": \"My Other Message\"},\"nullValue\": null}";

    @Test
    void testDeserialization() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        ExampleMessage subject = jsonMapper.readValue(TEST_CASE, ExampleMessage.class);
        Assertions.assertEquals(((Number) subject.getMyDouble()).doubleValue(), 123.456D);
        Assertions.assertEquals(((Number) subject.getMyArray().get(0)).longValue(), 1L);
        Assertions.assertEquals(((Number) subject.getMyArray().get(1)).longValue(), 2L);
        Assertions.assertEquals(((Number) subject.getMyArray().get(2)).longValue(), 3L);
        Assertions.assertFalse((boolean) subject.getMyBoolean());
        Assertions.assertEquals(((Number) subject.getMyInt()).longValue(), 123L);
        Assertions.assertEquals(subject.getMessage(), "Hello, World!");
        Assertions.assertEquals(subject.getMyObject().getOtherMessage(), "My Other Message");
        Assertions.assertNull(subject.getNullValue());
    }
}
