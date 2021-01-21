import java.util.*;
import java.io.*;
import java.nio.file.Path;

public class Six {

  private static Path gettingArgs(String[] args){
    final Path path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
    return path;
  }

  private static LinkedList readStop() throws IOException{
    LinkedList stop_list  = new LinkedList<>();
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
    return stop_list;
  }

  private static HashMap readfreq(Path path, LinkedList stop_list) throws IOException{
    HashMap <String, Integer> word_freq  = new HashMap<>();
    File file = new File(path.toString());
    Scanner sc = new Scanner(file);
    String world = new String();
    while (sc.hasNextLine()){
      world = sc.nextLine().toLowerCase();
      world = world.replaceAll("[^a-zA-Z0-9]", " ");
      String[] wlist = world.split(" ");
      for(int i=0; i<wlist.length; i++){
        Integer count = word_freq.get(wlist[i]);
        if(wlist[i] != "" && wlist[i]!= " "){
          if(!stop_list.contains(wlist[i]) && wlist[i].length() >= 2){
            if(count == null){
              word_freq.put(wlist[i],1);
            }
            else{
              word_freq.put(wlist[i], count+1);
            }
          }
        }
      }
    }
    sc.close();
    return word_freq;
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
    printResult(sortedResult(readfreq(gettingArgs(args),readStop())));
  }
}
