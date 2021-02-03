import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.util.function.Function;

public class Ten {
  private static class TFTheOne{
    private Object value;

    TFTheOne(Object v){
      this.value = v;
    }

    public TFTheOne bind(Function<Object, Object> func){
      this.value = func.apply(this.value);
      return this;
    }

    public void printme(){
      System.out.println(this.value);
    }
  }

  private static final Function<Object, Object> getArgs = (object) -> gettingArgs(object);

  private static Object gettingArgs(Object args){
    final Path path = Path.of((String)args);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
    return path;
  }

  private static final Function<Object, Object> readFreqWords = (object) -> readfreq(object);

  private static Object readfreq(Object path){
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
    catch (Exception e){
        System.out.println(e);
    }
    return words; 
  }

  private static final Function<Object, Object> countWords = (object) -> increment_count(object);

  private static Object increment_count(Object words){
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

  private static final Function<Object, Object> readStopWords = (object) -> load_stop_words(object);

  private static Object load_stop_words(Object freqWords){
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
    catch (Exception e){
        System.out.println(e);
    }

    fw.removeAll(stop_list);

    return fw;
  }

  private static final Function<Object, Object> sortWordFreq = (object) -> sortedResult(object);

  private static Object sortedResult(Object word_freq){
    List<Map.Entry<String, Integer>> sortedResult;
    sortedResult = new ArrayList<>(((HashMap<String, Integer>)word_freq).entrySet());
    sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    return sortedResult;
  }

  private static final Function<Object, Object> print25 = (object) -> printResult(object);

  private static Object printResult(Object sortedResult){
    String result = "";
    for(int i = 0; i < 25; i++) {   
        result += ((List<Map.Entry<String, Integer>>)sortedResult).get(i).getKey() + " -> " + ((List<Map.Entry<String, Integer>>)sortedResult).get(i).getValue() + "\n";
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    //final long startTime = System.nanoTime();
    TFTheOne TF = new TFTheOne(args[0]).bind(getArgs)
    .bind(readFreqWords).bind(readStopWords).bind(countWords)
    .bind(sortWordFreq).bind(print25);
    TF.printme();
    //final long duration = System.nanoTime() - startTime;
    //System.out.println(duration);
  }
}