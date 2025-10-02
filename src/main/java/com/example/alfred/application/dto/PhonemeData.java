package com.example.alfred.application.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhonemeData {
    private String symbol;
    private double startTime;
    private double endTime;
}
