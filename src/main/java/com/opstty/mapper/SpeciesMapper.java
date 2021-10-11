package com.opstty.mapper;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class SpeciesMapper extends Mapper<Object, Text, Text, NullWritable> {
    public int line_current = 0;

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        if (line_current!= 0) {
            context.write(new Text(value.toString().split(";")[3]), NullWritable.get());
        }
        line_current++;
    }
}