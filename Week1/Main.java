import java.util.*;
import java.io.*;


public class Main {
  public static LinkedList list2  = new LinkedList<>();

  public static void main(String[] args) throws IOException{
    File file1 = new File("../stop_words.txt");
    Scanner sc = new Scanner(file1);
    list2 = readStop(sc);
    //System.out.println(list2);

    if (args.length > 0){
      File file2 = new File(args[0]);
      Scanner sc2 = new Scanner(file2);
      HashMap<String, Integer> dic2  = new HashMap<>();
      dic2 = readUserFile(sc2);
      dic2 = sortByValue(dic2);
      
      int[] iarr = {0};
      dic2.entrySet().forEach(entry->{
        if(iarr[0] < 25)
          System.out.println(entry.getKey() + " -> " + entry.getValue());
          iarr[0]++;
        });
    }
  }

  private static LinkedList readStop(Scanner s){
    LinkedList list  = new LinkedList<>();
    String world = new String();
    while (s.hasNextLine()){
      world = s.nextLine();
      String[] wlist = world.split(",");
      for(int i=0; i<wlist.length; i++){
        list.add(wlist[i]);
      }
    }
  return list;
  }

  private static HashMap readUserFile(Scanner s){
    HashMap<String, Integer> dic  = new HashMap<>();
    String world = new String();
      while (s.hasNextLine()){
        world = s.nextLine().toLowerCase();
        world = world.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = world.split(" ");
        for(int i=0; i<wlist.length; i++){
          Integer count = dic.get(wlist[i]);
          if(wlist[i] != "" && wlist[i]!= " "){
            if(!list2.contains(wlist[i]) && wlist[i].length() >= 2){
              if(count == null){
                dic.put(wlist[i],1);
              }
              else{
                dic.put(wlist[i], count+1);
              }
            }
          }
        }
      }
    return dic;
  }

  // Sorting function is from: https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
  public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){ 
              return (o2.getValue()).compareTo(o1.getValue());} 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
}
