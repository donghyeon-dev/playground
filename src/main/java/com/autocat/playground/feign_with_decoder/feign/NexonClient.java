package com.autocat.playground.feign_with_decoder.feign;

import com.autocat.playground.feign_with_decoder.dto.Character;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Nexon", url = "https://open.api.nexon.com", configuration = FeignConfig.class)
public interface NexonClient {
    @GetMapping("/maplestory/v1/character/hyper-stat")
    Character getCharacterHyperStat(@RequestParam(value = "ocid", defaultValue = "985664fe5f82eeae91b658c5b5f650ab") String ocid
            , @RequestParam(value = "date", defaultValue = "2024-01-09") String date
    , @RequestHeader(value = "x-nxopen-api-key", defaultValue = "test_487e24c821190fe3aa97781386c7975a11954dff55f5581cb01f04389bd81777d0d06b52b171467c30194aef7009d658") String authorization);
}
