package BCV_ukoly;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.*;
import java.util.ArrayList;

/**
 * Compares messages in different languages and adds defects to ArrayList
 *
 * @author Petr Hanák
 */

public class Compare {

    /**
     * Parses JSON files and add messages to ArrayLists
     * @param file
     * @throws IOException
     */
    public static void parseJson(File file) throws IOException {
        Messages messages = new Messages();
        Messages.addLanguage(messages);

        InputStream is = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(is);
        JsonReader reader = new JsonReader(isr);

        JsonToken token = reader.peek();

        try {
            while (token != JsonToken.END_DOCUMENT) {

                token = reader.peek();

                switch (token) {
                    case BEGIN_OBJECT:
                        reader.beginObject();
                        break;
                    case NAME:
                        reader.nextName();
                        messages.addMessage(reader.getPath().substring(2, reader.getPath().length()));
                        break;
                    case STRING:
                        reader.nextString();
                        break;
                    case END_OBJECT:
                        reader.endObject();
                        break;
                    case NULL:
                        break;
                    case END_DOCUMENT:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("\nJSON Parsing error: ");
            System.out.println(reader.toString());
            System.out.println();
        }

    }

    /**
     * Takes one folder after another and calling compare method on them
     * @throws IOException
     * @throws InterruptedException
     */
    public static void compareAllFiles() throws IOException, InterruptedException {

        for(int i = 0; i < FindFile.getFolderList().size(); i++) {
            Compare.compareMessages(FindFile.getFolderList().get(i));
        }
    }

    /**
     * Compares ArrayLists with messages, taken from JSON files
     * @param dir
     * @throws InterruptedException
     * @throws IOException
     */
    public static void compareMessages(File dir) throws InterruptedException, IOException {

        if(FindFile.findFilesInFolder(dir) != null) {
            Messages.clear();
            System.out.println("\nCompared: ");
            for (File file : FindFile.findFilesInFolder(dir)) {
                parseJson(file);
                System.out.println(file.getAbsoluteFile());
            }
        } else
            return;

        ArrayList<String> language1 = new ArrayList<>(Messages.getLanguages().get(0).getMessages());
        ArrayList<String> language2 = new ArrayList<>(Messages.getLanguages().get(1).getMessages());

        int i;
        int j = 0;
        for (i = 0; i + j < language1.size() && i < language2.size(); i++) {
            // System.out.println((i+j) + " " + i + " " + language1.get(i + j) + " vs. " + language2.get(i));

            // if the messages are different, check if it's not about different plural values
            if (!language1.get(i + j).equals(language2.get(i))) {

                if (language1.get(i + j).startsWith(language2.get(i))) {
                    int[] iterators = comparePlural(i, j, language1, language2);
                    i = iterators[0];
                    j = iterators[1];

                } else if(language2.get(i).endsWith("plural")) {

                    // check if there is singular in EN
                    if(language2.get(i).startsWith(language2.get(i - 1))) {
                        // has got singular in EN
                        if(language1.get(i + j).startsWith(language2.get(i - 1))) {
                            Messages.addDefect("CS: " + language1.get(i + j - 1) + " -> defining plural in CS without suffix");
                            i -= 2;
                            j++;
                        }
                    } else {
                        // EN plural hasn't got singular
                        Messages.addDefect("EN: " + language2.get(i) + " -> missing singular word in EN");
                        // check if there is plural in CS, matching EN plural ----------------------------------
                        int[] iterators = comparePlural(i, j, language1, language2);
                        i = iterators[0];
                        j = iterators[1];
                    }

                } else if (language2.get(i).endsWith("0")) {
                    // check if the base of the words is the same (means numerical serie)
					if(language1.get(i + j).startsWith(language2.get(i).substring(0, language2.get(i + j).length() - 2))) {
						Messages.addDefect("EN: " + language2.get(i) + " -> missing suffix '0' in CS");
						i++;
						j--;
						int[] iterators = comparePlural(i, j, language1, language2);
						i = iterators[0];
						j = iterators[1];
					}

					// try to find rest of the numerical serie in CS
					else if(findCSWord(i, j, language1, language2.get(i)) != null) {
						// if the matching numerical serie in CS is found
						int[] iterators = findCSWord(i, j, language1, language2.get(i));
						i = iterators[0];
						j = iterators[1];

					} else {
						// if the matching numerical serie not found
						Messages.addDefect("EN: " + language2.get(i) + " -> missing suffix '0' in CS");
					}

					// check if it has more than one uppercase letters
                } else if (Character.isUpperCase(language1.get(i + j).codePointAt(0)) &&
                        Character.isUpperCase(language1.get(i + j).codePointAt(1)) &&
                        Character.isUpperCase(language2.get(i).codePointAt(0)) &&
                        Character.isUpperCase(language2.get(i).codePointAt(1))) {

                        int [] iterators = findClosestMatch(i, j, language1, language2);
                        i = iterators[0];
                        j = iterators[1];

                } else {

                    if (language1.size() > i + j + 1 && language2.size() > i + 1) {
                        // check if next pair is OK, than it could be typing error
                        if (language1.get(i + j + 1).equals(language2.get(i + 1))) {
                            Messages.addDefect(language1.get(i + j) + " vs. " + language2.get(i) + " -> mismatch in both languages");
                        } else {
                            // search for next nearest match
                            int[] iterators = findClosestMatch(i, j, language1, language2);
                            i = iterators[0];
                            j = iterators[1];
                        }
                    } else {
                        Messages.addDefect(language1.get(i + j) + " vs. " + language2.get(i) + " -> match not found for both words");
                    }
                }
            }
        }

        // check for extra words when one of the msg list reached the end
        if(i + j < language1.size()) {
            for(;i + j < language1.size(); j++) {
                Messages.addDefect("CS: " + language1.get(i + j) + " -> extra word");
            }
        } else if(i < language2.size()) {
            for(;i < language2.size(); i++) {
                Messages.addDefect("EN: " + language2.get(i) + " -> extra word");
            }
        }

        View.printDefects();
    }

    /**
     * Compares messages which describes quantity
     * @param i
     * @param j
     * @param language1
     * @param language2
     * @return
     */
    private static int[] comparePlural(int i, int j, ArrayList<String> language1, ArrayList<String> language2) {
        boolean foundCS_1 = false;
        boolean foundCS_2 = false;
        boolean foundCS_5 = false;

        try {
            for(int k = 0; k < 10; k++) {
                int number = Integer.parseInt(String.valueOf(language1.get(i + j).charAt(language1.get(i + j).length() - 1)));

                if(number == 0) {
                    Messages.addDefect("CS: " + language1.get(i + j) + " -> mising suffix '0' in EN");
                    j++;
                } else if(number == 1 && language2.get(i).endsWith("plural")) {
                    foundCS_1 = true;
                    Messages.addDefect("CS: " + language1.get(i + j) + " -> mising EN singular");
                    j++;
                } else if(number == 1) {
                    foundCS_1 = true;
                    i++;
                } else if(number == 2 && language2.get(i).endsWith("plural")) {
                    j++;
                    foundCS_2 = true;
                } else if(number == 5 && language2.get(i).endsWith("plural")) {
                    foundCS_5 = true;
                    break;
                } else if(number == 2 || number == 5) {
                    if(number == 2)
                        foundCS_2 = true;
                    else foundCS_5 = true;

                    if(language2.get(i + 1).endsWith("plural")) {
                        Messages.addDefect("EN: " + language2.get(i) + " -> missing singular number in CS");
                        i++;
                    } else {
                        if(language2.get(i).startsWith(language2.get(i - 1))) {
                            Messages.addDefect(language1.get(i + j) + " vs. " + language2.get(i) +
                                    " -> missing singular number in CS and plural in EN");
                            i++;
                        } else {
                            Messages.addDefect(language1.get(i + j) + " vs. " + language2.get(i) +
                                    " -> missing plural in EN");
                            j++;
                        }
                    }
                } else {
                    Messages.addDefect("CS: " + language1.get(i + j) + " -> invalid number");
                }
                 // System.out.println(language1.get(i + j) + " vs. " + language2.get(i) + " ");
            }
        } catch (Exception e) {
            if(language2.get(i).endsWith("plural")) {
                Messages.addDefect("CS: " + language1.get(i + j - 1) + " -> missing amount number in CS");
                j--;
            } else if(foundCS_2)
                Messages.addDefect("CS: " + language1.get(i + j - 1)
						+ " -> please check numerical serie for possible missing numbers");
            return new int[] {i, j};
        }

        // control of Czech numbers, defining quantity
        if(foundCS_1 || foundCS_2 || foundCS_5) {
            if (!foundCS_1)
                Messages.addDefect("CS: " + language1.get(i + j) + " -> missing number '1' in CS");
            if (!foundCS_2)
                Messages.addDefect("CS: " + language1.get(i + j) + " -> missing number '2' in CS");
            if (!foundCS_5)
                Messages.addDefect("CS: " + language1.get(i + j) + " -> missing number '5' in CS");
        }

        return new int[] {i, j};
    }

	/**
	 * Tries to find nearest possible match or any possible match
	 * @param i
	 * @param j
	 * @param language1
	 * @param language2
	 * @return
	 */
	private static int[] findClosestMatch(int i, int j, ArrayList<String> language1, ArrayList<String> language2) {
		int count1 = 0;
		int count2 = 0;
		boolean found1 = false;
		boolean found2 = false;

		for (int k = i + j + 1; k < language1.size(); k++) {
			count1++;
			if (language1.get(k).equals(language2.get(i))) {
				found1 = true;
				break;
			}
		}
		for (int k = i + 1; k < language2.size(); k++) {
			count2++;
			if (language2.get(k).equals(language1.get(i + j))) {
				found2 = true;
				break;
			}
		}

		if (count1 <= count2 && found1) {
			for (int k = i + j; k < i + j + count1 ; k++) {
				Messages.addDefect("CS: " + language1.get(k) + " -> extra word");
			}
			i--;
			j += count1;
		} else if (count2 < count1 && found2) {
			for (int k = i; k < i + count2 ; k++) {
				Messages.addDefect("EN: " + language2.get(k) + " -> extra word");
			}
			i += count2 - 1;
			j -= count2;
		} else if (found1) {
			for (int k = i + j; k < i + j + count1 ; k++) {
				Messages.addDefect("CS: " + language1.get(k) + " -> extra word");
			}
			i--;
			j += count1;
		} else if (found2) {
			for (int k = i; k < i + count2 ; k++) {
				Messages.addDefect("EN: " + language2.get(k) + " -> extra word");
			}
			i += count2 - 1;
			j -= count2;
		} else {
			Messages.addDefect(language1.get(i + j) + " vs. " + language2.get(i) + " -> match not found for both words");
		}

		return new int[] {i, j};
	}

	private static int[] findCSWord(int i, int j, ArrayList<String> language1, String wordEN) {
    	boolean found = false;
		int count = 0;

    	for(int k = i + j; i < language1.size(); k++) {
    		count++;
			if (language1.get(k).startsWith(wordEN.substring(0, wordEN.length() - 2))) {
				found = true;
				break;
			}
		}

		if (found) {
			for (int k = i + j; k < i + j + count; k++) {
				Messages.addDefect("CS: " + language1.get(k) + " -> extra word");
			}
			i--;
			j += count;
		} else {
			return null;
		}

    	return new int[] {i, j};
	}
}
