package com.autocat.playground.feign_with_decoder.controller;

import com.autocat.playground.feign_with_decoder.dto.Character;
import com.autocat.playground.feign_with_decoder.feign.NexonClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/character")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CharacterController {

    private final NexonClient nexonClient;

    @GetMapping("/hyper-stat")
    Character getCharacterHyperStat() {
        Character character = nexonClient.getCharacterHyperStat("985664fe5f82eeae91b658c5b5f650ab"
                , "2024-01-09"
                , "test_487e24c821190fe3aa97781386c7975a11954dff55f5581cb01f04389bd81777d0d06b52b171467c30194aef7009d658");

        log.info("CharacterClass is {}", character.getCharacterClass());
        return character;
    }
}
