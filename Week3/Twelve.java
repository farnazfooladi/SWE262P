import java.util.*;
import java.io.*;
import java.nio.file.Path;

public class Twelve {
  private static void gettingArgs(String[] args) throws Exception{
    final Path path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
  }

  private static class DataStorageManager {
    private LinkedList<String> words  = new LinkedList<>();
    private Scanner sc; 

    public Object dispatch(String[] message) throws Exception{
      if(message[0].equals("init")){
        return this.init(message[1]);
      }
      else if(message[0] == "words"){
        return this.words();
      }
      else{
        throw new Exception("Message not found");
      }
    }

    private Object init(String path_to_file){
      try{
        File file = new File(path_to_file);
        this.sc = new Scanner(file);
      }
      catch (IOException e){
        System.out.println(e);
      }
      return new Object();
    }

    private LinkedList<String> words(){
      this.words = new LinkedList<>();
      while (sc.hasNextLine()){
        String word;
        word = sc.nextLine().toLowerCase();
        word = word.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = word.split(" ");
        for(int i=0; i<wlist.length; i++){
          words.add(wlist[i]);
        }
      }
      sc.close();
      return words;
    }
  }

  private static class StopWordManager{
    LinkedList stop_list  = new LinkedList<>();

    public Object dispatch(String[] message) throws Exception{
      if(message[0].equals("init")){
        this.init();
      }
      else if(message[0].equals("is_stop_word")){
        return this.is_stop_word(message[1]);
      }
      else{
        throw new Exception("Message not found");
      }
      return new Object();
    }

    private Object init(){
      try{
        File file = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        String word = new String();
        while (sc.hasNextLine()){
          word = sc.nextLine();
          String[] wlist = word.split(",");
          for(int i=0; i<wlist.length; i++){
            this.stop_list.add(wlist[i]);
          }
        }
        sc.close();
      }
      catch (Exception e){
        System.out.println(e);
      }
      return new Object();
    }

    private boolean is_stop_word(String word){
      return this.stop_list.contains(word);
    }
  }

  private static class WordFrequencyManager {
      private HashMap <String, Integer> word_freq  = new HashMap<>();

    public Object dispatch(String[] message) throws Exception{
      if(message[0].equals("increment_count")){
        this.increment_count(message[1]);
      }
      else if(message[0].equals("sorted")){
        return this.sorted();
      }
      else{
        throw new Exception("Message not found");
      }
      return new Object();
    }

    private Object increment_count (String word){
      Integer count = word_freq.get(word);
      if(count == null){
        this.word_freq.put(word,1);
      }
      else{
        this.word_freq.put(word, count+1);
      }
      return new Object();
    }

    private List<Map.Entry<String, Integer>> sorted(){
      List<Map.Entry<String, Integer>> sortedResult;
      sortedResult = new ArrayList<>(word_freq.entrySet());
      sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
      return sortedResult;
    }
  }

  private static class WordFrequencyController {
    private DataStorageManager storage_manager;
    private StopWordManager stop_word_manager;
    private WordFrequencyManager word_freq_manager;

    public void dispatch(String[] message) throws Exception{
      if(message[0].equals("init")){
        this.init(message[1]);
      }
      else if(message[0].equals("run")){
        this.run();
      }
      else{
        throw new Exception("Message not found");
      }
    }

    private void init (String path_to_file) throws Exception{
      this.storage_manager = new DataStorageManager();
      this.stop_word_manager = new StopWordManager();
      this.word_freq_manager = new WordFrequencyManager();
      this.storage_manager.dispatch(new String[]{"init", path_to_file});
      this.stop_word_manager.dispatch(new String [] {"init"});
    }
            
    private void run() throws Exception{
      LinkedList<String> words = (LinkedList<String>)storage_manager.dispatch(new String[]{"words"});
      for( String w : words){
        if (!(boolean)stop_word_manager.dispatch(new String[]{"is_stop_word", w}) && w.length() >=2) {
          word_freq_manager.dispatch(new String[]{"increment_count", w});
        }
      }

      List<Map.Entry<String, Integer>> sortedResult = new ArrayList<>();
      sortedResult = (List<Map.Entry<String, Integer>>)word_freq_manager.dispatch(new String [] {"sorted"});

      for(int i = 0; i < 25; i++) {
        System.out.print(sortedResult.get(i).getKey() + " -> " + sortedResult.get(i).getValue() + "\n");
      } 
    }
  }

  public static void main(String[] args) throws Exception {
    gettingArgs(args);
    WordFrequencyController wfc = new WordFrequencyController();
    wfc.dispatch(new String[] {"init", args[0]});
    wfc.dispatch(new String[]{"run"});
  }
}