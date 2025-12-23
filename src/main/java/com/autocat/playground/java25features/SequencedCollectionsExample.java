package com.autocat.playground.java25features;

import java.util.*;

/**
 * Java 21 - Sequenced Collections (순서가 있는 컬렉션)
 *
 * 컬렉션에서 정의된 순서대로 요소에 접근할 수 있는 새로운 인터페이스입니다.
 * 첫 번째/마지막 요소 접근 및 역순 뷰를 통일된 API로 제공합니다.
 *
 * 새로운 인터페이스 계층:
 * - SequencedCollection: 순서가 있는 컬렉션
 * - SequencedSet: 순서가 있는 Set
 * - SequencedMap: 순서가 있는 Map
 *
 * 적용된 클래스:
 * - List, Deque -> SequencedCollection
 * - LinkedHashSet, SortedSet -> SequencedSet
 * - LinkedHashMap, SortedMap -> SequencedMap
 */
public class SequencedCollectionsExample {

    /**
     * SequencedCollection 기본 사용법
     */
    public void basicSequencedCollection() {
        List<String> list = new ArrayList<>(List.of("첫째", "둘째", "셋째", "넷째"));

        // 첫 번째 요소 접근
        String first = list.getFirst();
        System.out.println("첫 번째: " + first);  // 첫째

        // 마지막 요소 접근
        String last = list.getLast();
        System.out.println("마지막: " + last);  // 넷째

        // 첫 번째에 요소 추가
        list.addFirst("새 첫째");
        System.out.println("addFirst 후: " + list);  // [새 첫째, 첫째, 둘째, 셋째, 넷째]

        // 마지막에 요소 추가
        list.addLast("새 마지막");
        System.out.println("addLast 후: " + list);  // [새 첫째, 첫째, 둘째, 셋째, 넷째, 새 마지막]

        // 첫 번째 요소 제거
        String removedFirst = list.removeFirst();
        System.out.println("제거된 첫 번째: " + removedFirst);  // 새 첫째

        // 마지막 요소 제거
        String removedLast = list.removeLast();
        System.out.println("제거된 마지막: " + removedLast);  // 새 마지막
    }

    /**
     * reversed() - 역순 뷰 사용
     */
    public void reversedView() {
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        // 역순 뷰 생성 (새로운 리스트가 아닌 뷰)
        SequencedCollection<Integer> reversed = numbers.reversed();
        System.out.println("원본: " + numbers);     // [1, 2, 3, 4, 5]
        System.out.println("역순 뷰: " + reversed);  // [5, 4, 3, 2, 1]

        // 역순 뷰를 통한 순회
        System.out.print("역순 순회: ");
        for (Integer num : numbers.reversed()) {
            System.out.print(num + " ");  // 5 4 3 2 1
        }
        System.out.println();

        // 역순 뷰 수정 시 원본도 변경됨
        reversed.addFirst(0);  // 역순의 첫 번째 = 원본의 마지막
        System.out.println("수정 후 원본: " + numbers);  // [1, 2, 3, 4, 5, 0]
    }

    /**
     * SequencedSet 사용법
     */
    public void sequencedSetExample() {
        // LinkedHashSet은 삽입 순서 유지
        SequencedSet<String> linkedSet = new LinkedHashSet<>();
        linkedSet.add("Apple");
        linkedSet.add("Banana");
        linkedSet.add("Cherry");

        System.out.println("첫 번째: " + linkedSet.getFirst());  // Apple
        System.out.println("마지막: " + linkedSet.getLast());    // Cherry
        System.out.println("역순: " + linkedSet.reversed());     // [Cherry, Banana, Apple]

        // TreeSet은 정렬된 순서
        SequencedSet<Integer> treeSet = new TreeSet<>(List.of(5, 2, 8, 1, 9));
        System.out.println("TreeSet: " + treeSet);           // [1, 2, 5, 8, 9]
        System.out.println("TreeSet 첫 번째: " + treeSet.getFirst());  // 1
        System.out.println("TreeSet 마지막: " + treeSet.getLast());    // 9
    }

    /**
     * SequencedMap 사용법
     */
    public void sequencedMapExample() {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        // 첫 번째/마지막 엔트리
        System.out.println("첫 번째 엔트리: " + map.firstEntry());  // one=1
        System.out.println("마지막 엔트리: " + map.lastEntry());    // three=3

        // 첫 번째에 추가 (기존 엔트리 이동)
        map.putFirst("zero", 0);
        System.out.println("putFirst 후: " + map);  // {zero=0, one=1, two=2, three=3}

        // 마지막에 추가
        map.putLast("four", 4);
        System.out.println("putLast 후: " + map);  // {zero=0, one=1, two=2, three=3, four=4}

        // 첫 번째/마지막 제거
        Map.Entry<String, Integer> removedFirst = map.pollFirstEntry();
        Map.Entry<String, Integer> removedLast = map.pollLastEntry();
        System.out.println("제거된 첫 번째: " + removedFirst);  // zero=0
        System.out.println("제거된 마지막: " + removedLast);    // four=4

        // 역순 뷰
        SequencedMap<String, Integer> reversedMap = map.reversed();
        System.out.println("역순 맵: " + reversedMap);  // {three=3, two=2, one=1}

        // sequencedKeySet, sequencedValues, sequencedEntrySet
        System.out.println("키셋 역순: " + map.sequencedKeySet().reversed());
        System.out.println("값 역순: " + map.sequencedValues().reversed());
    }

    /**
     * 기존 방식과 비교
     */
    public void compareWithOldWay() {
        List<String> list = List.of("A", "B", "C", "D");

        // 기존 방식 (Java 20 이전)
        String firstOld = list.get(0);
        String lastOld = list.get(list.size() - 1);

        // 새로운 방식 (Java 21+)
        String firstNew = list.getFirst();
        String lastNew = list.getLast();

        System.out.println("첫 번째 (기존): " + firstOld + " (새로운): " + firstNew);
        System.out.println("마지막 (기존): " + lastOld + " (새로운): " + lastNew);

        // Deque - 기존에도 있었지만 이제 통일된 API
        Deque<Integer> deque = new ArrayDeque<>(List.of(1, 2, 3));
        System.out.println("Deque 첫 번째: " + deque.getFirst());
        System.out.println("Deque 마지막: " + deque.getLast());
        System.out.println("Deque 역순: " + deque.reversed());
    }
}
