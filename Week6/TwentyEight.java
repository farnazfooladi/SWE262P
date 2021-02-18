import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwentyEight{

  private static void gettingArgs(String[] args){
    final Path path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
  }

  private static Iterable<String> readingLines(String path){
    try{
      Path p = Path.of(path);
      Iterator<String> l = Files.lines(p).iterator();
      Iterable<String> lines = () -> l;
      return lines;
    }
    catch(Exception e){
      return null;
    }
  }

  private static Iterable<String> all_words(String path){
    // Citation: https://www.geeksforgeeks.org/stream-builder-java-examples/
    Stream.Builder<String> word_freq = Stream.builder();
    try{
      for(String s: readingLines(path)){
        String s2 = s.toLowerCase();
        s2 = s2.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = s2.split(" ");
        for(int i=0; i<wlist.length; i++){
          if(wlist[i] != "" && wlist[i]!= " " && wlist[i].length() >= 2){
            word_freq.add(wlist[i]);
          }
        }
      }
    }

    catch(Exception e){
      System.out.println(e);
    }

    Iterable<String> result = () -> word_freq.build().iterator();
    return result;
  }

  private static Iterable<String> non_stop_words(String path){
    Stream.Builder<String> stop_words = Stream.builder();
    List<String> stop_words_list = new ArrayList<String>();

    try{
      Path file_path = Path.of("../stop_words.txt");
      stop_words_list = Files.lines(file_path).flatMap(line -> Arrays.stream(line.split(","))).collect(Collectors.toList());
      String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
      List<String> aList = Arrays.asList(a);
      stop_words_list.addAll(aList);
      
      for (String w : all_words(path)){
          if(!stop_words_list.contains(w)){
              stop_words.add(w);
          }
      }
    }
    catch(Exception e){
      System.out.println(e);
    }

    Iterable<String> result = () -> stop_words.build().iterator();
    return result;
  }

  private static Iterable<List<Map.Entry<String, Integer>>> count_and_sort(String path){
    Stream.Builder<List<Map.Entry<String, Integer>>> sortedResult = Stream.builder();
    HashMap<String, Integer> words_freq = new HashMap<>();
    
    try{
      int i = 1;
      for (String word: non_stop_words(path)){
        Integer count = words_freq.get(word);
          
        if(count != null){
          words_freq.put(word, count+1);
        }
        else{
          words_freq.put(word, 1);
        }

        if(i % 5000 == 0){
          // Citation: https://howtodoinjava.com/java/sort/java-sort-map-by-values/
          LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

          words_freq.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
            .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

          sortedResult.add(reverseSortedMap.entrySet().stream().collect(Collectors.toList()));
         
        }
        i++;
      }

      LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

      words_freq.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
        .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

      sortedResult.add(reverseSortedMap.entrySet().stream().collect(Collectors.toList()));
    }

    catch(Exception e){
      System.out.println(e);
    }

    Iterable<List<Map.Entry<String, Integer>>> result = () -> sortedResult.build().iterator();
    return result;
  }

  public static void main(String[] args){
    gettingArgs(args);
    
    for (var list : count_and_sort(args[0])) {
      int i = 0; 
      if(list.size() > 25){
        i = 25;
      }
      else{
        i = list.size();
      }

      System.out.println("----------DEBUGGING----------");
      for(Map.Entry<String, Integer> e : list.subList(0, i)){
        System.out.println(e.getKey() + " -> " + e.getValue());
      }
    }
  }
}