import java.io.*;
import java.util.*;
import java.nio.file.Path;


public class TwentyOne{

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

  private static LinkedList<String> extract_words(Object path) {
    if (!(path instanceof String) || path == null){
      return new LinkedList<>();
    }

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
        return new LinkedList<>();
    }
    return words;
  }

  private static LinkedList<String> remove_stopWords(Object freqWords){
    if (!(freqWords instanceof List)){
        return new LinkedList<>();
    }

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
      return fw;
    }

    fw.removeAll(stop_list);

    return fw;
  }

  private static HashMap<String,Integer> increment_count(Object words){
    if(!(words instanceof List) || ((List)words).isEmpty()){
        return new HashMap<>();
    }
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
    if(!(word_freq instanceof Map) || ((Map)word_freq).isEmpty()){
        return new LinkedList<>();
    }
    List<Map.Entry<String, Integer>> sortedResult;
    sortedResult = new ArrayList<>(((HashMap<String, Integer>)word_freq).entrySet());
    sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    return sortedResult;
  }

  public static void main (String[] args){
    List<Map.Entry<String, Integer>> word_freqs_result = sortedResult(increment_count(remove_stopWords(extract_words(gettingArgs(args)))));

    for(int i = 0; i < 25; i++) {   
      System.out.println(word_freqs_result.get(i).getKey() + " -> " + word_freqs_result.get(i).getValue());
    }
  }
}