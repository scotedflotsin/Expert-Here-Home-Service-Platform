package www.experthere.in.model;

public class MaintenanceRes {

    boolean success;
    String message;

    MaintenanceData data;


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public MaintenanceData getData() {
        return data;
    }
}
