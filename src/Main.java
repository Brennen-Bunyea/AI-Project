import db.Database;
import engine.TypingTestEngine;
import engine.Analyzer;
import model.TestResult;
import util.RandomSentenceGenerator;
import engine.PredictionEngine;


import java.util.*;

public class Main
{
    public static void main(String[] args)
    {

        Database.initialize();

        Scanner sc = new Scanner(System.in);
        TypingTestEngine engine = new TypingTestEngine();
        Analyzer analyzer = new Analyzer();

        while (true)
        {
            System.out.println("\n==== TYPING TEST AI ====");
            System.out.println("1. Take a test");
            System.out.println("2. View weaknesses");
            System.out.println("3. Predictions");
            System.out.println("4. Exit");
            System.out.println("5. Clear all typing data");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            PredictionEngine predictor = new PredictionEngine();

            //used switch to make case uses easier
            switch (choice)
            {
                //the arrows are a new implementation of Java, it basically just removes the need for a break
                case 1 -> {
                    String sentence = RandomSentenceGenerator.generateSentence(8);
                    TestResult result = engine.runTest(sentence);
                    Database.saveTest(result);

                    System.out.println("\nSaved test!");
                    System.out.println("WPM: " + result.wpm);
                    System.out.println("Accuracy: " + (result.accuracy * 100) + "%");
                }
                case 2 ->
                {
                    List<TestResult> history = Database.loadAllTests();
                    analyzer.printWeaknesses(history);
                }
                case 3 ->
                {
                    List<TestResult> history = Database.loadAllTests();
                    showPredictions(history, predictor);
                }
                case 4 -> System.exit(0);
                case 5 ->
                {
                    Database.clearAllData();
                    System.out.println("\nAll typing data cleared.");
                }

            }
        }
    }

    public static void showPredictions(List<TestResult> history, PredictionEngine predictor)
    {
        if (history.isEmpty()) {
            System.out.println("\nNot enough data for predictions yet.");
            return;
        }

        System.out.println("\n=== AI Predictions ===");

        double predictedWPM = predictor.predictNextWPM(history);
        System.out.println("Predicted next WPM: " + predictedWPM);

        // Example word difficulty prediction:
        String sample = "keyboard";
        double difficulty = predictor.predictWordDifficulty(sample, history);
        System.out.println("Predicted difficulty of '" + sample + "': " + difficulty);

        // Predict the hardest letters
        System.out.println("\nPredicted hardest letters:");
        predictor.predictTopHardLetters(history)
                .entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(e ->
                        System.out.println(e.getKey() + ": " + e.getValue())
                );
    }

}
