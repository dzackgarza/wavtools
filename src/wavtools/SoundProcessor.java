package wavtools;
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
WavFile inputWav;
WavFile outputWav = new WavFile();
String inputPath;
String outputPath;
int[] inputData;
int[] outputData;


    
    public  void setup()
    {
        this.inputWav = new WavFile(inputPath);
        this.inputData= inputWav.getMonoData();
        this.outputWav = new WavFile();
    }
    
    public void quieter(double factor){
        
        int i= 0;
        while (i < this.inputWav.numFrames()){
            inputData[i]= inputData[i] / (int) (1/factor);
            i= i + 1;
        }
        
        outputWav.setData(inputData);
    }
    
    public void combine(String inputWav2_path){
        
        WavFile inputWav2 = new WavFile(inputWav2_path);
        int[] inputData2= inputWav2.getMonoData();
        int[] inputData3= new int[20*44100];
        
        int i= 0;
        while (i < 20*44100){
            inputData3[i]= inputData[i]+inputData2[i];
            i++;
        }
        
        outputWav.setData(inputData3);
    }
    
    public void reverse(){
        int[] outputData= new int[this.inputWav.numFrames()];
        int i = 0;
        while(i < this.inputWav.numFrames()){
            outputData[i] = inputData[this.inputWav.numFrames()-i-1];
            i++;
        }
        outputWav.setData(outputData);
        
    }
    /**
     * Changes the pitch. Speeds up the sound by a factor of 1.5 by removing every third frame.
     * 
     */
    public void speedUp(double variableRate){
        variableRate= (1.0/variableRate);
        int[] outputData= new int[(int) (variableRate * inputWav.numFrames())];
        int i= 0;
        int v= 0;
        while (i < (int) (this.inputWav.numFrames() * variableRate)){
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
            outputData[i]= inputData[i+v];

            i++;
        }
        
        outputWav.setData(outputData);
    }
    
    public void removeSilence(int threshold) {
        int i = 0;
        int lengthOfSilence = 0;
        int skipCounter = 0;
        
        // Sometimes, silence is not true silence!
        
        // First, we find out how much silence is in the track so we know the proper size of the output.
        while (i < this.inputWav.numFrames()){
            if (inputData[i] <= threshold && inputData[i] >= (-1 * threshold))
                lengthOfSilence++;
            i++;
            }
        int[] outputData= new int[this.inputWav.numFrames() - lengthOfSilence];
        
        // Reset and reuse the iterator (#TODO there must be a better way..)
        i = 0;
        
        // Stream input to output, skipping spots where volume is inside of threshold of silence.
        // Uses a counter to keep track of the desired position in the output when skipping data points.
        while (i < this.inputWav.numFrames()){
                    if (inputData[i] > threshold || inputData[i] < (-1 * threshold))
                        outputData[i-skipCounter] = inputData[i];
                    else if (inputData[i] <= threshold && inputData[i] >= (-1 * threshold))
                        skipCounter ++;
                    i++;
        }
        
        outputWav.setData(outputData);
    }
    
    public void addEcho(double echoLength)
    {
        /** Adds the current position in the song with a position 'lengthOfEcho' behind it */
        
        
        int i = 0;
        
        // Measured in frames
        // int lengthOfEcho = 4800;
        int lengthOfEcho = (int) (echoLength * 44100);
        int[] outputData = new int [this.inputWav.numFrames()];
        
        
        while (i < this.inputWav.numFrames())
        {
            if (i >= lengthOfEcho){
                outputData[i] = (inputData[i] + inputData[i - lengthOfEcho]);
            }
            // Necessary, because during the first 'lengthOfEcho' frames, not enough sound has been
            // produced to create an echo.
            else if (i < lengthOfEcho)
                outputData[i] = inputData[i];
            i++;
        }
        
        outputWav.setData(outputData);
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
        int[] newData= new int[end-start+1];
         
        for (int i= 0; i < end-start+1; i++) {
            newData[i]= data[i+start];
        }
         
        WavFile newWave = new WavFile();
        newWave.setData(newData);
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
    
    public boolean trimFile(double startTime, double length)
    {
        
        int beginFrame = (int) (startTime * 44100);
        int endFrame = (int) (beginFrame + (length * 44100));
        
        
        // 0 < begin < end < upper bound
        if (0 < beginFrame && beginFrame < endFrame && endFrame < inputWav.numFrames()) {
            this.outputWav = trim(inputWav, beginFrame, endFrame);
            return true;
        }
        
        else {
        	System.out.println("Input not within bounds, please try again.");
    		return false;
        }
        
    }
    
}