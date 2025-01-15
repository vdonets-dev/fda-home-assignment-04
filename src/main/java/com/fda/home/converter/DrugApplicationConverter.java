package com.fda.home.converter;

import com.fda.home.model.DrugApplication;
import com.openfda.generated.models.DrugApplicationDto;
import org.springframework.stereotype.Component;

@Component
public class DrugApplicationConverter extends BiConverter<DrugApplication, DrugApplicationDto> {

    @Override
    protected DrugApplicationDto doConvert(DrugApplication input) {
        return new DrugApplicationDto()
                .applicationNumber(input.getApplicationNumber())
                .manufacturerNames(input.getManufacturerNames())
                .substanceNames(input.getSubstanceNames())
                .productNumbers(input.getProductNumbers());
    }
}
