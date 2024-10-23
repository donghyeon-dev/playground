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

import java.util.function.Consumer;
import java.util.function.Predicate;

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


}