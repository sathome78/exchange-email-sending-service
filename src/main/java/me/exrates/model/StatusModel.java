package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusModel {
    private Integer currentNumber;
    private Integer totalEmail;
    private String currentEmail;
    private String error;
    private String status;
}
