import java.util.*;
import java.io.*;


public class App2 implements FreqApp{

    public HashMap <String, Integer> loadWords(String path){
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

        LinkedList<String> sw = load_stop_words(words);
        HashMap <String, Integer> wf = increment_count(sw);
        return wf;
      }

    private HashMap <String, Integer> increment_count(LinkedList<String> words){
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

    private LinkedList<String> load_stop_words(LinkedList<String> freqWords){
        LinkedList<String> stop_list  = new LinkedList<>();
        LinkedList<String> fw = (LinkedList<String>)freqWords;
        try{
          File file = new File("../../../stop_words.txt");
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

    public List<Map.Entry<String, Integer>> sortedResult(HashMap<String, Integer> word_freq){
        List<Map.Entry<String, Integer>> sortedResult;
        sortedResult = new ArrayList<>(word_freq.entrySet());
        sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedResult;
    }
}