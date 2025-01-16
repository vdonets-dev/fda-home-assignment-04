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
                .meta(mapMeta(input))
                .drugApplications(Optional.ofNullable(input)
                        .map(OpenFdaSearchResponse::getResults)
                        .stream()
                        .flatMap(List::stream)
                        .map(this::mapToDrugApplication)
                        .toList());
    }

    private SearchResponseMeta mapMeta(OpenFdaSearchResponse input) {
        return Optional.ofNullable(input)
                .map(OpenFdaSearchResponse::getMeta)
                .map(OpenFdaSearchResponse.Meta::getResults)
                .map(results -> {
                    int totalPages = (int) Math.ceil((double) results.getTotal() / results.getLimit());
                    return new SearchResponseMeta()
                            .skip(results.getSkip())
                            .limit(results.getLimit())
                            .total(results.getTotal())
                            .totalPages(totalPages);
                })
                .orElse(null);
    }

    private DrugApplicationDetails mapToDrugApplication(OpenFdaSearchResponse.Result result) {
        return Optional.ofNullable(result)
                .map(r -> {
                    OpenFdaSearchResponse.Result.OpenFda openFda = r.getOpenfda();
                    return new DrugApplicationDetails()
                            .applicationNumber(r.getApplicationNumber())
                            .sponsorName(r.getSponsorName())
                            .manufacturerNames(Optional.ofNullable(openFda)
                                    .map(OpenFdaSearchResponse.Result.OpenFda::getManufacturerName)
                                    .orElse(List.of()))
                            .brandNames(Optional.ofNullable(openFda)
                                    .map(OpenFdaSearchResponse.Result.OpenFda::getBrandName)
                                    .orElse(List.of()))
                            .substanceNames(Optional.ofNullable(openFda)
                                    .map(OpenFdaSearchResponse.Result.OpenFda::getSubstanceName)
                                    .orElse(List.of()))
                            .productNumbers(Optional.ofNullable(r.getProducts())
                                    .stream()
                                    .flatMap(List::stream)
                                    .map(OpenFdaSearchResponse.Result.Product::getProductNumber)
                                    .toList())
                            .products(Optional.ofNullable(r.getProducts())
                                    .stream()
                                    .flatMap(List::stream)
                                    .map(this::mapToProduct)
                                    .toList())
                            .submissions(Optional.ofNullable(r.getSubmissions())
                                    .stream()
                                    .flatMap(List::stream)
                                    .map(this::mapToSubmission)
                                    .toList());
                })
                .orElse(null);
    }

    private Product mapToProduct(OpenFdaSearchResponse.Result.Product product) {
        return Optional.ofNullable(product)
                .map(p -> new Product()
                        .productNumber(p.getProductNumber())
                        .brandName(p.getBrandName())
                        .dosageForm(p.getDosageForm())
                        .marketingStatus(p.getMarketingStatus())
                        .referenceDrug(p.getReferenceDrug())
                        .referenceStandard(p.getReferenceStandard())
                        .teCode(p.getTeCode())
                        .activeIngredients(Optional.ofNullable(p.getActiveIngredients())
                                .stream()
                                .flatMap(List::stream)
                                .map(ai -> new ActiveIngredient()
                                        .name(ai.getName())
                                        .strength(ai.getStrength()))
                                .toList()))
                .orElse(null);
    }

    private Submission mapToSubmission(OpenFdaSearchResponse.Result.Submission submission) {
        return Optional.ofNullable(submission)
                .map(s -> new Submission()
                        .submissionType(s.getSubmissionType())
                        .submissionNumber(s.getSubmissionNumber())
                        .submissionStatus(s.getSubmissionStatus())
                        .submissionStatusDate(s.getSubmissionStatusDate())
                        .submissionClassCode(s.getSubmissionClassCode())
                        .submissionClassCodeDescription(s.getSubmissionClassCodeDescription()))
                .orElse(null);
    }
}
