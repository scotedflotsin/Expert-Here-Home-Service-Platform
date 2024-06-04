package www.experthere.in.model;

import com.google.gson.annotations.SerializedName;

public class EditServiceRes {


        @SerializedName("success")
        private boolean success;

        @SerializedName("message")
        private String message;

        @SerializedName("service")
        private ServiceModel service;

        // Getters and setters

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ServiceModel getService() {
            return service;
        }

        public void setService(ServiceModel service) {
            this.service = service;
        }


}
