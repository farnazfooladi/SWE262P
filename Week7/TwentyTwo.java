import java.io.*;
import java.util.*;
import java.nio.file.Path;


public class TwentyTwo{

  private static String gettingArgs(String[] args){
    String path = "";
    if(args.length > 0){
      path = args[0];
    }
    else{
      path = "input.txt";
    }
    return path;
  }

  private static LinkedList<String> extract_words(Object path) throws IOException{

    assert (path instanceof String) : "I need a string!";
    assert (path != null) : "I need a non-empty string!";

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
          if(wlist[i] != "" && wlist[i]!= " " && wlist[i].length() >= 2){
            words.add(wlist[i]);
          }
        }
      }
      sc.close();
    }   
    catch (IOException e){
        System.out.println("I/O error(" + e.getMessage() + ") when opening " + path + ":");
        e.printStackTrace();
        throw e;
    }
    return words;
  }

  private static LinkedList<String> remove_stopWords(Object freqWords) throws IOException{
    assert (freqWords instanceof List) : "I need a list!";

    LinkedList<String> stop_list  = new LinkedList<>();
    LinkedList<String> removed_stop_list  = new LinkedList<>();
    LinkedList<String> fw = (LinkedList<String>)freqWords;
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
      System.out.println("I/O error(" + e.getMessage() + ") when opening ../stop_words.txt:");
      e.printStackTrace();
      throw e;
    }

    fw.removeAll(stop_list);

    return fw;
  }

  private static HashMap<String,Integer> increment_count(Object words){
    assert (words instanceof List) : "I need a list!";
    assert !((List)words).isEmpty() : "I need a non-empty list!";

    HashMap <String, Integer> word_freq  = new HashMap<>();
    for (int i = 0; i < ((LinkedList<String>) words).size(); i++){
      Integer count = word_freq.get(((LinkedList<String>) words).get(i));
      if(count == null){
        word_freq.put(((LinkedList<String>) words).get(i),1);
      }
      else{
        word_freq.put(((LinkedList<String>) words).get(i), count+1);
      }
    }

    return word_freq;
  }

  private static List<Map.Entry<String, Integer>> sortedResult(Object word_freq){
    assert (word_freq instanceof Map) : "I need a map";
    assert !((Map)word_freq).isEmpty() : "I need a non-empty map";

    List<Map.Entry<String, Integer>> sortedResult;
    sortedResult = new ArrayList<>(((HashMap<String, Integer>)word_freq).entrySet());
    sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    return sortedResult;
  }


  public static void main (String[] args){
    try{
      assert args.length > 0 : "You idiot! I need an input file!";        
      List<Map.Entry<String, Integer>> word_freqs_result = sortedResult(increment_count(remove_stopWords(extract_words(gettingArgs(args)))));

      assert (word_freqs_result instanceof Map) : "OMG! This is not a Map!";
      assert (word_freqs_result.size() >= 25) : "SRSLY? Less than 25 words!";

      for(int i = 0; i < 25; i++) {   
        System.out.println(word_freqs_result.get(i).getKey() + " -> " + word_freqs_result.get(i).getValue());
      }
    }

    catch (Exception e){
      System.out.println("Something wrong: " + e.getMessage());
      e.printStackTrace();
    }
  }
}