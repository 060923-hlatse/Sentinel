package co.wethinkcode.sentinel.model;

public class SecurityEvent {

    private String username;
    private String eventType;
    private String ipAddress;
    private String severity;

    public SecurityEvent(String username, String eventType,
                          String ipAddress, String severity) {
        this.username = username;
        this.eventType = eventType;
        this.ipAddress = ipAddress;
        this.severity = severity;
    }

    public String getUsername() {
        return username;
    }

    public String getEventType() {
        return eventType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSeverity() {
        return severity;
    }
}
