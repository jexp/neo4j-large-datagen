export JAVA_HOME=/usr/lib/jvm/java-8-oracle
mkdir -p target
$JAVA_HOME/bin/javac src/main/java/*.java -d target
$JAVA_HOME/bin/java -cp target AccountFileGenerator 100 3000000000
$JAVA_HOME/bin/java -cp target TransactionFileGenerator 100000000 100 

# clean memory buffers/caches: https://unix.stackexchange.com/questions/87908/how-do-you-empty-the-buffers-and-cache-on-a-linux-system
free && sync && echo 3 > /proc/sys/vm/drop_caches && free

