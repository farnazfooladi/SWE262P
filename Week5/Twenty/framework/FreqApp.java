import java.util.*;

public interface FreqApp {
    HashMap<String, Integer> loadWords(String path);
    List<Map.Entry<String, Integer>> sortedResult(HashMap<String, Integer> word);  
}
