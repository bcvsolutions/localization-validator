package BCV_ukoly;

import java.io.File;

/**
 * Printing text to the user
 *
 * @author Petr Hanák
 *
 */
public class View {

    public static void printIntroduction() {
        System.out.println("\n  ------------- JSON DEFECT FINDER -------------\n");
    }

    public static void printUserSelection() {

        System.out.println("----------------------------------------------");
        System.out.println("all -> find defects in all folders");
        System.out.println("select -> compare messages in selected folder");
        System.out.println("new -> new search");
        System.out.println("e -> exit program");
        System.out.println("----------------------------------------------");
        System.out.print("Enter command: ");
    }

    public static void printShortSelection() {

        System.out.println("----------------------------------------------");
        System.out.println("new -> new search");
        System.out.println("e -> exit program");
        System.out.println("----------------------------------------------");
        System.out.print("Choose command: ");
    }

    public static void printSearchResultStatus() {
        if(FindFile.getFolderList().isEmpty())
            System.out.println("JSON files not found!");
        else if(FindFile.getFolderList().size() == 1)
            System.out.println("Just one folder with comparable files found!");
        else
            System.out.println("More than one folder with comparable files found!");
    }

    public static void printDefects() {

        if(Messages.getDefects().size() > 0) {
            System.out.println("------------------ DEFECTS: ------------------");
            for (int i = 0; i < Messages.getDefects().size(); i++) {
                System.out.println(Messages.getDefects().get(i));
            }
            System.out.println("==============================================");
        } else {
            System.out.println("----------------------------------------------");
            System.out.println("NO DEFECTS FOUND!");
            System.out.println("==============================================");
        }
    }

    public static void printFolderList() {
        int count = 1;

        if(!FindFile.getFolderList().isEmpty()) {
            for (File path : FindFile.getFolderList()) {
                System.out.print(count++ + " ");
                System.out.println(path);
            }
        }
    }
}
