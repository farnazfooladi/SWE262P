#!/bin/bash

ONTIME=$(TZ='America/Los_Angeles' date -d "2021-02-03 23:59:59" +"%s")
FIVEPERCENTPENALTY=$(TZ='America/Los_Angeles' date -d "2021-02-04 23:59:59" +"%s")

echo "-------------------------" > rubric.txt
echo "Correctness" >> rubric.txt
echo "-------------------------" >> rubric.txt

echo "--------------------------------------"
echo "Downloading expected program output..."
echo "--------------------------------------"
curl https://raw.githubusercontent.com/crista/exercises-in-programming-style/master/test/pride-and-prejudice.txt -o expected.txt
cat expected.txt
echo ""
echo "-------------------------"
echo "Compiling the programs..."
echo "-------------------------"
javac *.java

for x in $(ls | grep "^[a-zA-Z0-9]*.class" | sed 's/.class//') 
do
  echo "---------" | tee -a rubric.txt
  echo $x | tee -a rubric.txt
  echo "---------" | tee -a rubric.txt
  java $x ../pride-and-prejudice.txt | tee actual.txt
  diff -q expected.txt actual.txt --ignore-all-space --suppress-blank-empty --side-by-side --suppress-common-lines --color 1>/dev/null
  if [[ $? == "1" ]]
  then
    echo "Program output differed from expected." | tee -a rubric.txt
    echo "--------                            -------" | tee -a rubric.txt
    echo "EXPECTED                            ACTUAL" | tee -a rubric.txt 
    echo "--------                            -------" | tee -a rubric.txt
    diff expected.txt actual.txt --ignore-all-space --suppress-blank-empty --side-by-side --suppress-common-lines --color | tee -a rubric.txt
  else
    echo "Program produces correct output." | tee -a rubric.txt
  fi
done

echo "-------------------------" | tee -a rubric.txt
echo "Timestamps" | tee -a rubric.txt
echo "-------------------------" | tee -a rubric.txt
rm expected.txt
rm actual.txt
rm -rf *.class
for x in $(ls -1 *.java) 
do
  turnedin=$(TZ='America/Los_Angeles' date -r $x)
  epoch=$(TZ='America/Los_Angeles' date -r $x +"%s")
  if (( epoch < ONTIME ))
  then
    echo $x "    on time             " $turnedin | tee -a rubric.txt
  elif (( epoch < FIVEPERCENTPENALTY))
  then
    echo $x "    5% penalty         " $turnedin | tee -a rubric.txt
  else
    echo $x "    20% penalty        " $turnedin| tee -a rubric.txt
  fi

done


cat rubric.txt
