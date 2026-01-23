package com.zone01oujda.moblogging.react.dto;

import com.zone01oujda.moblogging.react.enums.ReactType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReactSummaryDto {
    private long likeCount;
    private long dislikeCount;
    private ReactType userReact;
}
