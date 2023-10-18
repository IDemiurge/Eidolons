package apps;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 9/6/2023
 */
public class CodeProblems {

            @Test
            public void test() {
                int[] arr1 = {2,3,1,3,2,4,6,7,9,2,19};
                int[] arr2 = {2,1,4,3,9,6};
                int[] expected = {2, 2, 2, 1, 4, 3, 3, 9, 6, 7, 19};

                int[] sorted = relativeSortArray(arr1, arr2);
                assertTrue(toString(expected).equals(toString(sorted)));

            }

            private Object toString(int[] expected) {
                StringBuilder sb = new StringBuilder("");
                Arrays.stream(expected).forEach(i->sb.append(i));
                return sb.toString();
            }

            public int[] relativeSortArray(int[] arr1, int[] arr2) {
                Map<Integer, Integer> occurMap = new HashMap<>();
                List<Integer> list = new ArrayList();
                for (int i : arr2) {
                    list.add(i);
                }

                int index = 0;
                List<Integer> missing = new ArrayList<>();
                for (int i : arr1) {
                    if (!list.contains(i)) {
                        missing.add(i);
                    } else{

                        occurMap.put(i, occurMap.getOrDefault(i, 0)+1);
                    }
                        // occurMap.computeIfAbsent(i, x -> new LinkedList<>()).add(index++);

                }

                index = 0;
                int[] sorted = new int[arr1.length];
                for (int i : arr2) {
                    for (int n= 0; n < occurMap.get(i); n++)
                        sorted[index++] = i;

                }

                Collections.sort(missing);
                int gap = missing.size();
                for (int i = 0; i < gap; i++) {
                    sorted[index + i] = missing.get(i);
                }

                return sorted;
            }

            public List<List<String>> groupAnagrams(String[] strs) {
                Map<String, List<String>> map = new LinkedHashMap<>();
                for (String str : strs) {
                    char[] charArray = str.toCharArray();
                    Arrays.sort(charArray);
                    String sortedStr = new String(charArray);
                    map.computeIfAbsent(sortedStr, s -> new ArrayList<>()).add(str);
                }
                return map.keySet().stream().map(key -> map.get(key)).collect(Collectors.toList());
            }
        }
    //     Map<Integer, List<String>> map = new LinkedHashMap<>();
    //     for (String str : strs) {
    //
    //         List<Character> chars = Arrays.asList(str.split("")).stream().map(s -> new Character(s.toCharArray()[0])).collect(Collectors.toList());
    //         // Stream<Character> stream = chars.stream();
    //         int sum = 0;
    //         for (Character c : str.toCharArray()) {
    //             sum += (int) c;
    //         }
    //
    //         map.computeIfAbsent(sum, s -> new ArrayList<>()).add(str);
    //     }
    //     return map.keySet().stream().map(key -> map.get(key)).collect(Collectors.toList());
    // }


