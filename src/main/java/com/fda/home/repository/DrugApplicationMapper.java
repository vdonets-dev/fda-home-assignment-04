package com.fda.home.repository;

import com.fda.home.model.DrugApplication;
import com.openfda.generated.models.DrugApplicationRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DrugApplicationMapper {
    void insertDrugApplicationWithDetails(DrugApplicationRequest drugApplication);
    List<DrugApplication> getAllDrugApplications();
    DrugApplication getDrugApplicationById(String applicationNumber);
}
