package wavtools;
import wav.*;

/**
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
    
    public void speedUp(double variableRate) {
    	
        int[][] outputData= new int[inputData.length][(int) (inputWav.numFrames() / variableRate)];
        
        for (int i= 0; i < (int) (Math.floor((this.inputWav.numFrames() / variableRate))); i++) {
        	for (int t=0; t < inputData.length; t++) {
        		
        	if (variableRate >= 1) {
        		outputData[t][i]= inputData[t][(int)(Math.ceil(i * variableRate))];
        	}
        	if (variableRate < 1) {
        		if (i==0) {
        			outputData[t][i] = inputData[t][0]; }
        		else {
        			outputData[t][i]= inputData[t][(int)(Math.ceil(i * variableRate)) - 1];
        		}
        	}
            // Round up (i * variableRate) to obtain reference frame
        	// Choosing between floor and ceil for these calculations is largely a matter
        	// of preference; its only practical effect determines which frames are duplicated
        	// For example, when slowing down a file, frames must be duplicated to increase
        	// the length and information of the output.
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
        /** Combines the current position in the song with a position 'lengthOfEcho' behind it */
        
        
        
        
        // Conversion to frames
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
    * trim : start (int), end (int) -> none
    * Given the starting time (second) and ending time (seconds) of the desired clip,
    * trim the working wav file down to be within these parameters.
    * 
    */
    private void trim(int start, int end)
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
    * trimFile : startTime (double), length (double) -> none
    * Given the starting time (seconds), and length (seconds),
    * trim the input file to be within the new beginning and 
    * end times.
    * 
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