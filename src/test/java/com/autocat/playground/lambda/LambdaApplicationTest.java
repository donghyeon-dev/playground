package com.autocat.playground.lambda;

import com.autocat.playground.lambda.dto.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class LambdaApplicationTest {

    @Test
    void prdicate_test(){
        Predicate<String> predicate = s -> s.length() == 3;
        boolean result = predicate.test("abc");
        boolean result2 = predicate.test("abcd");

        assertTrue(result);
        assertFalse(result2);
    }

    @Test
    void lambda_expression_with_local_values_returns_error(){
        int totalPrice = 0;
        Consumer<Product> consumer = product -> totalPrice += product.getPrice(); // error case

    }

    @Test
    void filtering_with_stream(){
        List<String> names = Arrays.asList("John", "Jane", "Tom", "Jerry");

        List<String> filteredNames = names.stream()
                .filter(name -> name.length() == 3)
                .toList();

        assertEquals("Tom", filteredNames.get(0));
    }

    @Test
    void mapping_and_sorting(){
        List<String> names = Arrays.asList("John", "Jane", "Tom", "Jerry");

        List<String> sortedNames = names.stream()
                .map(String::toUpperCase)
                .sorted()
                .toList();

        assertEquals("JANE", sortedNames.get(0));
        assertEquals("JERRY", sortedNames.get(1));
        assertEquals("JOHN", sortedNames.get(2));
        assertEquals("TOM", sortedNames.get(3));
    }

    @Test
    void mapping_and_reverse_sorting_with_comparator(){
        List<String> names = Arrays.asList("John", "Jane", "Tom", "Jerry");

        List<String> sortedNames = names.stream()
                .map(String::toUpperCase)
                .sorted(Comparator.reverseOrder())
                .toList();

        assertEquals("TOM", sortedNames.get(0));
        assertEquals("JOHN", sortedNames.get(1));
        assertEquals("JERRY", sortedNames.get(2));
        assertEquals("JANE", sortedNames.get(3));
    }

    @Test
    void mapping_and_sorting_by_field(){
        List<String> names = Arrays.asList("James", "Jane", "Tom", "Verstapen");

        List<String> sortedNames = names.stream()
                .map(String::toUpperCase)
                .sorted(Comparator.comparingInt(String::length))
                .toList();

        assertEquals("TOM", sortedNames.get(0));
        assertEquals("JANE", sortedNames.get(1));
        assertEquals("JAMES", sortedNames.get(2));
        assertEquals("VERSTAPEN", sortedNames.get(3));
    }

    @Test
    void stream_with_parallel_streams(){
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        int sum = numbers.parallelStream() // 병렬 스트림 생성
                .filter(n -> n % 2 == 0) // 중간 연산: 짝수 필터링
                .mapToInt(n -> n) // 중간 연산: int로 변환
                .sum(); // 최종 연산: 합계 계산

        assertEquals(30, sum);
    }


}