import java.util.*;
import java.io.*;
import java.nio.file.Path;

public class Five {
  public static LinkedList stop_list  = new LinkedList<>();
  public static Path path;
  public static HashMap <String, Integer> word_freq  = new HashMap<>();
  public static List<Map.Entry<String, Integer>> sortedResult;

  private static void gettingArgs(String[] args){
    path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
  }

  private static void readStop() throws IOException{
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

  private static void readfreq() throws IOException{
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
  }

  private static void sortResult(){
    sortedResult = new ArrayList<>(word_freq.entrySet());
    sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
  }

  private static void printResult(){
    for(int i = 0; i < 25; i++) {   
        System.out.print(sortedResult.get(i).getKey() + " -> " + sortedResult.get(i).getValue() + "\n");
      }
  }

  public static void main(String[] args) throws IOException{
    gettingArgs(args);
    readStop();
    readfreq();
    sortResult();
    printResult();
  }
}
