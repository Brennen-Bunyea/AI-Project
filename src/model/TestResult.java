package model;

import java.util.Map;

public class TestResult
{
    public String prompt;
    public String typed;
    public double wpm;
    public double accuracy;
    public Map<Character, LetterInfo> letterMap;
    public Map<String, LetterInfo> digraphMap;

    public static class LetterInfo
    {
        public int errors;
        public double avgTime;

        public LetterInfo(int errors, double avgTime)
        {
            this.errors = errors;
            this.avgTime = avgTime;
        }
    }
}
