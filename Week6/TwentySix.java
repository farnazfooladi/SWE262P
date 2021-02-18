import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.sql.*;


public class TwentySix{

  private static void gettingArgs(String[] args) throws Exception{
    final Path path = Path.of(args[0]);
    if (!path.toFile().exists()){
      System.err.println("Path" + path + "does not exists!");
      System.exit(1);
    }
  }

  private static Connection create_db_schema(String path) throws SQLException{
    Connection connection = null;
    try
    {
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.

      statement.executeUpdate("drop table if exists documents");
      statement.executeUpdate("create table documents (id INTEGER PRIMARY KEY AUTOINCREMENT, name)");
      statement.executeUpdate("drop table if exists words");
      statement.executeUpdate("create table words (id, doc_id, value)");
      statement.executeUpdate("drop table if exists characters");
      statement.executeUpdate("create table characters (id, word_id, value)");

      load_file_into_db(path, connection);

      ResultSet resultSet = statement.executeQuery("SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC");

      for(int i = 0; i < 25; i++){
        resultSet.next();
        System.out.println(resultSet.getString(1) + " -> " + resultSet.getInt(2));
      }
    }
    catch(SQLException e)
    {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
      System.err.println("error");
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }
    return connection;
  }

  private static void load_file_into_db(String path, Connection connection) throws SQLException{
    LinkedList<String> words_result = extract_words(path);
    Statement state = connection.createStatement();
    state.executeUpdate("INSERT INTO documents (name) VALUES (" + "'"+path+"'" + ")") ;
    ResultSet resultSet = state.executeQuery("SELECT id from documents WHERE name=" + "'"+path+"'");
    int doc_id = resultSet.getInt("id");
    
    int word_id;
    try{
      resultSet = state.executeQuery("SELECT MAX(id) FROM words");
      word_id = resultSet.getInt(1);
    }

    catch(SQLException e){
      word_id = 0;
    }

    connection.setAutoCommit(false);
    for(String w: words_result){
      String insertString = String.format("INSERT INTO words VALUES ('" +  
                 "%1$s', '%2$s', '%3$s')", word_id, doc_id, w);
      state.executeUpdate(insertString);
      int char_id = 0;
      for(int i=0; i< w.length(); i++){
        Character cValue = w.charAt(i);
        String s = String.format("INSERT INTO characters VALUES ('" +  
                 "%1$s', '%2$s', '%3$s')", char_id, word_id, cValue);
        state.executeUpdate(s);
        char_id += 1;
      }
      word_id += 1;
    }

    connection.commit();    
  }

  private static LinkedList<String> extract_words(String path){
    LinkedList word_freq  = new LinkedList<>();

    try{
      LinkedList stop_list  = new LinkedList<>();
      File file = new File("../stop_words.txt");
      Scanner sc = new Scanner(file);
      String word = new String();
      while (sc.hasNextLine()){
        word = sc.nextLine();
        String[] wlist = word.split(",");
        for(int i=0; i<wlist.length; i++){
          stop_list.add(wlist[i]);
        }
      }
      sc.close();
    
      File file2 = new File(path.toString());
      Scanner sc2 = new Scanner(file2);
      String word2 = new String();
      while (sc2.hasNextLine()){
        word2 = sc2.nextLine().toLowerCase();
        word2 = word2.replaceAll("[^a-zA-Z0-9]", " ");
        String[] wlist = word2.split(" ");
        for(int i=0; i<wlist.length; i++){
          if(wlist[i] != "" && wlist[i]!= " "){
            if(!stop_list.contains(wlist[i]) && wlist[i].length() >= 2){
              word_freq.add(wlist[i]);
            }
          }
        }
      }
    }
    catch (Exception e){
      System.out.println(e);
    }

    return word_freq;
  }

  public static void main(String[] args)throws Exception{
    gettingArgs(args);
    create_db_schema(args[0]);
  }
}