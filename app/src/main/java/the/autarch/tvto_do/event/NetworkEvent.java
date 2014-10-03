package the.autarch.tvto_do.event;

/**
 * Created by jpierce on 9/26/14.
 */
public class NetworkEvent {

    public enum NetworkEventType {
        SUCCESS,
        FAILURE
    }

    private NetworkEventType type;
    private String message;

    public NetworkEvent(NetworkEventType type, String message) {
        this.type = type;
        this.message = message;
    }

    public boolean isSuccess() {
        return type == NetworkEventType.SUCCESS;
    }

    public String getMessage() {
        return message;
    }
}
