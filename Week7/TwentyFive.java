import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.util.function.Function;

public class TwentyFive {

  private static String input = "";
   
  interface MyFun {
    Object func();
  }

  private static class TFQuarantine{
    private LinkedList<Object> funcs = new LinkedList<>();

    public TFQuarantine(Object func){
        this.funcs.add(func);
    }

    public TFQuarantine bind(Object func){
        this.funcs.add(func);
        return this;
    }

    public void execute(){
      Object val = "";
      for (int i = 0; i < this.funcs.size(); i++ ){
        if(guard_callable(val)){
            val = ((Function)this.funcs.get(i)).apply(((MyFun)val).func());
        }
        else{
            val = ((Function)this.funcs.get(i)).apply(val);
        }
      }
      System.out.println(guard_callable(val)?((MyFun)val):val);
    }

    private boolean guard_callable(Object v){
      if (v instanceof MyFun){
        return true;
      }
      else{
        return false;
      }
    }
  }

  private static final Function<Object, Object> getArgs = (object) -> gettingArgs(object);

  private static Object gettingArgs(Object args){
    MyFun fun = new MyFun() {
      public Path func(){
        final Path path = Path.of(input);
      return path;
      }
    };
    return fun; 
  }

  private static final Function<Object, Object> readFreqWords = (object) -> readfreq(object);

  private static Object readfreq(Object path){
    MyFun fun = new MyFun() {
      public LinkedList<String> func(){
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
    };
    return fun;
  }

  private static final Function<Object, Object> countWords = (object) -> increment_count(object);

  private static HashMap <String, Integer> increment_count(Object words){
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
    MyFun fun = new MyFun() {
      public LinkedList<String> func(){
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
    };
    return fun; 
  }

  private static final Function<Object, Object> sortWordFreq = (object) -> sortedResult(object);

  private static List<Map.Entry<String, Integer>> sortedResult(Object word_freq){
    List<Map.Entry<String, Integer>> sortedResult;
    sortedResult = new ArrayList<>(((HashMap<String, Integer>)word_freq).entrySet());
    sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    return sortedResult;
  }

  private static final Function<Object, Object> print25 = (object) -> printResult(object);

  private static String printResult(Object sortedResult){
    String result = "";
    for(int i = 0; i < 25; i++) {   
        result += ((List<Map.Entry<String, Integer>>)sortedResult).get(i).getKey() + " -> " + ((List<Map.Entry<String, Integer>>)sortedResult).get(i).getValue() + "\n";
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    input = args[0];
    TFQuarantine TF = new TFQuarantine(getArgs);
    TF.bind(readFreqWords).bind(readStopWords).bind(countWords)
    .bind(sortWordFreq).bind(print25).execute();
  }
}