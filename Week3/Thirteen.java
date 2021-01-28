import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Thirteen {

  private static void gettingArgs(String[] args) throws Exception{
    final Path path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
  }

  private static void readfreq(HashMap<String, Object> obj, Path path){
    LinkedList<String> words  = new LinkedList<>();
    try{
      File file = new File(path.toString());
      Scanner sc = new Scanner(file);
      String world = new String();
      while (sc.hasNextLine()){
        world = sc.nextLine().toLowerCase();
        world = world.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = world.split(" ");
        for(int i=0; i<wlist.length; i++){
          words.add(wlist[i]);
        }
      }
      sc.close();
    }
    catch (Exception e){
        System.out.println(e);
    }
    obj.put("data", words);
    
  }

  private static void increment_count(HashMap<String, Object> obj, String words){
    Integer count = ((HashMap<String, Integer>) obj.get("freqs")).get(words);
    if(count == null){
      ((HashMap<String, Integer>) obj.get("freqs")).put(words,1);
    }
    else{
      ((HashMap<String, Integer>) obj.get("freqs")).put(words, count+1);
    }
  }

  private static void load_stop_words(HashMap<String, Object> obj){
    LinkedList<String> stop_list  = new LinkedList<>();
    try{
      File file = new File("../stop_words.txt");
      Scanner sc = new Scanner(file);
      String world = new String();
      while (sc.hasNextLine()){
        world = sc.nextLine();
        String[] wlist = world.split(",");
        for(int i=0; i<wlist.length; i++){
          stop_list.add(wlist[i]);
        }
      }
      sc.close();
    }
    catch (Exception e){
        System.out.println(e);
    }
    obj.put("stop_words", stop_list);
  }

  // https://stackoverflow.com/questions/2943556/static-block-in-java
  //https://medium.com/swlh/understanding-java-8s-consumer-supplier-predicate-and-function-c1889b9423d

  private static final HashMap<String, Object> data_storage = new HashMap<>();
  static{
    data_storage.put("data", new LinkedList<>());
    data_storage.put("init", (Consumer <Path>) path -> readfreq(data_storage, path));
    data_storage.put("words", (Supplier<LinkedList<String>>) () -> (LinkedList<String>)data_storage.get("data"));
  }

  private static final HashMap<String, Object> stop_words = new HashMap<>();
  static{
    stop_words.put("stop_words", new LinkedList<>());
    stop_words.put("init", (Runnable) () -> load_stop_words(stop_words));
    stop_words.put("is_stop_word", (Function<String, Boolean>) w_exicts -> ((LinkedList<String>)stop_words.get("stop_words")).contains(w_exicts));
  }

  private static final HashMap<String, Object> words_freq = new HashMap<>();
  static{
    words_freq.put("freqs", new HashMap<String, Integer>());
    words_freq.put("increment_count", (Consumer <String>) w -> increment_count(words_freq, w));
  }

  public static void main(String[] args) throws Exception {
    gettingArgs(args);
    ((Consumer)data_storage.get("init")).accept(Path.of(args[0]));
    ((Runnable)stop_words.get("init")).run();

    LinkedList<String> temp = (LinkedList<String>)((Supplier)data_storage.get("words")).get();
    LinkedList<String> temp2 = (LinkedList<String>)stop_words.get("stop_words");

    for(String w: temp){
      if (!((Function<String, Boolean>)stop_words.get("is_stop_word")).apply(w) & w.length() >= 2) {
          ((Consumer<String>) words_freq.get("increment_count")).accept(w);
        }
    }

    words_freq.put("sorted", (Runnable) () -> {
      List<Map.Entry<String, Integer>> sortedResult;
      sortedResult = new ArrayList<>(
                    ((HashMap<String, Integer>) words_freq.get("freqs")).entrySet());
      sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));

      for(int i = 0; i < 25; i++) {   
        System.out.print(sortedResult.get(i).getKey() + " -> " + sortedResult.get(i).getValue() + "\n");
      }
    });
  
    ((Runnable)words_freq.get("sorted")).run();
  }
}