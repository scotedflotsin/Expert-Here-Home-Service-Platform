package www.experthere.in.model;

import java.util.List;

public class TestResponse {

    boolean success;
    List<CAT_TEST> categories;


    public List<CAT_TEST> getCategories() {
        return categories;
    }

    public boolean isSuccess() {
        return success;
    }
}
