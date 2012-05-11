package wavtools;
import java.util.Scanner;
import java.io.File;

/*
 * #TODO
 * 		Sort out whether statics are necessary
 * 		Redundant declarations - SCanner, SoundProcessor, etc.
 * 		Accept command line arguments
 * 		Ability to switch files from main menu
 */

public class Main {
	
	static Scanner keyboardinput = new Scanner(System.in);
    static SoundProcessor s;
   
    public static void main (String[] args) {
    	System.out.println("wavtools");
    	
    	s = new SoundProcessor();
        Boolean moreTasks = false;
        boolean runOnce = false;
        
        do {
        	
	        if (!runOnce) {
	    		promptForPathAndSetup();
	            runOnce = true;  
	    	}
	    	  
	    	else if (runOnce) {
	    		
				System.out.println("Would you like to continue working on the same file? (y/n)");
				boolean continueWorking = getYesOrNo();
				
				if (continueWorking) {
					// Use the input as the output to continue modifying
					// the same file.
					s.inputData = s.outputWav.getAllData();
					s.inputWav = s.outputWav;
					
	    		}     
				
				else if (!continueWorking) {
					s.outputWav.save(s.outputPath);
					System.out.println(s.outputPath + " saved.");
					promptForPathAndSetup();
				}
	    	}
	        
			printMenu();
	        int decision = Integer.parseInt(getKeyboardInput());
	    	System.out.println(processMenuChoice(decision));           	        	
	            
	        if (decision == 9) {
	        	moreTasks = false;
	        }
	        
	        else if (1 <= decision && decision <=8) {
	        	System.out.println("Perform another task? (y/n)");
	        	moreTasks = getYesOrNo();
	        }
	            
        } while (moreTasks);
        
        s.outputWav.save(s.outputPath);
        
        System.out.println("File saved at " + s.outputPath);
        System.out.println("Goodbye!");
    }
    public static void promptForPathAndSetup() {
    	/*
    	 * Gets and sets the input and output paths and sets the wav files
    	 * being worked on appropriately.
    	 */
    	getInputFile();
		getOutputFile();
		s.setup();
    }
    public static void printMenu() {
    
	  System.out.println("Please select from the following effects:");
      System.out.println("1 - Reduce Volume");
      System.out.println("2 - Combine two clips");
      System.out.println("3 - Reverse Audio");
      System.out.println("4 - Increase Speed");
      System.out.println("5 - Remove Silence");
      System.out.println("6 - Add Echo");
      System.out.println("7 - Trim");
      System.out.println("8 - Save");
      System.out.println("9 - Exit");
      
      
    }
    
    public static String getKeyboardInput() {
    /*
     * Returns a string containing keyboard input.
     */
    
        Scanner keyboardinput = new Scanner(System.in);
        return keyboardinput.next();
    }

    public static String processMenuChoice(int decision) {
    /*
     * Given the decision from the main menu, the path of an input file, and the path of an output
     * file, processes the choice and returns a confirmation string.
     */
    
    	
    	switch (decision) {
	        case 1: System.out.println("Enter desired output volume as an integer percentage of input volume. (For example, '10' returns " +
	        		"a file that is 10% of the original volume. ");
	        	s.quieter(Integer.parseInt(getKeyboardInput()));
            	return "Done!";
	        case 2: System.out.print("Choose a second wave file. ");
        		s.combine(getInputFile());
        		return "Done!";
	        case 3: s.reverse();
        		return "Done!";
	        case 4: System.out.println("Enter desired speed of output in relation to input. (For example, \"1.5\" returns " +
	        		"a file that is 1.5 times the speed of the original; \".75\" returns a file that is 3/4 the speed.");
	        	s.speedUp(Double.parseDouble(getKeyboardInput()));
            	return "Done!";
	        case 5: System.out.println("Enter an integer volume threshold. Everyhing under this will be considered \"silence\" and will be cut. " +
	        		"Reccommended value: ~5.");
	        	s.removeSilence(Integer.parseInt(getKeyboardInput()));
            	return "Done!";
	        case 6: 
    			System.out.println("Enter length of echo in seconds.");
    			s.addEcho( Double.parseDouble(getKeyboardInput() )   );
            	return "Done!";
	        case 7: processTrim();
        		return "Done!";
	        case 8: s.outputWav.save(s.outputPath);
	        	return "File saved to " + s.outputPath;
	      
	        case 9:
        		return "Exiting...";
	        default: 
        		return("Not a valid choice.");
    	}
    }
    
    public static String getInputFile() {
    /*
     * Prompts the user for an input file, and does not complete unless an existing 
     * file is entered. Returns a string containing the path.
     */
    
    System.out.println("Choose input wav file to work with.");
    String inputFilePath = null;
    File inputFile = null;
    
        do
        {
            System.out.print("Enter path to file: ");
            inputFilePath = getKeyboardInput();
            inputFile = new File(inputFilePath);
            if (!inputFile.canRead()) {
                System.out.println("File does not exist. Please try again.");
            }
        }
        while (!inputFile.canRead());
        s.inputPath = inputFilePath;
        return inputFilePath;

    }
    
    public static String getOutputFile() {
    /**
     * Prompts the user for an output file and, if it already exists, asks whether or not to overwrite.
     * After obtaining a valid input - to either create a new file or overwrite an old one - returns a 
     * string containing the path.
     */  
    
	    boolean overwrite = false;
	    String outputPath = null;
	    
	    do {
	    	 System.out.print("Please enter an output filename: ");
	    	 outputPath = getKeyboardInput();
	    	 File outputFile = new File(outputPath);
	    	 
	        if (outputFile.canRead()) {
	        	
	            System.out.println("File already exists. Would you like to overwrite? (y/n)");                    
	            overwrite = getYesOrNo();
	        }
	            
	        else if(!outputFile.canRead()) {
	        	overwrite = true;
	        }
	        
	    } while (overwrite == false);       
	    s.outputPath = outputPath;
	    return outputPath;
	    
        }
    
    
    public static boolean getYesOrNo() {
    /*
     * Prompts the user for a yes or no input. Only completes once the user has 
     * entered a properly formatted input. Returns a boolean denoting their choice.
     */

	    Boolean inputValidity = null;
	    Boolean YesResponse = null;    
    
    	do { 
    		// Check if the answer is valid
        	
    		String inputToCheck = getKeyboardInput();
    		inputToCheck = inputToCheck.intern();
	    		// Contents of string are not known until runtime, so it must be interned
	    		// in order to use the '==' operator.
	    		
        	if (inputToCheck == "y"
    			|| inputToCheck == "Y"
    			|| inputToCheck == "n" 
    			|| inputToCheck == "N") {
        		// Input is valid, breaks this loop 
        		
        		inputValidity = true; 

        		if (inputToCheck == "y" || inputToCheck == "Y") {
        			YesResponse = true;
        		}
        		
        		else if (inputToCheck == "n" || inputToCheck == "N") {
        			YesResponse = false;
        		}
        		
        	}
        	
        	else { 
        		// Loops back to get a valid response
        		
        		inputValidity = false;
        		System.out.println("Not a valid response, please try again.");
        	} 
        	
        		
    	} while (inputValidity == false);
    	
    	return YesResponse;
    }
    
    
    
    
    public static void processTrim() {
    
    	double startTime;
    	double lengthTime;
    	do {
	        System.out.print("When to start trimming? ");
	        String start = getKeyboardInput();
	        startTime = Double.parseDouble(start);
	
	        System.out.print("How long? ");
	        String length = getKeyboardInput();
	        lengthTime = Double.parseDouble(length);
	        
    	} while (!s.trimFile(startTime, lengthTime));
    }


}
