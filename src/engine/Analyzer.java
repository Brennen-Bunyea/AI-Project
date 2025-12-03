package engine;

import model.TestResult;
import java.util.*;

public class Analyzer
{

    public void printWeaknesses(List<TestResult> allTests)
    {
        Map<Character, Integer> letterErrors = new HashMap<>();
        Map<String, Integer> digraphErrors = new HashMap<>();

        for (TestResult t : allTests) {
            t.letterMap.forEach((c, info) -> 
                letterErrors.put(c, letterErrors.getOrDefault(c, 0) + info.errors)
            );

            t.digraphMap.forEach((d, info) -> 
                digraphErrors.put(d, digraphErrors.getOrDefault(d, 0) + info.errors)
            );
        }

        System.out.println("\nMost problematic letters:");
        letterErrors.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        System.out.println("\nMost problematic digraphs:");
        digraphErrors.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
