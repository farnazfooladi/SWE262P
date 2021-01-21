import java.util.*;
import java.awt.event.WindowStateListener;
import java.io.*;


public class Four {
  //public static LinkedList list2  = new LinkedList<>();

  public static void main(String[] args) throws IOException{

    // Reading stop words
    File file1 = new File("../stop_words.txt");
    Scanner sc = new Scanner(file1);
    LinkedList list  = new LinkedList<>();
    String world = new String();
    while (sc.hasNextLine()){
      world = sc.nextLine();
      String[] wlist = world.split(",");
      for(int i=0; i<wlist.length; i++){
        list.add(wlist[i]);
      }
    }

    // Reading actual file 
    if (args.length > 0){
      Scanner s = null;
      s = new Scanner((new File(args[0])));
      ArrayList<Map.Entry<String, Integer>> word_freqs  = new ArrayList<>();

      while (s.hasNextLine()){
        int start_word = -1;
        int count = 0;
        String str = s.nextLine() + " ";
        char[] myChar = str.toCharArray();
        for(int i = 0; i < myChar.length; i++){
          if(start_word == -1){
            if(Character.isDigit(myChar[i]) || Character.isLetter(myChar[i])){
              start_word = count;
            }
          }
          else{
            if(!(Character.isDigit(myChar[i]) || Character.isLetter(myChar[i]))){
              boolean found = false;
              int pair_index;
              String word = str.substring(start_word,count).toLowerCase();
              if(!list.contains(word) && word.length() >= 2){
                for(pair_index = 0; pair_index < word_freqs.size(); pair_index++){
                  if(word.equals(word_freqs.get(pair_index).getKey())){
                    int num = word_freqs.get(pair_index).getValue();
                    num += 1;
                    word_freqs.get(pair_index).setValue(num);
                    found = true;
                    break;
                  }
                }
                if(found == false){
                  Map.Entry<String, Integer> pair = new AbstractMap.SimpleEntry<String, Integer>(word, 1);
                  word_freqs.add(pair);
                }
                else if(word_freqs.size() > 1){
                  for(int n = pair_index-1; n >= 0; n--){
                    if(word_freqs.get(pair_index).getValue() > word_freqs.get(n).getValue()){
                      Map.Entry<String, Integer> temp = word_freqs.get(pair_index);
                      word_freqs.set(pair_index, word_freqs.get(n));
                      word_freqs.set(n, temp);
                      pair_index = n;
                    }
                  }
                }
              }
              start_word = -1;
            }
          }
          count++;
        }
      }
      s.close();
      for(int i = 0; i < 25; i++) {   
        System.out.print(word_freqs.get(i).getKey() + " -> " + word_freqs.get(i).getValue() + "\n");
      }
    }  
  }
}
