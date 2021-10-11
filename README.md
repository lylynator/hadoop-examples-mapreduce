# hadoop-examples-mapreduce


*Florence Nguyen 20180437 DS3, Big Data Framework*
# YARN & MapReduce 2

## 1. MapReduce JAVA

### 1.1 Install OpenJDK 8

Once we cloned the [repository](https://github.com/makayel/hadoop-examples-mapreduce), we generate a JAR using maven lifecycle package.

**Build sucess message :**

    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  42.306 s
    [INFO] Finished at: 2021-10-07T12:23:21+02:00
    [INFO] ------------------------------------------------------------------------
    
    Process finished with exit code 0

A new folder package appeared with JAR file. We will use this file:

    hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar


### 1.6 Send the JAR to the edge node
#### 1.6.1 Microsoft Windows
After dowloading FileZilla, we connect to the edge node using via ssh with thoses parameters: 

> Host: sftp://hadoop-edge01.efrei.online 
>  Username: ssh username 
> Password: ssh password  
> Port: 22

**Connexion message**

    Statut :	Connected to hadoop-edge01.efrei.online
    Statut :	Récupération du contenu du dossier...
    Statut :	Listing directory /home/f.nguyen
    Statut :	Contenu du dossier "/home/f.nguyen" affiché avec succès

Then we slide the JAR file to the node in the f.nguyen folder.

#### 1.6.3 Run the job

First we need to import the dataset :  *trees.csv file* using the command wget.

     wget https://raw.githubusercontent.com/makayel/hadoop-examples-mapreduce/main/src/test/resources/data/trees.csv
    --2021-10-07 12:43:09--  https://raw.githubusercontent.com/makayel/hadoop-examples-mapreduce/main/src/test/resources/data/trees.csv

**Sucess message**

    Resolving raw.githubusercontent.com (raw.githubusercontent.com)... 185.199.108.133, 185.199.109.133, 185.199.110.133, ...
    Connecting to raw.githubusercontent.com (raw.githubusercontent.com)|185.199.108.133|:443... connected.
    HTTP request sent, awaiting response... 200 OK
    Length: 16680 (16K) [text/plain]
    Saving to: ‘trees.csv’

Then we use an alias (job) to simplify the command, job contains the path of the jar file : 


    alias job="yarn jar hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar"

Copy the file from le local to the cluster :

     hdfs dfs -copyFromLocal hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar

Run the command wordcount

    job wordcount trees.csv count
**Output**

    [...]21/10/07 13:52:55 INFO mapreduce.Job:  map 0% reduce 0%
    21/10/07 13:53:04 INFO mapreduce.Job:  map 100% reduce 0%
    21/10/07 13:53:09 INFO mapreduce.Job:  map 100% reduce 100%
    21/10/07 13:53:09 INFO mapreduce.Job: Job job_1630864376208_2897 completed successfully
    21/10/07 13:53:10 INFO mapreduce.Job: Counters: 54
            File System Counters
                    FILE: Number of bytes read=16561
                    FILE: Number of bytes written=559089
                    FILE: Number of read operations=0
     [...]
      File Input Format Counters
                    Bytes Read=16680
            File Output Format Counters
                    Bytes Written=14251
Now we  count occurences in the file to test the job

    hdfs dfs -cat count/part-r-00000
**Output**

    [...]      172
    des     24
    du      51
    encens;;11;Jardin       1
    et      2
    faux-acacia;;4;Square   1
    fleurs;;66;Bois 1
    glutineux;;28;Square    1
    grande  1
    grandes 1
    grands  1
    gravelle)       1
    guide   2
    gutta-percha;;7;Parc    1
    géant;;12;Jardin        1
    [...]


 ### 1.8 Remarkable trees of Paris
*You are going to write some MapReduce jobs on the remarkable trees of Paris using this dataset. Download the file and put it in your HDFS home directory.*

**1.8.1  Districts containing trees (very easy)**
-

- We create inside the package job a new class : `DistinctDistricts.java`

#### A. DistinctTreesMapper.java
- We create inside the package **mapper** a new class : `DistinctTreesMapper.java`
This class is and extend of mapper class : `public class DistinctTreesMapper extends Mapper<Object, Text, IntWritable, IntWritable>`
This class contains a map funtion, once the mapper finished its task, the output will be give to the reducer as an input.

- The goal is to count the trees and associate them with a key value. We use the function split() to get the district value : `value.toString().split(";")`

#### B. DistinctTreesReducer.java
- We create inside the package **reducer** a new class : `DistinctTreesReducer`
This class is an etend of reducer class : `public class DistinctTreesReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable>`
This class contains a reduce function. 


- The reducer works as the wordcount job, it will add all values together. Returning the couple (key, sum) : `context.write(key, new IntWritable(somme));`. For example if he counts 2 trees belong to the same district, he will group them together. The reducer iterates through the list values and increment the somme variable.

>      for (IntWritable val : values) {  
>         somme += val.get();  
>     }



Let's set the DistinctDistrict class with mapper and reducer

    Job job = Job.getInstance(conf, "distinctDistricts");  
    job.setJarByClass(DistinctDistricts.class);  
    job.setMapperClass(DistinctTreesMapper.class);  
    job.setCombinerClass(DistinctTreesReducer.class);  
    job.setReducerClass(DistinctTreesReducer.class);
    
*As we said, the ouput of the mapper is the input of the reducer. It because we set CombinerClass.* 

Now let's try the job :


    job distinctDistricts trees.csv districts
    hdfs dfs -cat districts/part-r-00000

    11	1
    12	29
    13	2
    14	3
    15	1
    16	36
    17	1
    18	1
    19	6
    20	3
    3	1
    4	1
    5	2
    6	1
    7	3
    8	5
    9	1



**1.8.2 Show all existing species (very easy)**
 *Write a MapReduce job that displays the list of different species trees in this file.*
-
- We create the job Species.java inside the package job. 

        Job job = Job.getInstance(conf, "species");  
          job.setJarByClass(Species.class);  
          job.setMapperClass(SpeciesMapper.class);  
          job.setCombinerClass(SpeciesReducer.class);  
          job.setReducerClass(SpeciesReducer.class);  
          job.setOutputKeyClass(Text .class);  
          job.setOutputValueClass(NullWritable .class);  
         for (int i = 0; i < otherArgs.length - 1; ++i) {  
         FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
    
   Insider de AppDriver class we add the Species job 

#### A. SpeciesMapper.java

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {  
        if (line_current!= 0) {  
            context.write(new Text(value.toString().split(";")[3]), NullWritable.get());  
      }  
        line_current++;  
    }

#### B. SpeciesReducer.java

    public class SpeciesReducer extends Reducer<Text, NullWritable, Text, NullWritable> {  
        public void reduce(Text key, Iterable<NullWritable> values, Context context)  
                throws IOException, InterruptedException {  
            context.write(key, NullWritable.get());  
      }


