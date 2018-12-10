package f18a14c09s.generation.alexa.music.gen.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageInfo {
    private MessageClassInfo requestInfo;
    private MessageClassInfo responseInfo;

    public MessageInfo() {
    }

    public MessageInfo(MessageClassInfo requestInfo, MessageClassInfo responseInfo) {
        this.requestInfo = requestInfo;
        this.responseInfo = responseInfo;
    }
}
