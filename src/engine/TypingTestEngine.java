package engine;

import model.TestResult;
import java.util.*;

public class TypingTestEngine
{

    public TestResult runTest(String prompt)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nType this:");
        System.out.println(prompt);
        System.out.println("\nPress ENTER when ready...");
        sc.nextLine();

        long start = System.currentTimeMillis();
        String typed = sc.nextLine();
        long end = System.currentTimeMillis();

        double timeMinutes = (end - start) / 60000.0;
        int charactersTyped = typed.length();

        // Standard typing test formula
        double wpm = (charactersTyped / 5.0) / timeMinutes;

        double accuracy = calculateAccuracy(prompt, typed);
        Map<Character, TestResult.LetterInfo> letterStats = analyzeLetters(prompt, typed);
        Map<String, TestResult.LetterInfo> digraphStats = analyzeDigraphs(prompt, typed);

        TestResult result = new TestResult();
        result.prompt = prompt;
        result.typed = typed;
        result.wpm = wpm;
        result.accuracy = accuracy;
        result.letterMap = letterStats;
        result.digraphMap = digraphStats;

        return result;
    }

    private double calculateAccuracy(String prompt, String typed)
    {
        int max = Math.max(prompt.length(), typed.length());
        int correct = 0;

        for (int i = 0; i < Math.min(prompt.length(), typed.length()); i++)
        {
            if (prompt.charAt(i) == typed.charAt(i)) correct++;
        }

        return (double) correct / max;
    }

    private Map<Character, TestResult.LetterInfo> analyzeLetters(String prompt, String typed)
    {
        Map<Character, TestResult.LetterInfo> stats = new HashMap<>();

        for (int i = 0; i < prompt.length(); i++)
        {
            char c = prompt.charAt(i);
            boolean correct = (i < typed.length() && typed.charAt(i) == c);

            stats.putIfAbsent(c, new TestResult.LetterInfo(0, 0));
            if (!correct) stats.get(c).errors++;
        }
        return stats;
    }

    private Map<String, TestResult.LetterInfo> analyzeDigraphs(String prompt, String typed)
    {
        Map<String, TestResult.LetterInfo> stats = new HashMap<>();

        for (int i = 0; i < prompt.length() - 1; i++) {
            String d = "" + prompt.charAt(i) + prompt.charAt(i + 1);
            boolean correct = (i < typed.length() - 1 && typed.substring(i, i + 2).equals(d));

            stats.putIfAbsent(d, new TestResult.LetterInfo(0, 0));
            if (!correct) stats.get(d).errors++;
        }
        return stats;
    }
}
