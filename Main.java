import java.io.*;
import java.util.*;

/**
 * Reads a Slay the Spire deck from a text file and generates
 * information about the deck's total energy cost.
 */
public class Main {

    /**
     * Input: Command line execution.
     * Output: Deck analysis and report.
     * Steps: Gets a filename from the user, reads the deck,
     * validates the cards, and generates a report.
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter deck file name: ");
        String fileName = input.nextLine();

        int deckId = generateDeckId();

        int totalCost = 0;
        int totalCards = 0;
        int invalidCount = 0;

        int[] histogram = new int[7];
        ArrayList<String> invalidCards = new ArrayList<>();

        try {
            Scanner file = new Scanner(new File(fileName));

            while (file.hasNextLine()) {
                String line = file.nextLine();
                totalCards++;

                // More than 1000 cards automatically creates a void report.
                if (totalCards > 1000) {
                    continue;
                }

                String[] parts = line.split(":", -1);

                if (parts.length != 2) {
                    invalidCards.add(line);
                    invalidCount++;
                    continue;
                }

                String cardName = parts[0].trim();
                String costText = parts[1].trim();

                if (cardName.isEmpty()) {
                    invalidCards.add(line);
                    invalidCount++;
                    continue;
                }

                try {
                    int cost = Integer.parseInt(costText);

                    if (cost < 0 || cost > 6) {
                        invalidCards.add(line);
                        invalidCount++;
                        continue;
                    }

                    totalCost += cost;
                    histogram[cost]++;

                } catch (NumberFormatException e) {
                    invalidCards.add(line);
                    invalidCount++;
                }
            }

            file.close();

            boolean isVoid = invalidCount > 10 || totalCards > 1000;

            createReport(
                deckId,
                totalCost,
                histogram,
                invalidCards,
                isVoid
            );

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
        }

        input.close();
    }

    /**
     * Input: None.
     * Output: A random 9-digit integer.
     * Steps: Generates a number between 100000000 and 999999999.
     */
    public static int generateDeckId() {
        Random random = new Random();
        return 100000000 + random.nextInt(900000000);
    }

    /**
     * Input: Deck ID, total cost, histogram, invalid cards, and void status.
     * Output: A report file containing the deck analysis.
     * Steps: Creates either a normal report or a VOID report.
     */
    public static void createReport(
            int deckId,
            int totalCost,
            int[] histogram,
            ArrayList<String> invalidCards,
            boolean isVoid) {

        String fileName;

        if (isVoid) {
            fileName = "SpireDeck_" + deckId + "(VOID).txt";
        } else {
            fileName = "SpireDeck_" + deckId + ".txt";
        }

        try {
            PrintWriter writer = new PrintWriter(fileName);

            if (isVoid) {
                writer.println("VOID");
            } else {
                writer.println("SLAY THE SPIRE DECK REPORT");
                writer.println();
                writer.println("Deck ID: " + deckId);
                writer.println("Total Cost: " + totalCost + " energy");
                writer.println();
                writer.println("Energy Cost Histogram:");

                for (int i = 0; i < histogram.length; i++) {
                    writer.print(i + " energy: ");

                    for (int j = 0; j < histogram[i]; j++) {
                        writer.print("*");
                    }

                    writer.println(" (" + histogram[i] + ")");
                }

                writer.println();
                writer.println("Invalid Cards:");

                if (invalidCards.isEmpty()) {
                    writer.println("None");
                } else {
                    for (String card : invalidCards) {
                        writer.println(card);
                    }
                }
            }

            writer.close();

            System.out.println("Report created: " + fileName);

        } catch (FileNotFoundException e) {
            System.out.println("Error creating report.");
        }
    }
}
