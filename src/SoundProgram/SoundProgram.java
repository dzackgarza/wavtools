import java.util.Scanner;
import java.io.File;

public class SoundProgram
{
	static Scanner keyboardinput = new Scanner(System.in);
    static SoundProcessor s = new SoundProcessor();
    static File f = null;
    static String filename = null;
   
    public static void main (String[] args)
    {
    	SoundProcessor s = new SoundProcessor();
        Boolean moretasks = null;
        
        do
        {    
        	printMenu();
        	
            int decision = Integer.parseInt(getKeyboardInput());
            
            if (1 <= decision && decision <=7) {
                s.setup(getInputFile());
                getOutputFile(decision);
                moretasks = checkContinue();
            }
            
            else if (decision == 8) {
            	moretasks = false;
            }
            
        } while (moretasks);
    }
    
    
    
    public static void printMenu()
    {
	  System.out.println("Please select from the following menu options:");
      System.out.println("1 - Reduce Volume");
      System.out.println("2 - Combine two clips");
      System.out.println("3 - Reverse Audio");
      System.out.println("4 - Increase Speed");
      System.out.println("5 - Remove Silence");
      System.out.println("6 - Add Echo");
      System.out.println("7 - Trim");
      System.out.println("8 - Exit");
    }
    
    public static String getKeyboardInput()
    {
        Scanner keyboardinput = new Scanner(System.in);
        return keyboardinput.next();
    }

    public static String processMenuChoice(int decision, String outputpath)
    {
    	switch (decision) {
	        case 1: s.quieter(outputpath);
	            return "Done!";
	        case 2: System.out.print("Choose a second wave file. ");
	            s.combine(getInputFile(), outputpath);
	            return "Done!";
	        case 3: s.reverse(outputpath);
	        	return "Done!";
	        case 4: s.speedUp(outputpath);
	            return "Done!";
	        case 5: s.removeSilence(outputpath);
	            return "Done!";
	        case 6: s.addEcho(outputpath);
	            return "Done!";
	        case 7: processTrim(filename, outputpath);
	        	return "Done!";
	        case 8:
	        	return "Goodbye!";
	        default: return("Not a valid choice.");
    	}
    }
    
    public static String getInputFile()
    {
    System.out.println("Choose input wav file.");
    
        do
        {
            System.out.print("Enter path to file: ");
            filename = getKeyboardInput();
            f = new File(filename);
            if (!f.canRead())
                System.out.println("File does not exist. Please try again.");
        }
        while (!f.canRead());
        return filename;

    }
    
    public static void getOutputFile(int decision)
    {
    	
            
            boolean overwrite = false;
            String outputpath = null;
            
            do {
            	 System.out.print("Please enter an output filename: ");
                 
                 String overwritedecision = null;
                 
                 
                if (f.canRead()) {
                	
                    System.out.println("File already exists. Would you like to overwrite? (y/n)");
                    Boolean inputvalidity = null; // Primitive boolean can't be null?
                    
                    checkAnswer();
                }
                    
                else if(!f.canRead() || overwritedecision == "y") {
                	outputpath = getKeyboardInput();
                	overwrite = true;
                }
                
            } while (overwrite == false);
            
            
            System.out.println(processMenuChoice(decision,outputpath));
            
            
            
        }
    
    
    public static boolean checkAnswer()

    {
    	do { // Check if the answer is valid
        	
        	overwritedecision = keyboardinput.next(); // Get decision to overwrite
        	
        	if (overwritedecision == "y" || overwritedecision == "n") {
        		inputvalidity = true; // Breaks this loop 
        		
        		if (overwritedecision == "y") {
        			overwrite = true;
        		}
        		
        		else if (overwritedecision == "n") {
        			overwrite = false;
        		}
        		
        	}
        	
        	else {
        		inputvalidity = false;
        		System.out.println("Not a valid response, please try again.");
        	} // Loops back to get a valid response
        	
    	} while (inputvalidity == false);
    	return overwrite
    }
    
    public static boolean checkContinue()
    {
    	System.out.println("Perform another task? (y/n)");
    	String anothertask = getKeyboardInput();
    	
    	if (anothertask == "y") {
    		return true;
    	}
    	else {
    		return false;
    	}
    
    
    }
    public static void processTrim(String input, String output)
    {
        
        System.out.print("When to start trimming? ");
        String start = keyboardinput.next();
        double startTime = Double.parseDouble(start);
        
        System.out.print("How long? ");
        String length = keyboardinput.next();
        double lengthTime = Double.parseDouble(length);
        
        s.trimFile(input, output, startTime, lengthTime);
        System.out.println("Done!");
    }

}
