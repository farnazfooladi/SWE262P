import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;


public class Framework {
    private static String Conf_path = "config.properties";
    private static Properties prop = new Properties();
    private static FreqApp app;

    private static void loadPlug() throws Exception{
        prop.load(new FileInputStream(Conf_path));
        String appName = prop.getProperty("app");
        String appJar = appName + ".jar";
        URL appUrl = new File(appJar).toURI().toURL();

        URL [] urls = {appUrl};
        URLClassLoader appClass = new URLClassLoader(urls);
        app = (FreqApp) appClass.loadClass(appName).getDeclaredConstructor().newInstance();
    }

    private static Path gettingArgs(String[] args){
        final Path path = Path.of(args[0]);
        if (!path.toFile().exists()){
          System.err.println("Path" + path + "does not exists!");
          System.exit(1);
        }
        return path;
    }

    public static void main (String[] args) throws Exception{
        gettingArgs(args);
        loadPlug();
        List<Map.Entry<String, Integer>> words_freq = app.sortedResult(app.loadWords(args[0]));
        for(int i = 0; i < 25; i++) {   
            System.out.print(words_freq.get(i).getKey() + " -> " + words_freq.get(i).getValue() + "\n");
          }
    }
}
