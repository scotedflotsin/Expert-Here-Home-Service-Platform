package www.experthere.in.helper;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.experthere.in.model.ProviderList;

public class ProviderListResponse {


        @SerializedName("success")
        private boolean success;

        @SerializedName("providers")
        private List<ProviderList> providers;

        public boolean isSuccess() {
            return success;
        }

        public List<ProviderList> getProviders() {
            return providers;
        }



}
