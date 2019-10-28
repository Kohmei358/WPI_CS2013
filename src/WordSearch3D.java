import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//TODO: Acsess Type / Remove Static

/**
 * Implements a 3-d word search puzzle program.
 */
public class WordSearch3D {
	public WordSearch3D () {
	}

	/**
	 * Main function for recursivley finding all vectors where words can be found.
	 * @return an arrayList of 26 3D vectors
	 */
	private static ArrayList<int[]> findVectors(){
		ArrayList<int[]> finalList = new ArrayList<int[]>();
		int soFar[] = new int[3];
		int index = -1;
		findVectorHelper(finalList, soFar, index + 1);
		finalList.remove(13); //removing 0,0,0 vector
		return finalList;
	}

	/**
	 * Recursivley find all the possible combinations of vectors where words can be found.
	 * @param finalList arrayList of words that are built up an int arrray at a time
	 * @param soFar array of ints built so far
	 * @param index location of soFar array that should given a value next
	 */
	private static void findVectorHelper(ArrayList<int[]> finalList, int[] soFar, int index) {
		if (index <= 2) {
			soFar[index] = -1;
			findVectorHelper(finalList, soFar, index + 1);
			soFar[index] = 0;
			findVectorHelper(finalList, soFar, index + 1);
			soFar[index] = 1;
			findVectorHelper(finalList, soFar, index + 1);
		}
		else{ //Base Case
			finalList.add(soFar.clone());
		}
	}

	/**
	 * Determines if the given position is in the bounds of the grid.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param currPos the 3 value int array representing the position
	 * @return true if is grid, false if not
	 */
	public static boolean inGrid(char[][][] grid, int[] currPos){
		return (currPos[0] >= 0 && currPos[0] <= grid.length && currPos[1] >= 0 && currPos[1] <= grid[0].length &&
				currPos[2] >= 0 && currPos[2] <= grid[0][0].length);
	}

	/**
	 * Searches for the word based on starting position and a search direction vector.
	 * Continues the search only in the vector direction until word is found or out of grid bounds.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param dirVector the unit vector of the direction to search from
	 * @param currPos the current position where the current search is happening
	 * @param word the word to search for
	 * @return a list of positions where the word is
	 */
	public static int[][] searchHelper(char[][][] grid, int[] dirVector, int currPos[], String word){
		int[][] path = new int[word.length()][3]; //letter path
		for(int unitsMoved = 0; inGrid(grid, currPos); unitsMoved++){
			if(unitsMoved >= word.length()) return path; //word found
			else{
				//in next pos is in grid bounds
				path[unitsMoved] = currPos.clone();
				//if next pos letters do not match
				if(grid[currPos[0]][currPos[1]][currPos[2]] != word.charAt(unitsMoved)) return null;
				//move currPos by vector
				for(int j = 0; j < dirVector.length; j++) currPos[j] += dirVector[j];
			}
		}
		return null;
	}

	/**
	 * Searches for all the words in the specified list in the specified grid.
	 * You should not need to modify this method.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param words the words to search for
	 * @param a list of lists of locations of the letters in the words
	 */
	public int[][][] searchForAll (char[][][] grid, String[] words){
		final int[][][] locations = new int[words.length][][];
		for (int i = 0; i < words.length; i++) {
			locations[i] = search(grid, words[i]);
		}
		return locations;
	}

	/**
	 * Searches for the specified word in the specified grid.
	 * @param grid the grid of characters comprising the word search puzzle
	 * @param word the word to search for
	 * @return If the grid contains the
	 * word, then the method returns a list of the (3-d) locations of its letters; if not, 
	 */
	public int[][] search (char[][][] grid, String word) {
		//Simple Edge Case
		if(word == null) return null;
		if(word == "") return new int[0][0];

		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				for(int k = 0; k < grid[0][0].length; k++){
					if(word.charAt(0) == grid[i][j][k]){ //if grid letter location matches first letter of word
						for(int[] vector : findVectors()){
							int[] firstLetterPos = {i,j,k};
							int[][] path = searchHelper(grid,vector,firstLetterPos,word);
							if(path != null) return path; //if word found from this letter pos
						}
					}
				}
			}
		}
		return null;
	}

	public static void insertWordAt(char[][][] grid, int[] pos, int[] dir, String word){
		for(int i = 0; i < word.length(); i++){
			grid[pos[0]][pos[1]][pos[2]] = word.charAt(i);
			for(int j = 0; j < dir.length; j++) pos[j] += dir[j];
		}
	}

	public static boolean tryPos(char[][][] grid, int[] pos, int[] dir, String word){
		if(grid[pos[0]][pos[1]][pos[2]] != '\u0000') return false;
		for(int i = 0; i < word.length(); i++){
			for(int j = 0; j < dir.length; j++) pos[j] += dir[j];
			if(!inGrid(grid,pos) || (grid[pos[0]][pos[1]][pos[2]] != '\u0000')) return false;
		}
		return true;
	}

	public static char[][][] insertWord(char[][][] grid, String word){
		Random rand = new Random();
		ArrayList<int[]> possibleDirs = findVectors();
		for(int i = 0; i < 1000; i++){
			int[] pos = {rand.nextInt(grid.length),rand.nextInt(grid[0].length),rand.nextInt(grid[0][0].length)};
			int[] dir = possibleDirs.get(rand.nextInt(possibleDirs.size()));
			if(tryPos(grid, pos, dir, word)){
				insertWordAt(grid, pos, dir, word);
				return grid;
			}
		}
		return null;
	}

	public static char[][][] atemptMake(String[] words, int sizeX, int sizeY, int sizeZ){
		char[][][] returnGrid = new char[sizeX][sizeY][sizeZ];
		for(String word: words) {
			returnGrid = insertWord(returnGrid, word);
			if(returnGrid == null) return null;
		}
		return returnGrid;
	}

	/**
	 * Tries to create a word search puzzle of the specified size with the specified
	 * list of words.
	 * @param words the list of words to embed in the grid
	 * @param sizeX size of the grid along first dimension
	 * @param sizeY size of the grid along second dimension
	 * @param sizeZ size of the grid along third dimension
	 * @return a 3-d char array if successful that contains all the words, or <tt>null</tt> if
	 * no satisfying grid could be found.
	 */
	public static char[][][] make (String[] words, int sizeX, int sizeY, int sizeZ) {
		char[][][] returnGrid = null;
		for(int i  = 0; i < 1000; i++){
			returnGrid = atemptMake(words,sizeX,sizeY,sizeZ);
			if(returnGrid != null) return returnGrid;
		}
		return null;
	}

	/**
	 * Exports to a file the list of lists of 3-d coordinates.
	 * You should not need to modify this method.
	 * @param locations a list (for all the words) of lists (for the letters of each word) of 3-d coordinates.
	 * @param filename what to name the exported file.
	 */
	public static void exportLocations (int[][][] locations, String filename) {
		// First determine how many non-null locations we have
		int numLocations = 0;
		for (int i = 0; i < locations.length; i++) {
			if (locations[i] != null) {
				numLocations++;
			}
		}

		try (final PrintWriter pw = new PrintWriter(filename)) {
			pw.print(numLocations);  // number of words
			pw.print('\n');
			for (int i = 0; i < locations.length; i++) {
				if (locations[i] != null) {
					pw.print(locations[i].length);  // number of characters in the word
					pw.print('\n');
					for (int j = 0; j < locations[i].length; j++) {
						for (int k = 0; k < 3; k++) {  // 3-d coordinates
							pw.print(locations[i][j][k]);
							pw.print(' ');
						}
					}
					pw.print('\n');
				}
			}
			pw.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	/**
	 * Exports to a file the contents of a 3-d grid.
	 * You should not need to modify this method.
	 * @param grid a 3-d grid of characters
	 * @param filename what to name the exported file.
	 */
	public static void exportGrid (char[][][] grid, String filename) {
		try (final PrintWriter pw = new PrintWriter(filename)) {
			pw.print(grid.length);  // height
			pw.print(' ');
			pw.print(grid[0].length);  // width
			pw.print(' ');
			pw.print(grid[0][0].length);  // depth
			pw.print('\n');
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[0].length; y++) {
					for (int z = 0; z < grid[0][0].length; z++) {
						pw.print(grid[x][y][z]);
						pw.print(' ');
					}
				}
				pw.print('\n');
			}
			pw.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	/**
	 * Creates a 3-d word search puzzle with some nicely chosen fruits and vegetables,
	 * and then exports the resulting puzzle and its solution to grid.txt and locations.txt
	 * files.
	 */
	public static void main (String[] args) {
		final WordSearch3D wordSearch = new WordSearch3D();
		String[] temp = {"Test","Test2"};
		make(temp,1,2,2);
//		final String[] words = new String[] { "apple", "orange", "pear", "peach", "durian", "lemon", "lime", "jackfruit", "plum", "grape", "apricot", "blueberry", "tangerine", "coconut", "mango", "lychee", "guava", "strawberry", "kiwi", "kumquat", "persimmon", "papaya", "longan", "eggplant", "cucumber", "tomato", "zucchini", "olive", "pea", "pumpkin", "cherry", "date", "nectarine", "breadfruit", "sapodilla", "rowan", "quince", "toyon", "sorb", "medlar" };
//		final int xSize = 10, ySize = 10, zSize = 10;
//		final char[][][] grid = wordSearch.make(words, xSize, ySize, zSize);
//		exportGrid(grid, "grid.txt");
//
//		final int[][][] locations = wordSearch.searchForAll(grid, words);
//		exportLocations(locations, "locations.txt");
	}
}
