package f18a14c09s.generation.alexa.music.data;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static f18a14c09s.generation.alexa.music.data.MessageClassInfo.MessageType.RESPONSE;

@Getter
@Setter
public class MessageClassInfo extends ClassInfo {
    private MessageType messageType;
    private String namespace;

    public MessageClassInfo() {
    }

    public MessageClassInfo(String name,
                            String description,
                            List<PropertyInfo> propertyInfo,
                            List<JsonExample> jsonExamples,
                            MessageType messageType,
                            String namespace) {
        super(name, description, propertyInfo, jsonExamples);
        this.messageType = messageType;
        this.namespace = namespace;
    }

    @Override
    public String inferClassName() {
        return Optional.ofNullable(getMessageType())
                .map(msgType -> msgType == MessageType.REQUEST ?
                        String.format("%sRequest", getName()) :
                        msgType == RESPONSE ? String.format("%sResponse", getName().replaceAll("\\.Response$", "")) : null)
                .orElse(null);
    }

    public enum MessageType {
        REQUEST,
        RESPONSE
    }
}
