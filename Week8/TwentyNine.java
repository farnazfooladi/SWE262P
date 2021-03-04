import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class TwentyNine {
  private static abstract class ActiveWFObject extends Thread {
    private String name = this.getClass().getSimpleName();
    public BlockingQueue<Object[]> queue = new LinkedBlockingQueue<>();
    private boolean stopMe = false;

    ActiveWFObject() {
      super();
      this.start();
    }

    public void run() {
      while (!stopMe) {
        Object[] message = queue.poll();
        if (message != null) {
          this.dispatch(message);
          if (message[0].equals("die")) {
            stopMe = true;
          }
        }
      }
    }

    abstract void dispatch(Object[] message);

    public void end() {
      this.stopMe = true;
    }
  }

  private static void send(ActiveWFObject receiver, Object[] message) {
      receiver.queue.add(message);
  }

  private static class DataStorageManager extends ActiveWFObject {
    private ArrayList<String> data;
    private String path; 
    private StopWordManager stop_word_manager;

    public void dispatch(Object[] message){
      if(message[0].equals("init")){
        this.init(new Object[]{message[1], message[2]});
      }
      else if(message[0].equals("send_word_freqs")){
        this.process_words(new Object[]{message[1]});
      }
      else{
        send(this.stop_word_manager, message);
      }
    }

    private void init(Object[] message){
      this.path = (String)message[0];
      this.stop_word_manager = (StopWordManager)message[1];
      try{
        File file = new File(path);
        Scanner sc = new Scanner(file);
        data = new ArrayList<>();
        while (sc.hasNextLine()) {
          String line = sc.nextLine().toLowerCase();
          data.add(line);
      
        }
        sc.close();
      }
      catch(IOException e){
        System.out.println(e);
      }
    }

    private void process_words(Object[] message){
      WordFrequencyController recipient = (WordFrequencyController) message[0];
      LinkedList<String> words = new LinkedList<>();
      for(String line: data){
        String word;
        word = line.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
        String[] wlist = word.split(" ");
        for(int i=0; i < wlist.length; i++){
          words.add(wlist[i]);
        } 
      }
      for(String w: words){
        send(this.stop_word_manager, new Object[] {"filter", w});
      }
      send(this.stop_word_manager, new Object[]{"top25", recipient});
    }
  }

  private static class StopWordManager extends ActiveWFObject {
    LinkedList<String> stop_list;
    private WordFrequencyManager word_freqs_manager;

    public void dispatch(Object[] msgs){
      if(msgs[0].equals("init")){
        this.init(new Object[]{msgs[1]});
      }
      else if(msgs[0].equals("filter")){
        this.filter(new Object[]{msgs[1]});
      }
      else{
        send(this.word_freqs_manager, msgs);
      }
    }

    private void init(Object[] message){
      this.word_freqs_manager = (WordFrequencyManager)message[0];
      this.stop_list = new LinkedList<>();
      try{
        File file = new File("../stop_words.txt");
        Scanner sc = new Scanner(file);
        String word = new String();
        while (sc.hasNextLine()){
          word = sc.nextLine();
          String[] wlist = word.split(",");
          for(int i=0; i<wlist.length; i++){
            this.stop_list.add(wlist[i]);
          }
        }
        sc.close();
      }
      catch(IOException e){
        System.out.println(e);
      }
    }

    private void filter(Object[] message){
      String word = (String)message[0];
      if(!stop_list.contains(word) && word.length() >= 2){
        send(this.word_freqs_manager, new Object[]{"word", word});
      }
    }
  }

  private static class WordFrequencyManager extends ActiveWFObject {
    LinkedHashMap<String, Integer> word_freqs = new LinkedHashMap<>();

    public void dispatch(Object[] message){
      if(message[0].equals("word")){
        this.increment_count(new Object[]{message[1]});
      }
      else if(message[0].equals("top25")){
        this.top25(new Object[]{message[1]});
      }
    }

    private void increment_count(Object[] message){
      String word = (String)message[0];
      Integer count = word_freqs.get(word);
      if(count == null){
        this.word_freqs.put(word,1);
      }
      else{
        this.word_freqs.put(word, count+1);
      }
    }

    private void top25(Object[] message) {
      WordFrequencyController recipient = (WordFrequencyController) message[0];
      List<Map.Entry<String , Integer>> result = new ArrayList<>(word_freqs.entrySet());
      result.sort((a , b) -> b.getValue().compareTo(a.getValue()));
      send(recipient, new Object[]{"top25",result});
    }
  }

  private static class WordFrequencyController extends ActiveWFObject{
    private DataStorageManager storage_manager;

    public void dispatch(Object[] message){
      if(message[0].equals("run")){
        this.run(new Object[]{message[1]});
      }
      else if(message[0].equals("top25")){
        this.display(new Object[]{message[1]});
      }
      else{
        System.out.println("Message not understood " + (String)message[0]);
      }
    }

    private void run(Object[] message){
      this.storage_manager = (DataStorageManager) message[0];
      send(this.storage_manager, new Object[]{"send_word_freqs", this});
    }

    private void display(Object[] message){
      List<Map.Entry<String, Integer>> wordFreq = (List<Map.Entry<String, Integer>>)message[0];
      for(int i = 0; i < 25; i++) {
        System.out.print(wordFreq.get(i).getKey() + " -> " + wordFreq.get(i).getValue() + "\n");
      }
      end();
      send(this.storage_manager, new Object[]{"die"});
    }
  }
  

  public static void main(String[] args) throws InterruptedException {
    WordFrequencyManager wfm = new WordFrequencyManager();
    StopWordManager swm = new StopWordManager();
    send(swm, new Object[] {"init", wfm});

    DataStorageManager dsm = new DataStorageManager();
    send(dsm, new Object[]{"init", args[0], swm});

    WordFrequencyController wfc = new WordFrequencyController();
    send(wfc, new Object[]{"run", dsm});

    wfc.join();
    dsm.join();
    swm.join();
    wfc.join();
  }
}