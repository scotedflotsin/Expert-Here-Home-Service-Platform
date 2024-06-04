package www.experthere.in.model;

import java.util.List;

public class CallHistoryResponse {

    boolean success;
    String message;

    List<CallHistoryModel> call_history;


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<CallHistoryModel> getCall_history() {
        return call_history;
    }
}
