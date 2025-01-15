package com.fda.home.converter;

import com.fda.home.model.dto.OpenFdaSearchResponse;
import com.openfda.generated.models.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SearchResponseConverter extends BiConverter<OpenFdaSearchResponse, SearchResponse> {

    @Override
    protected SearchResponse doConvert(OpenFdaSearchResponse input) {
        return new SearchResponse()
                .meta(Optional.ofNullable(input)
                        .map(OpenFdaSearchResponse::getMeta)
                        .map(this::mapMeta)
                        .orElse(null))
                .drugApplications(Optional.ofNullable(input)
                        .map(OpenFdaSearchResponse::getResults)
                        .stream()
                        .flatMap(List::stream)
                        .map(this::mapToDrugApplication)
                        .toList());
    }

    private SearchResponseMeta mapMeta(OpenFdaSearchResponse.Meta meta) {
        int totalPages = (int) Math.ceil((double) meta.getResults().getTotal() / meta.getResults().getLimit());
        return new SearchResponseMeta()
                .skip(meta.getResults().getSkip())
                .limit(meta.getResults().getLimit())
                .total(meta.getResults().getTotal())
                .totalPages(totalPages);
    }

    private DrugApplicationDetails mapToDrugApplication(OpenFdaSearchResponse.Result result) {
        OpenFdaSearchResponse.Result.OpenFda openFda = result.getOpenfda();
        return new DrugApplicationDetails()
                .applicationNumber(result.getApplicationNumber())
                .sponsorName(result.getSponsorName())
                .manufacturerNames(Optional.ofNullable(openFda)
                        .map(OpenFdaSearchResponse.Result.OpenFda::getManufacturerName)
                        .orElse(List.of()))
                .brandNames(Optional.ofNullable(openFda)
                        .map(OpenFdaSearchResponse.Result.OpenFda::getBrandName)
                        .orElse(List.of()))
                .substanceNames(Optional.ofNullable(openFda)
                        .map(OpenFdaSearchResponse.Result.OpenFda::getSubstanceName)
                        .orElse(List.of()))
                .productNumbers(Optional.ofNullable(result.getProducts())
                        .stream()
                        .flatMap(List::stream)
                        .map(OpenFdaSearchResponse.Result.Product::getProductNumber)
                        .toList())
                .products(Optional.ofNullable(result.getProducts())
                        .stream()
                        .flatMap(List::stream)
                        .map(this::mapToProduct)
                        .toList())
                .submissions(Optional.ofNullable(result.getSubmissions())
                        .stream()
                        .flatMap(List::stream)
                        .map(this::mapToSubmission)
                        .toList());
    }

    private Product mapToProduct(OpenFdaSearchResponse.Result.Product product) {
        return new Product()
                .productNumber(product.getProductNumber())
                .brandName(product.getBrandName())
                .dosageForm(product.getDosageForm())
                .marketingStatus(product.getMarketingStatus())
                .referenceDrug(product.getReferenceDrug())
                .referenceStandard(product.getReferenceStandard())
                .teCode(product.getTeCode())
                .activeIngredients(Optional.ofNullable(product.getActiveIngredients())
                        .stream()
                        .flatMap(List::stream)
                        .map(ai -> new ActiveIngredient()
                                .name(ai.getName())
                                .strength(ai.getStrength()))
                        .toList());
    }

    private Submission mapToSubmission(OpenFdaSearchResponse.Result.Submission submission) {
        return new Submission()
                .submissionType(submission.getSubmissionType())
                .submissionNumber(submission.getSubmissionNumber())
                .submissionStatus(submission.getSubmissionStatus())
                .submissionStatusDate(submission.getSubmissionStatusDate())
                .submissionClassCode(submission.getSubmissionClassCode())
                .submissionClassCodeDescription(submission.getSubmissionClassCodeDescription());
    }
}
