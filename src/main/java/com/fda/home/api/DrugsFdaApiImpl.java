package com.fda.home.api;

import com.fda.home.service.DrugApplicationService;
import com.openfda.generated.controllers.DrugsFdaApi;
import com.openfda.generated.models.DrugApplicationDto;
import com.openfda.generated.models.DrugApplicationRequest;
import com.openfda.generated.models.SearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DrugsFdaApiImpl implements DrugsFdaApi {
    private final DrugApplicationService drugApplicationService;

    @Override
    public ResponseEntity<List<DrugApplicationDto>> getDrugApplications() {
        return ResponseEntity.ok(drugApplicationService.getDrugApplications());
    }

    @Override
    public ResponseEntity<Void> createDrugApplication(DrugApplicationRequest drugApplication) {
        drugApplicationService.createDrugApplication(drugApplication);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<SearchResponse> searchDrugApplications(String manufacturerName, String brandName, Integer page, Integer size) {
        return ResponseEntity.ok(drugApplicationService.searchDrugApplications(manufacturerName, brandName, page, size));
    }
}
