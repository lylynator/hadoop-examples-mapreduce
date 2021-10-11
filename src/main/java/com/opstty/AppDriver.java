package com.opstty;

import com.opstty.job.DistinctDistricts;
import com.opstty.job.Species;
import com.opstty.job.WordCount;
import org.apache.hadoop.util.ProgramDriver;

public class AppDriver {
    public static void main(String argv[]) throws Throwable {
        int exitCode = -1;
        ProgramDriver programDriver = new ProgramDriver();

        try {
            programDriver.addClass("wordcount", WordCount.class,
                    "A map/reduce program that counts the words in the input files.");
            programDriver.addClass("distinctDistricts", DistinctDistricts.class,
                    "A map/reduce program that returns the distinct districts with trees inside.");
            programDriver.addClass("species", Species.class,
                    "A map/reduce program that returns the distinct tree species.");


            exitCode = programDriver.run(argv);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.exit(exitCode);
    }
}
