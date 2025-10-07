package com.HabtTracker;

import java.io.*;
import java.util.*;

public class HabitEval {
    static class Habit {
        private String name;
        private String description;
        private String frequency;
        private int daysTracked;
        private int completions;

        public Habit(String name, String description, String frequency) {
            this.name = name;
            this.description = description;
            this.frequency = frequency;
            this.daysTracked = 0;
            this.completions = 0;
        }

        public void markCompletion(boolean completed) {
            daysTracked++;
            if (completed) completions++;
        }

        public double calculateStrength() {
            if (daysTracked == 0) return 0;
            return (completions / (double) daysTracked) * 100;
        }

        public String getFeedback() {
            double strength = calculateStrength();
            if (strength >= 80) return "Great job! You're consistently keeping up with your habit.";
            else if (strength >= 50) return "Good job! You're making progress, but there's room for improvement.";
            else return "Keep going! Try to be more consistent in completing your habit.";
        }

        public String toFileString() {
            return name + "," + description + "," + frequency + "," + completions + "," + daysTracked;
        }

        public static Habit fromFileString(String line) {
            String[] parts = line.split(",");
            Habit habit = new Habit(parts[0], parts[1], parts[2]);
            habit.completions = Integer.parseInt(parts[3]);
            habit.daysTracked = Integer.parseInt(parts[4]);
            return habit;
        }

        public String toString() {
            return name + " [" + frequency + "]: " + completions + "/" + daysTracked + " completed (" +
                    String.format("%.2f", calculateStrength()) + "%)";
        }
    }

    private static final String FILE_NAME = "habit_data.txt";
    private static List<Habit> habits = new ArrayList<>();

    public static void main(String[] args) {
        loadHabits();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Habit Strength Calculator ---");
            System.out.println("1. Add New Habit");
            System.out.println("2. Mark Habit Completion");
            System.out.println("3. View Habits and Strength");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addHabit(sc);
                case 2 -> markCompletion(sc);
                case 3 -> viewHabits();
                case 4 -> saveHabits();
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 4);

        sc.close();
    }

    private static void addHabit(Scanner sc) {
        System.out.print("Enter habit name: ");
        String name = sc.nextLine();
        System.out.print("Enter description: ");
        String desc = sc.nextLine();
        System.out.print("Enter frequency (Daily/Weekly): ");
        String freq = sc.nextLine();
        habits.add(new Habit(name, desc, freq));
        System.out.println("Habit added!");
    }

    private static void markCompletion(Scanner sc) {
        if (habits.isEmpty()) {
            System.out.println("No habits found.");
            return;
        }

        for (int i = 0; i < habits.size(); i++) {
            System.out.println((i + 1) + ". " + habits.get(i));
        }

        System.out.print("Enter habit number to update: ");
        int index = sc.nextInt() - 1;
        sc.nextLine();

        if (index < 0 || index >= habits.size()) {
            System.out.println("Invalid habit number.");
            return;
        }

        System.out.print("Did you complete the habit today? (yes/no): ");
        String response = sc.nextLine().trim().toLowerCase();
        habits.get(index).markCompletion(response.equals("yes"));
        System.out.println("Completion updated!");
    }

    private static void viewHabits() {
        if (habits.isEmpty()) {
            System.out.println("No habits to show.");
            return;
        }

        for (Habit h : habits) {
            System.out.println(h);
            System.out.println("Feedback: " + h.getFeedback());
            System.out.println("----------------------------------");
        }
    }

    private static void saveHabits() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Habit h : habits) {
                bw.write(h.toFileString());
                bw.newLine();
            }
            System.out.println("Data saved. Goodbye!");
        } catch (IOException e) {
            System.out.println("Error saving habits: " + e.getMessage());
        }
    }

    private static void loadHabits() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                habits.add(Habit.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading habits: " + e.getMessage());
        }
    }
}
