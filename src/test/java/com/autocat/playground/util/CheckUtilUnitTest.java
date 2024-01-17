package com.autocat.playground.util;

import com.autocat.playground.util.CheckUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CheckUtilUnitTest {

    @Test
    public void testIsNullOrEmptyString() {
        assertTrue(CheckUtil.isEmpty(""));
        assertTrue(CheckUtil.isEmpty((String) null));

        assertFalse(CheckUtil.isEmpty(" "));
        assertFalse(CheckUtil.isEmpty((char) 'T'));
        assertFalse(CheckUtil.isEmpty((char) ' '));
        assertFalse(CheckUtil.isEmpty("Test"));
    }

    @Test
    public void testIsEmptyArray() {
        Integer[] emptyArray = new Integer[]{};
        assertTrue(CheckUtil.isEmpty(emptyArray));

        Integer[] nonEmptyArray = new Integer[]{1, 2, 3};
        assertFalse(CheckUtil.isEmpty(nonEmptyArray));

        ArrayList<String> emptyArrayList = new ArrayList<>();
        assertTrue(CheckUtil.isEmpty(emptyArrayList));

        ArrayList<String> nonEmptyArrayList = new ArrayList<>();
        nonEmptyArrayList.add("Test");
        assertFalse(CheckUtil.isEmpty(nonEmptyArrayList));
    }

    @Test
    public void testIsEmptyCollection() {
        List<String> emptyList = Collections.emptyList();
        assertTrue(CheckUtil.isEmpty(emptyList));

        List<String> nonEmptyList = Arrays.asList("Test");
        assertFalse(CheckUtil.isEmpty(nonEmptyList));

        LinkedList<String> emptyLinkedList = new LinkedList<>();
        assertTrue(CheckUtil.isEmpty(emptyLinkedList));

        LinkedList<String> nonEmptyLinkedList = new LinkedList<>();
        nonEmptyLinkedList.add("Test");
        assertFalse(CheckUtil.isEmpty(nonEmptyLinkedList));
    }

    @Test
    public void testIsEmptyMap() {
        assertTrue(CheckUtil.isEmpty(Collections.emptyMap()));

        assertFalse(CheckUtil.isEmpty(Collections.singletonMap("key", "value")));

        HashMap<Integer, String> emptyHashMap = new HashMap<>();
        assertTrue(CheckUtil.isEmpty(emptyHashMap));

        HashMap<Integer, String> nonEmptyHashMap = new HashMap<>();
        nonEmptyHashMap.put(1, "Test");
        assertFalse(CheckUtil.isEmpty(nonEmptyHashMap));

        LinkedHashMap<Integer, String> emptyLinkedHashMap = new LinkedHashMap<>();
        assertTrue(CheckUtil.isEmpty(emptyLinkedHashMap));

        LinkedHashMap<Integer, String> nonEmptyLinkedHashMap = new LinkedHashMap<>();
        nonEmptyLinkedHashMap.put(1, "Test");
        assertFalse(CheckUtil.isEmpty(nonEmptyLinkedHashMap));
    }

    @Test
    public void testIsEmptyNull() {
        assertTrue(CheckUtil.isEmpty(null));
    }

    @Test
    public void testIsEmptyOptional() {
        assertTrue(CheckUtil.isEmpty(Optional.empty()));

        assertFalse(CheckUtil.isEmpty(Optional.of("Test")));
    }

    @Test
    public void testIsEmptyFalseBoolean() {
        assertTrue(CheckUtil.isEmpty(Boolean.FALSE));

        assertFalse(CheckUtil.isEmpty(Boolean.TRUE));
    }

    @Test
    public void testIsEmptyNumber() {
        assertTrue(CheckUtil.isEmpty(0.0));
        assertTrue(CheckUtil.isEmpty(-0.0));

        assertFalse(CheckUtil.isEmpty(-1));
        assertFalse(CheckUtil.isEmpty(1.0));
    }

    @Test
    public void testIsEmptyChar() {
        assertTrue(CheckUtil.isEmpty('\u0000'));

        assertFalse(CheckUtil.isEmpty('A'));
    }
}