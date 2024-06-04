package www.experthere.in.model;


import java.util.List;

public class SettingsRes {

    boolean success;
    String message;

    List<SettingsData> settings;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<SettingsData> getSettings() {
        return settings;
    }
}
