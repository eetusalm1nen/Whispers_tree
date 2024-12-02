package fi.utu.tech.telephonegame;

import java.util.*;

import fi.utu.tech.telephonegame.util.Words;
import org.apache.commons.text.similarity.LevenshteinDistance;


public class Refiner {

	/*
		The default implementation of Refiner uses word lists defined in fi.utu.tech.telephonegame.util.Words
		You can create your own lists either here or in the Words class.
	 */
	/*
	private static final String[] subjects = (
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit Aliquam laoreet vitae lectus id vehicula ")
					.split(" ");

	private static final String[] predicates = (
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit Aliquam laoreet vitae lectus id vehicula ")
			.split(" ");

	private static final String[] objects = (
			"Lorem ipsum dolor sit amet, consectetur adipiscing elit Aliquam laoreet vitae lectus id vehicula ")
			.split(" ");
	 */
	private static final String[] conjunctions  = (
			"että, jotta, koska, kun, jos, vaikka, kunnes, mikäli, ja")
			.split(",");

	/*
		If you decide to use the lists above, comment out the following three lines.
	 */
	private static final String[] subjects = Words.subjects;
	private static final String[] predicates = Words.predicates;
	private static final String[] objects = Words.subjects;

	private static final Random rnd = new Random();


	/*
	 * The refineText method is used to change the message
	 * Now it is time invent something fun!
	 *
	 * In the example implementation a random work from a word list is added to the end of the message.
	 * But you do you!
	 *
	 * Please keep the message readable. No ROT13 etc, please
	 *
	 */
	public static String refineText(String inText) {
		String outText = inText;


		// Change the content of the message here.
//		outText = outText + " " +
//				conjunctions[rnd.nextInt(conjunctions.length)] + " " +
//				subjects[rnd.nextInt(subjects.length)] + " " +
//				predicates[rnd.nextInt(predicates.length)]+ " " +
//				objects[rnd.nextInt(objects.length)];

		outText = closestWord(inText);
		return outText;
	}

	/**
	 * The closestWord method is used to change the given string
	 *
	 * When you provide a string, it changes the word or half of the words
	 * in a sentence by random, using levenshteinDistance algorithm.
	 *
	 * @param text What string should we change?
	 */
	public static String closestWord(String text) {
		String[] allWords = text.split(" ");
		// Defining in what indexes should we change the word/words
		int changeWordCount = (int) Math.ceil((double) allWords.length / 2);
		List<Integer> indexList = new ArrayList<>();
		for (int i = 0; i < changeWordCount; i++) {
			int randomIndex = rnd.nextInt(allWords.length);
			while (indexList.contains(randomIndex)) {
				randomIndex = rnd.nextInt(allWords.length);
			}
			indexList.add(randomIndex);
		}

		// Initializing some objects and lists
		List<String> closestList = new ArrayList<>();
		LevenshteinDistance levenshtein = new LevenshteinDistance();
		List<String[]> allLists = new ArrayList<>();
		allLists.add(subjects);
		allLists.add(predicates);
		List<String> closestWords = new ArrayList<>();

		// Changing the words in the given string
		for (int i = 0; i < allWords.length; i++) {
			// Ignoring words that we do not want to change
			if (!indexList.contains(i)) {
				closestList.add(allWords[i]);
				continue;
			// Returning empty string for an empty string
			} else if (Objects.equals(allWords[i], "")) {
				closestList.add(allWords[i]);
				continue;
			}

			String originalWord = allWords[i];
			int closestDifference = Integer.MAX_VALUE;
			// Go through all lists of words for the closest match
			for (String[] list : allLists) {
				for (String value : list) {
					value = value.replaceAll(" ", "");
					int difference = levenshtein.apply(originalWord, value);
					if (!Objects.equals(originalWord, value) && difference <= closestDifference) {
						if (closestDifference > difference) {
							closestWords.clear();
						}
						closestDifference = difference;
						closestWords.add(value);
					}
				}
			}

			// Choosing by random which word should replace the current word
			int randomIndex = rnd.nextInt(closestWords.size());
			closestList.add(closestWords.get(randomIndex));
			closestWords.clear();
		}

		String changedString = String.join(" ", closestList);
//		System.out.println(closestList);
//		System.out.println(Arrays.toString(allWords));
//		System.out.println(changedString);
		return changedString;
	}


}
