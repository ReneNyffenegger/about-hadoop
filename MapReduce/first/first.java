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
import java.util.HashSet;

public class first {

  public static class Mapper_ extends Mapper <
     //
     // Types that are relevant for for the mapper:
        LongWritable,      // Type of key_in    (1st parameter of method map()
        Text,              // Type of value_in  (2nd parameter of method map()
        Text,              // Type of key_out   (1st parameter of methods Context.write()
        Text               // Type of value_out (2nd paramater of method Context.write()
      > {

    private Text value_out_filename;

    @Override
    protected void setup(
    // Setup is called once at the beginning of the task.
    //
    // The Context object allows the Mapper/Reducer to interact with the
    // rest of the Hadoop system.
    // It includes 
    //   o  configuration data for the job
    //   o  interfaces to emit output (see context.write in reduce() below )
    // It can be used to
    //   o  report progress
    //   o  set application-level status messages
    //   o  indicate a job is alive
    //   o  to get the values that are stored in job
    //      configuration across map/reduce phase
    //  See also http://stackoverflow.com/a/26955662/180275
       Context     ctx
    ) 
    {

    // InputSplit represents the data to be processed by an individual Mapper.
       InputSplit input_split = ctx.getInputSplit();

    // A FileSplit represents a section of an input file. The class extends InputSplit.
       FileSplit file_split = (FileSplit) input_split;

       long pos_1st_byte    = file_split.getStart();

    // The Path to the file split's data.
       Path file_path = file_split.getPath();

    // Turn Path into a String
       String file_name = file_path.getName();

    // The file name of the input document is the out-key.
    // value_out_filename is a member variable
       value_out_filename     = new Text(file_name);

    }

    @Override
    protected void map(
    //
    //  map is called once for each key/value pair in the input split.
    //
      LongWritable      key_in,
      Text              value_in,
      Context           ctx
    ) throws IOException, InterruptedException {

   // Generate a key/value pair for each word found in the input file.
   // The Word is the key, while the value is the file name.
      for (String word : StringUtils.split(value_in.toString())) {
       // Mapper.Context (of which class ctx is an instance) implements
       // the interface MapContext, which in turn extends TaskInputOutputContext.
       // So, see TaskInputOutputContext for the API-documentaion.

          ctx.write(new Text(word), value_out_filename);
      }

    }

  }

  public static class Reducer_ extends Reducer <

     //
     // Types that are relevant for for the mapper:
        Text    , // Type of 1st (key) parameter of reduce(), corresponds
                  // to type of mapper's out key.
        Text    , // Type of which the reduce() method receives an
                  // Iterable, corresponds to type of mappers out value.
        Text    , // Type of 1st parameter of context.write()
        Text      // Type of 2nd parameter of context.write()
    > {

    public void reduce(
    //
    //  reduce is called once for each (unique) map-output-key (which,
    //  in this example, is a word)
    //
        Text                  key_in_word,
        Iterable<Text>        values_in_file_names,
        Context               context
    ) throws IOException, InterruptedException {

      HashSet<String> file_names_unique = new HashSet<String>();

   // Iterate over each filename in which a word (key_in_word)
   // occurs and store in in a HashSet (file_names_unique), so
   // that we're able to retrieve the unique file names for
   // the word.
      for (Text file_name: values_in_file_names) {
        file_names_unique.add(file_name.toString());
      }

      String file_names_out = new String( StringUtils.join(file_names_unique, " - ") );

   // Write a key/value pair. In this case: key_in = key_out.
      context.write(key_in_word, new Text(file_names_out));
    }
  }

  public static void main(String[] args) throws Exception {

 // A Configuration provides access to configuration parameters. 
    Configuration conf = new Configuration();
 
 // The Job class is used to create, configure and run
 // instances of MapReduce applications.
 //
 // getInstance(...) creates a new Job with no particular Cluster
 // and a given job name.
    Job job = Job.getInstance(conf, "MapRed Introduction");

 // Specify the classes that will to the Map Reduce job:
    job.setJarByClass  (first    .class);
    job.setMapperClass (Mapper_  .class);
    job.setReducerClass(Reducer_ .class);

 // Specify the output key and value types for a job.
 // By default, setOutput{Key,Value}Class() sets the type
 // expected as output from both the map and reduce phases.
 // 
 // If the mapper emits different types than the reducer,
 // the mapper's types can be set with job.setMapOutput{Key,Value}Class().
    job.setOutputKeyClass  (Text.class);
    job.setOutputValueClass(Text.class);

 // Set Path to inputfiles for the map-reduce job:
    FileInputFormat.setInputPaths (job, new Path(args[0]));

 // Set the Path of the output directory for the map-reduce job:
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}
