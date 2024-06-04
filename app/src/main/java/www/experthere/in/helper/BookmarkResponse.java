package www.experthere.in.helper;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookmarkResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("bookmarks")
    private List<Bookmark> bookmarks;



    // Constructor, getters, and setters


    public boolean isSuccess() {
        return success;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }
}

