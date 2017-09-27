package BCV_ukoly;

import java.io.File;
import java.util.ArrayList;

/**
 * File searching class
 *
 * @author Petr Hanák
 */
public class FindFile {

    private static final String language1 = "cs";
    private static final String language2 = "en";
    private static final String suffix = ".json";
    private static final String nothingToCompare = "Nothing to compare in this folder";

    private static ArrayList<File> folderList = new ArrayList<>();

    public static ArrayList<File> getFolderList() {
        return folderList;
    }

    public void clearFolderList() {
        folderList.clear();
    }

    /**
     * DFS for searching JSON files in directories
     * @param file
     */
    public void findFile(File file) {

        File[] list = file.listFiles();

        if (list != null)
            for (File fil : list) {
                if (fil.isDirectory()) {
                    findFile(fil);
                } else if (fil.getName().equals(language1 + suffix) || fil.getName().equals(language2 + suffix)) {
                    if (folderList.isEmpty()) {
                        folderList.add(fil.getParentFile());
                    } else if (!folderList.get(folderList.size() - 1).equals(fil.getParentFile())) {
                        folderList.add(fil.getParentFile());
                    }
                }
            }
    }

    /**
     * Simple search for files in specific folder
     * @param folder
     * @return json files
     */
    public static ArrayList<File> findFilesInFolder(File folder) {
        File dir = new File(folder.toString());
        File[] files = dir.listFiles();
        ArrayList<File> jsonFiles = new ArrayList<>();
        boolean foundCS = false;
        boolean foundEN = false;

        for(File file : files) {
           if(file.getName().equals(language1 + suffix)) {
               foundCS = true;
               jsonFiles.add(file);
           } else if(file.getName().equals(language2 + suffix)) {
               foundEN = true;
               jsonFiles.add(file);
           }
       }

       if(foundCS && foundEN) {
           return jsonFiles;
       }
       else if(foundCS) {
           System.out.println(nothingToCompare + " (cs.json file found only).");
           return null;
       } else if(foundEN) {
           System.out.println(nothingToCompare + " (en.json file found only).");
           return null;
       } else {
           System.out.println(nothingToCompare + " (missing files).");
            return null;
       }
    }
}
