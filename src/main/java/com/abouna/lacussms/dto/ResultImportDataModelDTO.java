package com.abouna.lacussms.dto;

import java.util.ArrayList;
import java.util.List;

public class ResultImportDataModelDTO {
    private List<SuccessImportDataModelDTO> success;
    private List<FailImportDataModelDTO> errors;

    public ResultImportDataModelDTO() {
        success = new ArrayList<>();
        errors = new ArrayList<>();
    }

    public ResultImportDataModelDTO(List<SuccessImportDataModelDTO> success, List<FailImportDataModelDTO> errors) {
        this.success = success;
        this.errors = errors;
    }

    public List<SuccessImportDataModelDTO> getSuccess() {
        return success;
    }

    public void setSuccess(List<SuccessImportDataModelDTO> success) {
        this.success = success;
    }

    public List<FailImportDataModelDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<FailImportDataModelDTO> errors) {
        this.errors = errors;
    }
}
