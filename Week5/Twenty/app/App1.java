import java.util.*;
import java.io.*;


public class App1 implements FreqApp{

    private LinkedList<String> readStop(){
        LinkedList<String> stop_list  = new LinkedList<>();
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
        return stop_list;
      }

    public HashMap <String, Integer> loadWords(String path){
        LinkedList<String> stop_words = readStop();
        HashMap <String, Integer> word_freq  = new HashMap<>();
        try{
            File file = new File(path);
            Scanner sc = new Scanner(file);
            String world = new String();
            while (sc.hasNextLine()){
              world = sc.nextLine().toLowerCase();
              world = world.replaceAll("[^a-zA-Z0-9]", " ");
              String[] wlist = world.split(" ");
              for(int i=0; i<wlist.length; i++){
                Integer count = word_freq.get(wlist[i]);
                if(wlist[i] != "" && wlist[i]!= " "){
                  if(!stop_words.contains(wlist[i]) && wlist[i].length() >= 2){
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
        catch (Exception e){
            System.out.println(e);
        }
        return word_freq;
    }

    public List<Map.Entry<String, Integer>> sortedResult(HashMap<String, Integer> word_freq){
        List<Map.Entry<String, Integer>> sortedResult;
        sortedResult = new ArrayList<>(word_freq.entrySet());
        sortedResult.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedResult;
    }   
}
