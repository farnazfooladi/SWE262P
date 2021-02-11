Week5 Exercise

This exercise will implement the term frequency for words in the text file "pride-and-prejudice.txt" in 2 different styles:

Style 1: reflective 
To run this java file:
1) cd Week5/Seventeen
2) javac Seventeen.java
3) java Seventeen ../../pride-and-prejudice.txt

Style 2: plugins 
To run this program for App1:
1) cd Week5/Twenty/framework 
2) javac Framework.java
3) jar cvmf manifest.mf framework.jar *.class
4) cd ../app
5) cp ../framework/framework.jar .
6) javac -cp framework.jar App1.java
7) jar cf App1.jar App1.class
8) cd ../deploy/
9) cp ../framework/framework.jar .
10) cp ../app/App1.jar .
11) java -jar framework.jar ../../../pride-and-prejudice.txt

* Now for running App2:
- cd  ../../../
- redo step 2 through 11 and obly change all App1 to App2


The top 25 most frequency words from text file will be save in .txt files.

