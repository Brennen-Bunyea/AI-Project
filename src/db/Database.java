package db;

import model.TestResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:typing_ai.db";

    public static Connection connect()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e)
        {
            throw new RuntimeException("Could not connect to SQLite.", e);
        }
    }

    public static void initialize() {
        String testsTable = "CREATE TABLE IF NOT EXISTS typing_tests ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "timestamp TEXT,"
                + "prompt TEXT,"
                + "typed TEXT,"
                + "wpm REAL,"
                + "accuracy REAL);";

        String letterStats = "CREATE TABLE IF NOT EXISTS letter_stats ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "test_id INTEGER,"
                + "letter TEXT,"
                + "errors INTEGER,"
                + "avg_time REAL,"
                + "FOREIGN KEY(test_id) REFERENCES typing_tests(id));";

        String digraphStats = "CREATE TABLE IF NOT EXISTS digraph_stats ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "test_id INTEGER,"
                + "digraph TEXT,"
                + "errors INTEGER,"
                + "avg_time REAL,"
                + "FOREIGN KEY(test_id) REFERENCES typing_tests(id));";

        try (Connection conn = connect())
        {
            conn.createStatement().execute(testsTable);
            conn.createStatement().execute(letterStats);
            conn.createStatement().execute(digraphStats);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveTest(TestResult result)
    {
        String insertTest = "INSERT INTO typing_tests (timestamp, prompt, typed, wpm, accuracy) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(insertTest, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, LocalDateTime.now().toString());
            ps.setString(2, result.prompt);
            ps.setString(3, result.typed);
            ps.setDouble(4, result.wpm);
            ps.setDouble(5, result.accuracy);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int testId = keys.getInt(1);
                saveLetterStats(testId, result);
                saveDigraphStats(testId, result);
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void saveLetterStats(int testId, TestResult result)
    {
        String sql = "INSERT INTO letter_stats (test_id, letter, errors, avg_time) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql))
        {

            for (var entry : result.letterMap.entrySet())
            {
                char letter = entry.getKey();
                TestResult.LetterInfo info = entry.getValue();

                ps.setInt(1, testId);
                ps.setString(2, String.valueOf(letter));
                ps.setInt(3, info.errors);
                ps.setDouble(4, info.avgTime);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void saveDigraphStats(int testId, TestResult result)
    {
        String sql = "INSERT INTO digraph_stats (test_id, digraph, errors, avg_time) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql))
        {

            for (var entry : result.digraphMap.entrySet())
            {
                String digraph = entry.getKey();
                TestResult.LetterInfo info = entry.getValue();

                ps.setInt(1, testId);
                ps.setString(2, digraph);
                ps.setInt(3, info.errors);
                ps.setDouble(4, info.avgTime);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static List<TestResult> loadAllTests()
    {
        List<TestResult> list = new ArrayList<>();

        String sql = "SELECT * FROM typing_tests";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            while (rs.next())
            {
                TestResult t = new TestResult();
                t.prompt = rs.getString("prompt");
                t.typed = rs.getString("typed");
                t.wpm = rs.getDouble("wpm");
                t.accuracy = rs.getDouble("accuracy");

                int testId = rs.getInt("id");
                t.letterMap = loadLetters(testId);
                t.digraphMap = loadDigraphs(testId);

                list.add(t);
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    private static Map<Character, TestResult.LetterInfo> loadLetters(int testId)
    {
        Map<Character, TestResult.LetterInfo> map = new HashMap<>();
        String sql = "SELECT letter, errors, avg_time FROM letter_stats WHERE test_id = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql))
        {

            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                char letter = rs.getString("letter").charAt(0);
                int errors = rs.getInt("errors");
                double avgTime = rs.getDouble("avg_time");

                map.put(letter, new TestResult.LetterInfo(errors, avgTime));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return map;
    }

    private static Map<String, TestResult.LetterInfo> loadDigraphs(int testId)
    {
        Map<String, TestResult.LetterInfo> map = new HashMap<>();
        String sql = "SELECT digraph, errors, avg_time FROM digraph_stats WHERE test_id = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql))
        {

            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String digraph = rs.getString("digraph");
                int errors = rs.getInt("errors");
                double avgTime = rs.getDouble("avg_time");

                map.put(digraph, new TestResult.LetterInfo(errors, avgTime));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return map;
    }

    public static void clearAllData() {
        try (Connection conn = connect()) {
            conn.createStatement().execute("DELETE FROM typing_tests;");
            conn.createStatement().execute("DELETE FROM letter_stats;");
            conn.createStatement().execute("DELETE FROM digraph_stats;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
