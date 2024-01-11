package com.autocat.playground.feign_with_decoder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;


/**
 * BlahBlah Entity 의 DT 를 도와주는 DTO 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HyperStatPreset {

    @JsonProperty("stat_type")
    private String statType;

    @JsonProperty("stat_name")
    private Long statPoint;

    @JsonProperty("stat_level")
    private Long statLevel;

    @JsonProperty("stat_increase")
    private String statIncrease;
}
