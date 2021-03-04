import java.io.File;
import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;


public class ThirtyTwo{
  private static Scanner read_file(Path path) throws IOException{
    File file = new File(path.toString());
    Scanner sc = new Scanner(file);
    return sc;
  }
  
  private static Iterable<String> partition(Scanner sc, int numLines) {
    int i = 0;
    String join_line = "";
    Stream.Builder<String> join_200 = Stream.builder();
    while (sc.hasNextLine()){
      if(i == (numLines-1)){
        join_line += " " + sc.nextLine();
        join_200.add(join_line);
        i = 0;
        join_line = "";
      }
      else{
        join_line += " " + sc.nextLine();
        i++;
      }
      
    }
    Iterable<String> result = () -> join_200.build().iterator();
    return result;
  }

  private static LinkedList scan(String lines){
    LinkedList<String> words  = new LinkedList<>();
    try{
      lines = lines.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();;
      String[] wlist = lines.split(" ");
      for(int i = 0; i < wlist.length; i++){
        if(wlist[i] != "" && wlist[i]!= " " && wlist[i].length() >= 2){
          words.add(wlist[i]);
        }
      }
    }
    catch (Exception e){
        System.out.println(e);
    }
    return words; 
  }

  private static ArrayList<ArrayList<Object>> split_words(String data_str){
    LinkedList<String> stop_list  = new LinkedList<>();
    LinkedList<String> removed_stop_list  = new LinkedList<>();
    LinkedList<String> fw = scan(data_str);
    ArrayList<ArrayList<Object>> result = new ArrayList<>();
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

    fw.removeAll(stop_list);

    for( int i = 0; i < fw.size(); i++){
      ArrayList<Object> tuple = new ArrayList<>();
      tuple.add(fw.get(i));
      tuple.add(1);
      result.add(tuple);
    }
    return result;
  }

  private static HashMap<String, ArrayList<ArrayList<Object>>> regroup(LinkedList<ArrayList<ArrayList<Object>>> pairs_list){
    HashMap<String, ArrayList<ArrayList<Object>>> mapping = new HashMap<>();
    for(int i = 0; i < pairs_list.size(); i++){
      for(int j = 0; j < pairs_list.get(i).size(); j++){
        if(mapping.containsKey(pairs_list.get(i).get(j).get(0))){
          mapping.get(pairs_list.get(i).get(j).get(0)).add(pairs_list.get(i).get(j));
        }
        else{
          ArrayList<ArrayList<Object>> temp = new ArrayList<>();
          temp.add(pairs_list.get(i).get(j));
          mapping.put((String)(pairs_list.get(i).get(j).get(0)), temp);
        }
      }
    }
    return mapping;
  }

  //(word, [(word, 1), (word, 1)...)])
  private static HashMap<String, Integer> counting_words(Map.Entry<String, ArrayList<ArrayList<Object>>> tuple){
    HashMap result = new HashMap<>();

    int add_total = 0;
    for(int i = 0; i < tuple.getValue().size(); i++){
      add_total += (Integer)tuple.getValue().get(i).get(1);
    }

    result.put(tuple.getKey(), add_total);
    return result;
  }

  private static List<Map.Entry<String, Integer>> sortedResult(HashMap<String, Integer> word_freq){
    List<Map.Entry<String, Integer>> sortedResult;
    sortedResult = new ArrayList<>(word_freq.entrySet());
    sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    return sortedResult;
  }

  private static void printResult(List<Map.Entry<String, Integer>> sortedResult){
    for(int i = 0; i < 25; i++) {   
        System.out.print(sortedResult.get(i).getKey() + " -> " + sortedResult.get(i).getValue() + "\n");
      }
  }

  public static void main(String[] args) throws IOException{
    Path path = Path.of(args[0]);
    Iterable<String> x = partition(read_file(path), 200);
    LinkedList<ArrayList<ArrayList<Object>>> splits = new LinkedList<>();
    HashMap<String, Integer> wf = new HashMap<>();
    for (String s: x){
      splits.add(split_words(s));
    }
    HashMap<String, ArrayList<ArrayList<Object>>> rg = regroup(splits);
    for(Map.Entry<String, ArrayList<ArrayList<Object>>> e: rg.entrySet()){
      HashMap<String, Integer> wordFreq = counting_words(e);
      for(Map.Entry<String, Integer> j: wordFreq.entrySet()){
        wf.put(j.getKey(), j.getValue());
      }
    }
    List<Map.Entry<String, Integer>> result = sortedResult(wf);
    printResult(result);
  }
}