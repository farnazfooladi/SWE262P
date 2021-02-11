import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class Seventeen {
    /*
     * The main function
     */
    public static void main(String[] args) throws Exception {
        new WordFrequencyController(args[0]).run();

        System.out.println("Enter a class name: ");
        Scanner sc = new Scanner(System.in); 
        String uInput = sc.nextLine();  // Read user input
        ClassInfo(uInput);
    }

    public static void ClassInfo(String userInp) throws Exception{
        Class cls = null;
        cls = Class.forName(userInp);

        if(cls != null){
            Class[] interfaces = cls.getInterfaces();
            for(Class intface: interfaces){
                System.out.println("Interfaces are:" + intface.getName());
            }

            Field[] fields = cls.getDeclaredFields();
            for (Field f:fields){
                System.out.println("Fields are:" + f.getName());
            }

            Method[] methods = cls.getDeclaredMethods();
            for (Method m:methods){
                System.out.println("Methods are:" + m.getName());
            }
            
            Class superClass = cls.getSuperclass();
            if(superClass != null){
                System.out.println("SuperClass are:" + superClass.getName());
            }
            else{
                System.out.println("Class: " + userInp + " does not have a super class");
            }
        }

    }
}

/*
 * The classes
 */

abstract class TFExercise {
    public String getInfo() {
        return this.getClass().getName();
    }
}

class WordFrequencyController extends TFExercise {
    private DataStorageManager storageManager;
    private StopWordManager stopWordManager;
    private WordFrequencyManager wordFreqManager;

    private Class<DataStorageManager> class_storageManager;
    private Class<StopWordManager> class_stopWordManager;
    private Class<WordFrequencyManager> class_wordFreqManager;
    
    public WordFrequencyController(String pathToFile) throws Exception {
        class_storageManager = (Class<DataStorageManager>) Class.forName("DataStorageManager");
        class_stopWordManager = (Class<StopWordManager>) Class.forName("StopWordManager");
        class_wordFreqManager = (Class<WordFrequencyManager>) Class.forName("WordFrequencyManager");


        storageManager = class_storageManager.getDeclaredConstructor(String.class).newInstance(pathToFile);
        stopWordManager = class_stopWordManager.getDeclaredConstructor().newInstance();
        wordFreqManager = class_wordFreqManager.getDeclaredConstructor().newInstance();


        // this.storageManager = new DataStorageManager(pathToFile);
        // this.stopWordManager = new StopWordManager();
        //this.wordFreqManager = new WordFrequencyManager();

    }
    
    public void run() throws Exception{
        List <String> getWords = (List <String>) class_storageManager.getMethod("getWords").invoke(storageManager);
        Method stop_words = class_stopWordManager.getMethod("isStopWord", String.class);
        Method word_freq = class_wordFreqManager.getMethod("incrementCount", String.class);

        for (String word : (List <String>) getWords) {
            if (!(boolean)stop_words.invoke(stopWordManager, word)) {
                word_freq.invoke(wordFreqManager, word);
            }
        }

        List<WordFrequencyPair> word_freq_sorted = (List<WordFrequencyPair>)class_wordFreqManager.getMethod("sorted").invoke(wordFreqManager);
        

        int numWordsPrinted = 0;
        for (WordFrequencyPair pair : word_freq_sorted) {
            System.out.println(pair.getWord() + " - " + pair.getFrequency());
            
            numWordsPrinted++;
            if (numWordsPrinted >= 25) {
                break;
            }
        }
    }
}

/** Models the contents of the file. */
class DataStorageManager extends TFExercise {
    private List<String> words;
    
    public DataStorageManager(String pathToFile) throws IOException {
        this.words = new ArrayList<String>();
        
        Scanner f = new Scanner(new File(pathToFile), "UTF-8");
        try {
            f.useDelimiter("[\\W_]+");
            while (f.hasNext()) {
                this.words.add(f.next().toLowerCase());
            }
        } finally {
            f.close();
        }
    }
    
    public List<String> getWords() {
        return this.words;
    }
    
    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.words.getClass().getName();
    }
}

/** Models the stop word filter. */
class StopWordManager extends TFExercise {
    private Set<String> stopWords;
    
    public StopWordManager() throws IOException {
        this.stopWords = new HashSet<String>();
        
        Scanner f = new Scanner(new File("../../stop_words.txt"), "UTF-8");
        try {
            f.useDelimiter(",");
            while (f.hasNext()) {
                this.stopWords.add(f.next());
            }
        } finally {
            f.close();
        }
        
        // Add single-letter words
        for (char c = 'a'; c <= 'z'; c++) {
            this.stopWords.add("" + c);
        }
    }
    
    public boolean isStopWord(String word) {
        return this.stopWords.contains(word);
    }
    
    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.stopWords.getClass().getName();
    }
}

/** Keeps the word frequency data. */
class WordFrequencyManager extends TFExercise {
    private Map<String, MutableInteger> wordFreqs;
    
    public WordFrequencyManager() {
        this.wordFreqs = new HashMap<String, MutableInteger>();
    }
    
    public void incrementCount(String word) {
        MutableInteger count = this.wordFreqs.get(word);
        if (count == null) {
            this.wordFreqs.put(word, new MutableInteger(1));
        } else {
            count.setValue(count.getValue() + 1);
        }
    }
    
    public List<WordFrequencyPair> sorted() {
        List<WordFrequencyPair> pairs = new ArrayList<WordFrequencyPair>();
        for (Map.Entry<String, MutableInteger> entry : wordFreqs.entrySet()) {
            pairs.add(new WordFrequencyPair(entry.getKey(), entry.getValue().getValue()));
        }
        Collections.sort(pairs);
        Collections.reverse(pairs);
        return pairs;
    }
    
    public String getInfo() {
        return super.getInfo() + ": My major data structure is a " + this.wordFreqs.getClass().getName();
    }
}

class MutableInteger {
    private int value;
    
    public MutableInteger(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}

class WordFrequencyPair implements Comparable<WordFrequencyPair> {
    private String word;
    private int frequency;
    
    public WordFrequencyPair(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }
    
    public String getWord() {
        return word;
    }
    
    public int getFrequency() {
        return frequency;
    }
    
    public int compareTo(WordFrequencyPair other) {
        return this.frequency - other.frequency;
    }
}