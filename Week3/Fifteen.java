import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Fifteen {
  public static void main(String[] args) throws Exception{
    WordFrequencyFramework wfapp = new WordFrequencyFramework();
    StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
    DataStorage data_storage = new DataStorage(wfapp,stop_word_filter);
    WordFrequencyCounter word_freq_counter = new WordFrequencyCounter(wfapp, data_storage);
    
    WordsContainingZ z_words = new WordsContainingZ(wfapp, data_storage);
    wfapp.run(args[0]);

  }

  private static class WordFrequencyFramework{
    private static ArrayList<Consumer<String>> load_event_handlers = new ArrayList<>();
    private static ArrayList<Runnable> dowork_event_handlers = new ArrayList<>();
    private static ArrayList<Runnable> end_event_handlers = new ArrayList<>();

    public void register_for_load_event(Consumer<String> handler){
      load_event_handlers.add(handler);
    }

    public void register_for_dowork_event(Runnable handler){
      dowork_event_handlers.add(handler);
    }

    public void register_for_end_event(Runnable handler){
      end_event_handlers.add(handler);
    }

    public void run(String path) throws Exception{
      for(int i = 0; i < load_event_handlers.size(); i++){
        ((Consumer<String>)load_event_handlers.get(i)).accept(path);
      }

      for(int j = 0; j < dowork_event_handlers.size(); j++){
        ((Runnable)dowork_event_handlers.get(j)).run();

      }

      for(int k = 0; k < end_event_handlers.size(); k++){
        ((Runnable)end_event_handlers.get(k)).run();
      }
    }
  }

  private static class StopWordFilter{
    LinkedList stop_list  = new LinkedList<>();

    StopWordFilter(WordFrequencyFramework wfapp){
      wfapp.register_for_load_event(this::load);
    }

    private void load(String ignore) {
      try{
        File file = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        String word = new String();
        while (sc.hasNextLine()){
          word = sc.nextLine();
          String[] wlist = word.split(",");
          for(int i=0; i<wlist.length; i++){
            stop_list.add(wlist[i]);
          }
        }
        sc.close();
      }
      catch (Exception e){
        System.out.println(e);
      }
    }

    private boolean is_stop_word(String word){
      return stop_list.contains(word);
    }
  }

  private static class DataStorage{
    private LinkedList<String> words  = new LinkedList<>();
    private StopWordFilter swfilter;
    private ArrayList<Object> word_event_handlers = new ArrayList<>();
    private Scanner sc;

    DataStorage(WordFrequencyFramework wfapp, StopWordFilter swf){
      this.swfilter = swf;
      wfapp.register_for_load_event(this::load);
      wfapp.register_for_dowork_event(this::produce_words);
    }

    private void load(String path){
      try{
        File file = new File(path);
        sc = new Scanner(file);
      }
      catch (IOException e){
        System.out.println(e);
      }
    } 

    private void produce_words(){
      String word = new String();
      while (this.sc.hasNextLine()){
        word = this.sc.nextLine().toLowerCase();
        word = word.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = word.split(" ");
        for(int i=0; i<wlist.length; i++){
          if(!this.swfilter.is_stop_word(wlist[i]) && wlist[i].length() >= 2){
            for(int j=0; j<this.word_event_handlers.size(); j++){
              ((Consumer<String>)word_event_handlers.get(j)).accept(wlist[i]);
            }
          }
        }
      }
    }

    private void register_for_word_event(Consumer<String> handler){
      this.word_event_handlers.add(handler);
    }
  }

  private static class WordFrequencyCounter{ 
    private HashMap <String, Integer> word_freq  = new HashMap<>();

    WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage DS){
      DS.register_for_word_event(this::inc_count);
      wfapp.register_for_end_event(this::print_freqs);
    }

    private void inc_count(String word){
      Integer count = word_freq.get(word);
      if(count == null){
        word_freq.put(word,1);
      }
      else{
        word_freq.put(word, count+1);
      }
    }

    private List<Map.Entry<String, Integer>> sortedResult(){
      List<Map.Entry<String, Integer>> sortedResult;
      sortedResult = new ArrayList<>(word_freq.entrySet());
      sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
      return sortedResult;
    }

    private void print_freqs(){
      List<Map.Entry<String, Integer>> sortedResult = this.sortedResult() ;
      for(int i = 0; i < 25; i++) {
        System.out.print(sortedResult.get(i).getKey() + " -> " + sortedResult.get(i).getValue() + "\n");
      }
    }
  }

  private static class WordsContainingZ{
    private ArrayList <String> z_words  = new ArrayList<>();

    WordsContainingZ(WordFrequencyFramework wfapp, DataStorage DS){
      DS.register_for_word_event(this::count_z);
      wfapp.register_for_end_event(this::print_z_word);
    }

    private void count_z(String word){
      if(word.contains("z")){
        z_words.add(word);
      }
    }

    private void print_z_word(){
      System.out.println("Words containing letter z including duplicate words: " + z_words.size());
    }
  }
}

  