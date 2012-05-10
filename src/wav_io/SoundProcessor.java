package wav_io;
import wav.*;

/**
 * #TODO
 * Prompt the user for an input file
 * Prompt the user for an output file
 * Prompt the user for the starting time
 * Prompt the user for the length
 * Trim the file and save it.
 * 
 * 
 * Available Methods:
 * 
 * quieter();
 * combine();   
 * reverse();
 * speedUp();
 * removeSilence();
 * addEcho();
 * trimFile();
 * 
 */

/**
 * WavFile Methods Reference:
 * 
 * getMonoData : none -> int[]
 * getDataOfChannel : channel (int) -> int[] ; 0: Left, 1: Right
 * setData : data (int[]) -> none
 * setDataOfChannel : data (int[]), channel (int) -> none
 * save : filename (String) -> none
 * numFrames : none -> int
 * numChannels : none -> int
 */
public class SoundProcessor
{   
// Initializes arrays and an empty WavFile.
WavFile inputwav;
WavFile outputwav = new WavFile();
int[] inputdata;
int[] outputdata;


    
    public  void setup(String path)
    {
        this.inputwav = new WavFile(path);
        this.inputdata= this.inputwav.getMonoData();
    }
    
    public void quieter(String outputpath){
        
        int i= 0;
        while (i < this.inputwav.numFrames()){
            inputdata[i]= inputdata[i] / 2;
            i= i + 1;
        }
        
        outputwav.setData(inputdata);
        outputwav.save(outputpath);
    }
    
    public void combine(String inputwav2_path, String outputpath){
        
        WavFile inputwav2 = new WavFile(inputwav2_path);
        int[] inputdata2= inputwav2.getMonoData();
        int[] inputdata3= new int[20*44100];
        
        int i= 0;
        while (i < 20*44100){
            inputdata3[i]= inputdata[i]+inputdata2[i];
            i++;
        }
        
        outputwav.setData(inputdata3);
        outputwav.save(outputpath);
    }
    
    public void reverse(String outputpath){
        int[] outputdata= new int[this.inputwav.numFrames()];
        int i = 0;
        while(i < this.inputwav.numFrames()){
            outputdata[i] = inputdata[this.inputwav.numFrames()-i-1];
            i++;
        }
        outputwav.setData(outputdata);
        outputwav.save(outputpath);
        
    }
    /**
     * Changes the pitch. Speeds up the sound by a factor of 1.5 by removing every third frame.
     * 
     */
    public void speedUp(String outputpath){
        double variablerate= (2.0/3.0);
        int[] outputdata= new int[(int) (variablerate * inputwav.numFrames())];
        int i= 0;
        int v= 0;
        while (i < (int) (this.inputwav.numFrames() * variablerate)){
           /**
             * Algorithm: Increments a counter for every even iterated.
             * 
             * 
             * Output: 0  1  2  3  4  5  6  7  8  9   10  11  12  13  14 ...
             * Input:  0  1  3  4  6  7  9  10 12 13  15  16  18  19  21 ...
             *         =  =  +1 +1 +2 +2 +3 +3 +4 +4  +5  +5  +6  +6  +7 ...
             *         		      |     |     |      |       |       |
             **/
            if (i % 2 ==0 && i != 0)
                v++;
            outputdata[i]= inputdata[i+v];

            i++;
        }
        
        outputwav.setData(outputdata);
        outputwav.save(outputpath);
    }
    
    public void removeSilence(String outputpath)
    {
        int i = 0;
        int lengthOfSilence = 0;
        int skipCounter = 0;
        
        // Sometimes, silence is not true silence!
        int threshold = 5;
        
        // First, we find out how much silence is in the track so we know the proper size of the output.
        while (i < this.inputwav.numFrames()){
            if (inputdata[i] <= threshold && inputdata[i] >= (-1 * threshold))
                lengthOfSilence++;
            i++;
            }
        int[] outputdata= new int[this.inputwav.numFrames() - lengthOfSilence];
        
        // Reset and reuse the iterator (#TODO there must be a better way..)
        i = 0;
        
        // Stream input to output, skipping spots where volume is inside of threshold of silence.
        // Uses a counter to keep track of the desired position in the output when skipping data points.
        while (i < this.inputwav.numFrames()){
                    if (inputdata[i] > threshold || inputdata[i] < (-1 * threshold))
                        outputdata[i-skipCounter] = inputdata[i];
                    else if (inputdata[i] <= threshold && inputdata[i] >= (-1 * threshold))
                        skipCounter ++;
                    i++;
        }
        
        outputwav.setData(outputdata);
        outputwav.save(outputpath);
    }
    
    public void addEcho(String outputpath)
    {
        /** Adds the current position in the song with a position 'lengthOfEcho' behind it */
        
        
        int i = 0;
        
        // Measured in frames
        int lengthOfEcho = 4800;
        
        int[] outputdata = new int [this.inputwav.numFrames()];
        
        
        while (i < this.inputwav.numFrames())
        {
            if (i >= lengthOfEcho){
                outputdata[i] = (inputdata[i] + inputdata[i - lengthOfEcho]);
            }
            // Necessary, because during the first 'lengthOfEcho' frames, not enough sound has been
            // produced to create an echo.
            else if (i < lengthOfEcho)
                outputdata[i] = inputdata[i];
            i++;
        }
        
        outputwav.setData(outputdata);
        outputwav.save(outputpath);
    }
    
    /**
    * trim : input (WavFile), start (int), end (int) -> WavFile
    * Given a WavFile, the starting frame, and ending frame,
    * return a new WavFile with the piece between start and end
    * extracted.
    * 
    * Sound s= new Sound();
    * WavFile w= new WavFile("C:\\...");
    * WaveFile x= s.trim(w, 100000, 500000);
    * x.numFrames() -> 400000
    * s.save("C:\.....");
    */
    public WavFile trim(WavFile input, int start, int end)
    {
        int[] data= input.getMonoData();
        int[] newdata= new int[end-start+1];
         
        for (int i= 0; i < end-start+1; i++)
        {
            newdata[i]= data[i+start];
        }
         
        WavFile newWave = new WavFile();
        newWave.setData(newdata);
        return newWave;
    }
         
        /**
        * trimFile : infile (String), outfile (String), startTime (double), length (double) -> none
        * Given the input filename, output filename, starting time (seconds), and length (seconds),
        * trim the input file down and save it to the output file.
        * 
        * Sound s= new Sound();
        * s.trimFile("C:\\.......", "C:\\......", 4.5, 3.0);
        */
    public void trimFile(String inFileName, String outFileName, double startTime, double length)
    {
        WavFile inputwav = new WavFile(inFileName);
        
        int beginFrame = (int) (startTime * 44100);
        int endFrame = (int) (beginFrame + (length * 44100));
        
        //int[] inputdata = inputwav.getMonoData();
        
        // 0 < begin < end < upper bound
        if (0 < beginFrame && beginFrame < endFrame && endFrame < inputwav.numFrames())
        {
            WavFile outputwav = trim(inputwav, beginFrame, endFrame);
            outputwav.save(outFileName);
        }
    }
    
}