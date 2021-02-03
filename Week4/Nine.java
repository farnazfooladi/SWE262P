import java.util.*;
import java.io.*;
import java.nio.file.Path;

public class Nine {
  interface IFunction{
    void call(Object arg, IFunction func);
  }

  public static class GettingArgs implements IFunction{
    public void call(Object arg, IFunction func){
      final Path path = Path.of((String)arg);
      if (!path.toFile().exists()){
        System.err.println("Path" + path + "does not exists!");
        System.exit(1);
      }
      func.call(path, new LoadStopWords());
    }
  }

  public static class ReadFreq implements IFunction{
    public void call(Object path, IFunction func){
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
      func.call(words, new IncrementCount());
    }
  }

  public static class LoadStopWords implements IFunction{
    public void call(Object freqWords, IFunction func){
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

      func.call(fw, new SortedResult());
    }
  }

  public static class IncrementCount implements IFunction{
    public void call(Object words, IFunction func){
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
      func.call(word_freq, new PrintResult());
    }
  }

  public static class SortedResult implements IFunction{
    public void call(Object word_freq, IFunction func){
      List<Map.Entry<String, Integer>> sortedResult;
      sortedResult = new ArrayList<>(((HashMap<String, Integer>)word_freq).entrySet());
      sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
      func.call(sortedResult, new NoOp());
    }
  }

  public static class PrintResult implements IFunction{
    public void call(Object sortedResult, IFunction func){
      String result = "";
      for(int i = 0; i < 25; i++) {   
        result += ((List<Map.Entry<String, Integer>>)sortedResult).get(i).getKey() + " -> " + ((List<Map.Entry<String, Integer>>)sortedResult).get(i).getValue() + "\n";
      }
      System.out.println(result);
      func.call(new NoOp(), new NoOp());
    }
  }

  public static class NoOp implements IFunction{
    public void call(Object arg, IFunction func){
    }
  }

  public static void main(String[] args) throws Exception {
    final long startTime = System.nanoTime();
    new GettingArgs().call(args[0], new ReadFreq());
    final long duration = System.nanoTime() - startTime;
    System.out.println(duration);
  }
}
