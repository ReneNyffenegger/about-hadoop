import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.util.ArrayList;
import java.util.Random;

public class phases {

  private static class idGenerator {
    static String id(String tag) {
       Random r = new Random();
       return tag + "-" + r.nextInt(10000);
    }
  }

  public static class Mapper_ extends Mapper<LongWritable, Text, Text, Text> {

    private String id;

    @Override
    protected void setup(Context ctx) {

       if (id != null) {
         int i=0/0;
       }
       id = idGenerator.id("Mapper");

    }

    @Override
    protected void map(
      LongWritable      key_in,
      Text              value_in,
      Context           ctx
    ) throws IOException, InterruptedException {

      ctx.write(
        new Text(id), 
	new Text("map(" + key_in + ", " + value_in + ")")
      ); 

    }

  }

  public static class Reducer_ extends Reducer<Text, Text, Text, Text> {

    private String id;

    @Override
    public void setup(Context ctx) {

       if (id != null) {
         int i= 0/0;
       }

       id = idGenerator.id("Reducer");

    }

    public void reduce(
        Text                  id_mapper,
	Iterable<Text>        map_calls,
        Context               context
    ) throws IOException, InterruptedException {

      ArrayList<String> map_calls_ = new ArrayList<String>();

        for (Text map_call: map_calls) {
          map_calls_.add(map_call.toString());
        }

      context.write(
        new Text(id), 
	new Text(id_mapper + "[" + StringUtils.join(map_calls_, ";") + "]")
      );
    }
  }

  public static void main(String[] args) throws Exception {

    Configuration conf = new Configuration();
 
    Job job = Job.getInstance(conf, "first");

    job.setJarByClass  (phases   .class);
    job.setMapperClass (Mapper_  .class);
    job.setReducerClass(Reducer_ .class);

    job.setOutputKeyClass  (Text .class);
    job.setOutputValueClass(Text .class);

    FileInputFormat.setInputPaths (job, new Path(args[0]));

    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}

