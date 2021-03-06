package howard.west.cs276.assignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Query {

	// Term id -> position in index file
	private static Map<Integer, Long> posDict = new TreeMap<Integer, Long>();
	// Term id -> document frequency
	private static Map<Integer, Integer> freqDict = new TreeMap<Integer, Integer>();
	// Doc id -> doc name dictionary
	private static Map<Integer, String> docDict = new TreeMap<Integer, String>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict = new TreeMap<String, Integer>();
	// Index
	private static BaseIndex index = null;


	private static PostingList readPosting(FileChannel fc, int termId)
			throws IOException {
		fc.position(posDict.get(termId));
		return index.readPosting(fc);
	}

	public static List<String> mainQuery(String input, String query) {

	    try {

	        String[] queryTokens = query.split(" ");
                index = new BasicIndex();

		/* Get index directory */
		File inputdir = new File(input);
		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.err.println("Invalid index directory: " + input);
			return null;
		}

		/* Index file */
		RandomAccessFile indexFile = new RandomAccessFile(new File(input,
				"corpus.index"), "r");

		String line = null;
		/* Term dictionary */
		BufferedReader termReader = new BufferedReader(new FileReader(new File(
				input, "term.dict")));
		while ((line = termReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			termDict.put(tokens[0], Integer.parseInt(tokens[1]));
		}
		termReader.close();

		/* Doc dictionary */
		BufferedReader docReader = new BufferedReader(new FileReader(new File(
				input, "doc.dict")));
		while ((line = docReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			docDict.put(Integer.parseInt(tokens[1]), tokens[0]);
		}
		docReader.close();

		/* Posting dictionary */
		BufferedReader postReader = new BufferedReader(new FileReader(new File(
				input, "posting.dict")));
		while ((line = postReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			posDict.put(Integer.parseInt(tokens[0]), Long.parseLong(tokens[1]));
			freqDict.put(Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[2]));
		}
		postReader.close();
		FileChannel indexChannel = indexFile.getChannel();

		    // Fetch all the posting lists from the index.
		    List<PostingList> postingLists = new ArrayList<PostingList>();
		    boolean noResults = false;
		    for (String queryToken : queryTokens) {
			// Get the term id for this token using the termDict map.
			Integer termId = termDict.get(queryToken);
			if (termId == null) {
			    noResults = true;
			    continue;
			}
			else postingLists.add(readPosting(indexChannel, termId));
		    }

				List<Integer> related_doc_ids = postingLists.get(0).getList();
				for (int i = 1; i < postingLists.size(); i++){
					related_doc_ids = getIntersection(related_doc_ids, postingLists.get(i).getList());
				}

				List<String> related_doc_links = new ArrayList<String>();

				for (Integer doc_id : related_doc_ids) {
					related_doc_links.add(docDict.get(doc_id));
				}


		indexFile.close();
		return related_doc_links;

		} catch (Exception e) { System.out.println("ERROR " + e); }

		return null;
	}

	private static List<Integer> getIntersection(List<Integer> first, List<Integer> second){
		List<Integer> result = new ArrayList<Integer>();
		int firstIndex = 0, secondIndex = 0;

		while (first_index < first.size() && second_index < second.size()){

			if (first.get(first_index) < second.get(second_index)){
				first_index++;
			}

			else if (second.get(second_index) < first.get(first_index)){
				second_index++;
			}

			else {
				result.add(first.get(first_index));
				first_index++;
				second_index++;
			}
		}

		return result;
	}

}
