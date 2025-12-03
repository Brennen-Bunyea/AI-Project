package engine;

import model.TestResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionEngine {

    // Predict probability of mistyping a letter (0–1)
    public double predictLetterErrorProbability(char c, List<TestResult> history)
    {
        int errors = 0;
        int attempts = 0;

        for (TestResult t : history) {
            if (t.letterMap.containsKey(c)) {
                errors += t.letterMap.get(c).errors;
                attempts += 1;
            }
        }

        if (attempts == 0) return 0.0;  // no info yet
        return (double) errors / attempts;
    }

    // Predict difficulty of a word using letter difficulties
    // Score is 0 (easy) to 1+ (harder)
    public double predictWordDifficulty(String word, List<TestResult> history)
    {
        double totalScore = 0.0;
        int count = 0;

        for (char c : word.toCharArray()) {
            totalScore += predictLetterErrorProbability(c, history);
            count++;
        }

        if (count == 0) return 0.0;

        return totalScore / count; // average difficulty
    }

    // Predict next WPM based on moving average of last tests
    public double predictNextWPM(List<TestResult> history)
    {
        if (history.isEmpty()) return 0;

        double total = 0;
        int count = 0;

        // average the last 5 tests (simple)
        for (int i = history.size() - 1; i >= 0 && count < 5; i--)
        {
            total += history.get(i).wpm;
            count++;
        }

        if (count == 0) return 0;
        return total / count;
    }

    // Predict which letters will be hardest next test
    // Returns a sorted map (letter -> score)
    public Map<Character, Double> predictTopHardLetters(List<TestResult> history)
    {
        Map<Character, Double> map = new HashMap<>();

        for (TestResult t : history) {
            for (char c : t.letterMap.keySet()) {
                double score = predictLetterErrorProbability(c, history);
                map.put(c, score);
            }
        }

        return map;
    }
}
