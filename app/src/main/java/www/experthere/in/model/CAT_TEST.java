package www.experthere.in.model;

import java.util.List;

import www.experthere.in.model.service_user_cat.Service;

public class CAT_TEST {

    String category_id,category_name,category_image;
    List<Service> services;

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public List<Service> getServices() {
        return services;
    }
}
