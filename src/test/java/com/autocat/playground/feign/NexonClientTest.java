package com.autocat.playground.feign;

import com.autocat.playground.feign_with_decoder.dto.Character;
import com.autocat.playground.feign_with_decoder.feign.NexonClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class NexonClientTest {

    @Autowired
    private NexonClient nexonClient;


    @DisplayName("snake_case의 데이터를 camelCaseDTO로 받도록 설정없이 요청을 보내본다")
    @Test
    void getCharacterHyperStat() {
        Character character = nexonClient.getCharacterHyperStat("985664fe5f82eeae91b658c5b5f650ab"
                , "2024-01-09"
                , "test_487e24c821190fe3aa97781386c7975a11954dff55f5581cb01f04389bd81777d0d06b52b171467c30194aef7009d658");
        log.info("character = {}", character);

    }

}