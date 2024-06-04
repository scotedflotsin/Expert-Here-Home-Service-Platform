package www.experthere.in.helper;

public class BookmarkCountResponse {



    boolean success;
    String message;

    int total_bookmarks;


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotal_bookmarks() {
        return total_bookmarks;
    }
}
