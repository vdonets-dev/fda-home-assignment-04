package com.fda.home.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenFdaSearchResponse {
    private Meta meta;
    private List<Result> results;

    @Data
    public static class Meta {
        private Results results;

        @Data
        public static class Results {
            @JsonProperty("skip")
            private int skip;

            @JsonProperty("limit")
            private int limit;

            @JsonProperty("total")
            private int total;
        }
    }

    @Data
    public static class Result {
        @JsonProperty("application_number")
        private String applicationNumber;

        @JsonProperty("sponsor_name")
        private String sponsorName;

        private OpenFda openfda;

        private List<Product> products;

        private List<Submission> submissions;

        @Data
        public static class OpenFda {
            @JsonProperty("manufacturer_name")
            private List<String> manufacturerName;

            @JsonProperty("brand_name")
            private List<String> brandName;

            @JsonProperty("substance_name")
            private List<String> substanceName;

            @JsonProperty("application_number")
            private List<String> applicationNumber;

            @JsonProperty("product_ndc")
            private List<String> productNdc;

            @JsonProperty("product_type")
            private List<String> productType;

            @JsonProperty("route")
            private List<String> route;

            @JsonProperty("rxcui")
            private List<String> rxcui;

            @JsonProperty("spl_id")
            private List<String> splId;

            @JsonProperty("spl_set_id")
            private List<String> splSetId;

            @JsonProperty("package_ndc")
            private List<String> packageNdc;

            @JsonProperty("unii")
            private List<String> unii;
        }

        @Data
        public static class Product {
            @JsonProperty("product_number")
            private String productNumber;

            @JsonProperty("brand_name")
            private String brandName;

            @JsonProperty("dosage_form")
            private String dosageForm;

            @JsonProperty("marketing_status")
            private String marketingStatus;

            @JsonProperty("reference_drug")
            private String referenceDrug;

            @JsonProperty("reference_standard")
            private String referenceStandard;

            @JsonProperty("te_code")
            private String teCode;

            private List<ActiveIngredient> activeIngredients;

            @Data
            public static class ActiveIngredient {
                @JsonProperty("name")
                private String name;

                @JsonProperty("strength")
                private String strength;
            }
        }

        @Data
        public static class Submission {
            @JsonProperty("submission_type")
            private String submissionType;

            @JsonProperty("submission_number")
            private String submissionNumber;

            @JsonProperty("submission_status")
            private String submissionStatus;

            @JsonProperty("submission_status_date")
            private String submissionStatusDate;

            @JsonProperty("submission_class_code")
            private String submissionClassCode;

            @JsonProperty("submission_class_code_description")
            private String submissionClassCodeDescription;
        }
    }
}
