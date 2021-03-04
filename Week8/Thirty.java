import java.io.File;
import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Thirty{
  private static LinkedList<String> stop_list = new LinkedList<>();
  private static BlockingQueue<String> word_space = new LinkedBlockingQueue<>();
  private static BlockingQueue<LinkedHashMap<String, Integer>> freq_space = new LinkedBlockingQueue<>();

  private static Path gettingArgs(String[] args){
    final Path path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
    return path;
  }

  private static void GettingStopWords(){
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

    catch (IOException e){
      System.out.println(e);
    } 
  }

  private static Runnable process_words = () -> {
    LinkedHashMap<String, Integer> word_freqs = new LinkedHashMap<>();

    while (word_space.isEmpty() == false) {
      String word = "";
      try {
        word = word_space.poll(1, TimeUnit.SECONDS);
      } 
      catch (Exception e) {
        System.out.println(e);
        break;
      }
      if (stop_list.contains(word) == false) {
        if (word_freqs.containsKey(word)) {
          word_freqs.put(word, word_freqs.get(word) + 1);
        } 
        else {
          word_freqs.put(word, 1);
        }
      }
    }

    try{
      freq_space.put(word_freqs);
    }
    catch (InterruptedException e) {
       System.out.println(e);
    }     
  };

  public static void main(String[] args) throws Exception {
    gettingArgs(args);
    GettingStopWords();

    // Let's have this thread populate the word space
    LinkedList<String> words_list  = new LinkedList<>();
    try{
      File file = new File(args[0]);
      Scanner sc = new Scanner(file);
      String world = new String();
      while (sc.hasNextLine()){
        world = sc.nextLine().toLowerCase();
        world = world.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = world.split(" ");
        for(int i=0; i<wlist.length; i++){
          if(wlist[i] != "" && wlist[i]!= " " && wlist[i].length() >= 2){
            words_list.add(wlist[i]);
          }
        }
      }
      sc.close();
    }   
    catch (IOException e){
      System.out.println(e);
      e.printStackTrace();
    }

    for(String word: words_list){
      word_space.add(word);
    }

    // Let's create the workers and launch them at their jobs
    List<Thread> workers = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        workers.add(new Thread(process_words));
      }
      workers.forEach(Thread::start);

    // Let's wait for the workers to finish
    for(Thread t: workers){
      t.join();
    } 

    // Let's merge the partial frequency results by consuming
   // frequency data from the frequency space
    final LinkedHashMap<String, Integer> word_freqs = new LinkedHashMap<>();
    while (freq_space.isEmpty() == false) {
      LinkedHashMap<String, Integer> freqs = freq_space.poll();
      freqs.forEach((k, v) -> {
        if (word_freqs.containsKey(k)) {
          word_freqs.put(k, word_freqs.get(k) + v);
        } 
        else {
          word_freqs.put(k, v);
        }
      });
    }

    // Sorting and printing the results
    LinkedHashMap<String, Integer> sortedResult = new LinkedHashMap<>();
    word_freqs.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
      .forEachOrdered(x -> sortedResult.put(x.getKey(), x.getValue()));

    int count = 0;
    for(Map.Entry<String, Integer> map : sortedResult.entrySet()){
      if(count < 25){
        System.out.println(map.getKey() + " -> " + map.getValue());
      }
      count++;
    }
  }
}
