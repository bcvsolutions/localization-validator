package BCV_ukoly;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            View.printShortIntroduction();
            String path = args[0];
            fileSearch(path);
            if(!FindFile.getFolderList().isEmpty()) {
                Compare.compareAllFiles();
            }
        } else {
            View.printIntroduction();
            while (true) {
                input();
            }
        }
    }

    private static void input() throws InterruptedException, IOException {
        Scanner reader = new Scanner(System.in, "UTF-8");
        if(FindFile.getFolderList().isEmpty()) {
            pathInput();
        } else if(FindFile.getFolderList().size() == 1) {
            Compare.compareMessages(FindFile.getFolderList().get(0));
            View.printShortSelection();
            switch (reader.nextLine().toLowerCase().trim()) {
                case "new":
                    pathInput();
                    break;
                case "exit:":
                case "e":
                    System.exit(0);
                default:
                    System.out.println("Unknown command!");
                    break;
            }

        } else {
            View.printUserSelection();
            switch(reader.nextLine().toLowerCase().trim()) {
                case "all":
                case "a":
                    Compare.compareAllFiles();
                    break;
                case "select":
                case "s":
                    View.printFolderList();
                    selectionInput();
                    break;
                case "new":
                case "n":
                    pathInput();
                    break;
                case "e":
                    System.exit(0);
                default:
                    System.out.println("Unknown command!");
                    break;
            }
        }
    }

    private static void selectionInput() throws IOException, InterruptedException {
        Scanner reader = new Scanner(System.in, "UTF-8");

        System.out.println("Choose path number: ");
        String input = reader.nextLine();
        int selection;

        try {
            if (input.trim().toLowerCase().equals("exit"))
                System.exit(0);
            selection = Integer.parseInt(input.trim());
        } catch (Exception e) {
            System.out.println("Invalid entry, not a number!");
            return;
        }

        if(selection > FindFile.getFolderList().size() || selection < 1) {
            System.out.println("Invalid entry, number out of range.");
        } else {
            Compare.compareMessages(FindFile.getFolderList().get(selection - 1));
        }
    }

    private static void pathInput() {
        // if the folder list is empty program asks for entering new directory to search for JSON files
        System.out.println("Enter the directory where to search or 'e' to exit: ");
        Scanner scan = new Scanner(System.in);
        String directory = scan.next();

        if(directory.equals("exit") || directory.equals("e"))
            System.exit(0);

        FindFile ff = new FindFile();
        ff.clearFolderList();
        ff.findFile(new File(directory));

        View.printSearchResultStatus();
    }

    private static void fileSearch(String directory) {
        FindFile ff = new FindFile();
        ff.clearFolderList();
        ff.findFile(new File(directory));
    }
}
