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
int[][] inputData;
int[][] outputData;


    
    public  void setup()
    {
        this.inputWav = new WavFile(inputPath);
        this.inputData= inputWav.getAllData();
        this.outputWav = new WavFile();
    }
    
    public void quieter(double factor){
        
        for (int i= 0; i < this.inputWav.numFrames(); i++)
        	for (int t=0; t < inputData.length; t++)
        		inputData[t][i]= (int) (inputData[t][i] / (100/factor));
        
        outputWav.setData(inputData);
    }
    
    public void combine(String inputWav2_path){
    	// #TODO Write isLower method (Given any number of inputs, determine the lowest value)
    	
        
        WavFile inputWav2 = new WavFile(inputWav2_path);
        int[][] inputData2= inputWav2.getAllData();
        
        int maxFrames = 0;
        int maxChannels = 0;
        
        if (inputData.length >= inputData2.length) {
        	maxChannels = inputData2.length;
    	}
        else if (inputData2.length > inputData.length) {
        	maxChannels = inputData.length;
		}
        
        if (inputData[0].length >= inputData2[0].length) {
        	maxFrames = inputData2[0].length;
        }
        else if (inputData2[0].length > inputData[0].length) {
        	maxFrames = inputData[0].length;
        }

        int[][] combinedOutputData= new int [maxChannels] [maxFrames];
        
        for (int i= 0; i < maxFrames; i++)
        	for (int t=0; t < maxChannels; t++)
        		combinedOutputData[t][i]= inputData[t][i]+inputData2[t][i];
        
        outputWav.setData(combinedOutputData);
    }
    
    public void reverse() {
        int[][] wavOutputData= new int[inputData.length][inputWav.numFrames()];
        
        for (int i= 0; i < inputWav.numFrames(); i++)
        	for (int t=0; t < inputData.length; t++)
        		wavOutputData[t][i] = inputData[t][inputWav.numFrames()-i-1];
        
        outputWav.setData(wavOutputData);
        
    }
    /**
     * Changes the pitch. Speeds up the sound by a factor of 1.5 by removing every third frame.
     * #TODO Figure out an algorithm to allow variable speeds.
     */
    public void speedUp(double variableRate) {
    	// #TODO Write good rounding method. That would probably make this a lot easier.
        int[][] outputData= new int[inputData.length][(int) (variableRate * inputWav.numFrames())];
        int v= 0;
        for (int i= 0; i < (int) (this.inputWav.numFrames() / variableRate); i++) {
        	for (int t=0; t < inputData.length; t++) {
            /**
             * Algorithm: Increments a counter for every even iterated.
             * 
             * 
             * Output: 0  1  2  3  4  5  6  7  8  9   10  11  12  13  14 ...
             * Input:  0  1  3  4  6  7  9  10 12 13  15  16  18  19  21 ...
             *         =  = +1 +1 +2 +2 +3  +3 +4 +4  +5  +5  +6  +6  +7 ...
             *         		      |     |     |      |       |       |
             **/
        		if (variableRate > 1) {
    	            outputData[t][i]= inputData[t][i+v];
        		}
        		else if (variableRate < 1) {
    	            outputData[t][i]= inputData[t][i-v];
        		}
	            if (inputData[t][i]/outputData[t][i] == variableRate)
	                v++;
	            // Round up (i * variableRate) to obtain reference frame
        	}
        }
        
        outputWav.setData(outputData);
    }

    public void removeSilence(int threshold) {
        
        int lengthOfSilence = 0;
        int skipCounter = 0;
        
        // Sometimes, silence is not true silence!
        
        // First, we find out how much silence is in the track so we know the proper size of the output.
        for (int i = 0; i < this.inputWav.numFrames(); i++) {
        	for (int t=0; t < inputData.length; t++) {
	            if (inputData[t][i] <= threshold && inputData[t][i] >= (-1 * threshold))
	                lengthOfSilence++;
	            	// Adds to the counter, however this reflects *double* the true length
	            	// in a stereo file, as the silence is counted once for each channel.
            }
        }
        lengthOfSilence = lengthOfSilence/inputData.length;
        // Divide by number of channels to get true length
        
        int[][] outputData= new int[inputData.length][this.inputWav.numFrames() - lengthOfSilence];
        
        
        // Stream input to output, skipping spots of the input where the volume is within the threshold of silence.
        // i is used to iterate through the input, while skipCounter is used to keep track of the last written spot
        // in the output wav (used to resume writing after silence)
        for (int i= 0; i < inputWav.numFrames(); i++) {
        	for (int t=0; t < inputData.length; t++) {
        		
                    if (inputData[t][i] > threshold || inputData[t][i] < (-1 * threshold)) //Outside
                        outputData[t][i-skipCounter] = inputData[t][i];
                    
                    else if (inputData[t][i] <= threshold && inputData[t][i] >= (-1 * threshold)) //Within
                        skipCounter ++;
        	}
        }
        
        outputWav.setData(outputData);
    }
    
    public void addEcho(double echoLength)
    {
        /** Adds the current position in the song with a position 'lengthOfEcho' behind it */
        
        
        
        
        // Measured in frames
        // int lengthOfEcho = 4800;
        int lengthOfEcho = (int) (echoLength * 44100);
        int[][] tempOutputData = new int [inputData.length][this.inputWav.numFrames()];
        
        
        for (int i = 0; i < inputWav.numFrames(); i++) {
        	for (int t=0; t < inputData.length; t++) {
        		
            if (i >= lengthOfEcho){
                tempOutputData[t][i] = (inputData[t][i] + inputData[t][i - lengthOfEcho]);
            }
            // Necessary, because during the first 'lengthOfEcho' frames, not enough sound has been
            // produced to create an echo.
            else if (i < lengthOfEcho)
                tempOutputData[t][i] = inputData[t][i];
        	}
        }
        
        outputWav.setData(tempOutputData);
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
    public void trim(int start, int end)
    {
        int[][] newData= new int[inputData.length][end-start+1];
         
        for (int i= 0; i < end-start+1; i++) {
        	for (int t=0; t < inputData.length; t++) {

        		newData[t][i]= inputData[t][i+start];
        	}
        }
         
        outputWav.setData(newData);
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
            trim(beginFrame, endFrame);
            return true;
        }
        
        else {
        	System.out.println("Input not within bounds, please try again.");
    		return false;
        }
        
    }
    


}