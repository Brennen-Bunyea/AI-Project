package util;

import java.util.Random;

public class RandomSentenceGenerator
{

    private static final String[] WORDS = {
            "hello", "world", "keyboard", "java", "typing", "speed", "random",
            "practice", "computer", "letters", "accuracy", "training", "sample",
            "example", "program", "model", "predict", "pattern", "learn", "skill",
            "algorithm", "system", "process", "method", "object", "input", "output",
            "memory", "storage", "analysis", "simple", "yellow", "winter", "summer",
            "autumn", "spring", "forest", "mountain", "valley", "river", "ocean",
            "animal", "planet", "station", "engine", "library", "school", "college",
            "student", "teacher", "friend", "family", "garden", "kitchen", "window",
            "bottle", "camera", "pencil", "notebook", "paper", "coffee", "energy",
            "motion", "office", "action", "future", "history", "science", "music",
            "secret", "quiet", "storm", "sunlight", "shadow", "midnight", "morning",
            "evening", "night", "bridge", "design", "rocket", "planet", "signal",
            "gravity", "network", "wireless", "cable", "battery", "charge",
            "language", "syntax", "variable", "function", "object", "class",
            "method", "public", "private", "static", "return", "boolean",
            "integer", "string", "double", "float", "thread", "process",
            "database", "sqlite", "query", "statement", "packet", "server",
            "client", "socket", "router", "buffer", "action", "motion", "travel",
            "running", "walking", "jumping", "flying", "swimming", "moving",
            "energy", "force", "matter", "element", "crystal", "metal", "carbon",
            "oxygen", "hydrogen", "fusion", "atom", "engineer", "builder", "artist",
            "writer", "reader", "market", "village", "country", "island", "bridge",
            "highway", "tunnel", "station", "airport", "harbor", "castle", "temple",
            "forest", "desert", "canyon", "garden", "flower", "pathway", "compass",
            "journey", "adventure", "victory", "failure", "challenge", "problem",
            "solution", "effort", "focus", "strength", "courage", "wisdom", "habit",
            "memory", "future", "dream", "vision", "rhythm", "melody", "whisper",
            "silence", "thunder", "lightning", "sunrise", "sunset", "planet",
            "orbit", "signal", "energy", "motion", "pattern", "reason"
    };


    public static String generateSentence(int wordCount)
    {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            sb.append(WORDS[rand.nextInt(WORDS.length)]);
            if (i < wordCount - 1) sb.append(" ");
        }

        return sb.toString();
    }
}
