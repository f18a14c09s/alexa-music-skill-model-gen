package f18a14c09s.generation.alexa.music.gen.data;

import f18a14c09s.integration.alexa.data.AbstractMessage;
import f18a14c09s.integration.alexa.music.messagetypes.Request;
import f18a14c09s.integration.alexa.music.messagetypes.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class MessageClassInfo extends AbstractClassInfo {
    private Class<? extends AbstractMessage> messageType;
    private String namespace;

    public MessageClassInfo() {
    }

    public MessageClassInfo(String name,
                            String description,
                            List<PropertyInfo> propertyInfo,
                            List<JsonExample> jsonExamples,
                            Class<? extends AbstractMessage> messageType,
                            String namespace) {
        super(name, description, propertyInfo, jsonExamples);
        this.messageType = messageType;
        this.namespace = namespace;
    }

    @Override
    public String inferClassName() {
        return Optional.ofNullable(getMessageType())
                .map(msgType -> msgType == Request.class ?
                        String.format("%sRequest", getName()) :
                        msgType == Response.class ?
                                String.format("%sResponse", getName().replaceAll("\\.Response$", "")) :
                                null)
                .orElse(null);
    }
}
