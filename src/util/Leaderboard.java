package util;

import java.io.*;
import java.util.*;

public class Leaderboard {
    private static final String FILE_PATH = "leaderboard.txt";
    private static final int MAX_ENTRIES = 10;

    public static class Entry {
        public final String name;
        public final int kills;

        public Entry(String name, int kills) {
            this.name = name;
            this.kills = kills;
        }
    }

    public static List<Entry> load() {
        List<Entry> entries = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return entries;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    entries.add(new Entry(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Leaderboard read error: " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }

        Collections.sort(entries, new Comparator<Entry>() {
            public int compare(Entry a, Entry b) {
                return Integer.compare(b.kills, a.kills);
            }
        });

        return entries;
    }

    public static void save(String name, int kills) {
        List<Entry> entries = load();
        entries.add(new Entry(name, kills));

        Collections.sort(entries, new Comparator<Entry>() {
            public int compare(Entry a, Entry b) {
                return Integer.compare(b.kills, a.kills);
            }
        });

        if (entries.size() > MAX_ENTRIES) {
            entries = entries.subList(0, MAX_ENTRIES);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH));
            for (Entry e : entries) {
                writer.write(e.name + "," + e.kills);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Leaderboard write error: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
        }
    }
}