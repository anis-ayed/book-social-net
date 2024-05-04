package com.ayed.booknetwork.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
  private Integer businessErrorCode;
  private String businessErrorDescription;
  private String error;
  private Set<String> validationErrors;
  private Map<String, String> errors;
}
