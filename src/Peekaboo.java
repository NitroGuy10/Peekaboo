import sun.security.util.SecurityConstants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;

public class Peekaboo
{
    // peekaboo [-o output_file [-v]] [-s screen_number] [-w]
    public static final String HELP_STRING = "peekaboo [-o output_file] [-v] [-w]\n" +
            "-o output_file   .... Output an image file to the specified path (WILL ALWAYS OVERWRITE)\n" +
            "-v               .... Open the resultant image file in the default image viewer\n" +
            "-s screen_number .... Specify the screen to capture (for multiple monitors etc.)\n" +
            "-w               .... Open a temporary GUI that displays the image";

    public static void main (String[] args)
    {
        ///////// Check for an unusable environment /////////
        if (GraphicsEnvironment.isHeadless())
        {
            System.out.println("The current environment is headless and, thus, a screenshot cannot be taken.");
            System.exit(-2);
        }
        // There is probably a better way to do this
        try
        {
            new Robot();
        }
        catch (SecurityException e)
        {
            System.out.println("\"createRobot\" permission is not granted");
            System.exit(-2);
        }
        catch (AWTException e)
        {
            System.out.println("The environment does not allow low-level input control (AWTException)");
            System.exit(-2);
        }

        ///////// Parse arguments /////////
        boolean doOutputFile = false;
        boolean doImageViewer = false;
        boolean doGUIWindow = false;
        int screen_number = -1;  // -1 == Default screen
        File outputFile = null;

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));

        // Output file flag
        if (arguments.contains("-o"))
        {
            if (arguments.indexOf("-o") + 1 < arguments.size())
            {
                outputFile = new File(arguments.get(arguments.indexOf("-o") + 1));

                arguments.remove(arguments.indexOf("-o") + 1);
                arguments.remove("-o");

                doOutputFile = true;

                // Image viewer flag
                if (arguments.contains("-v"))
                {
                    arguments.remove("-v");
                    doImageViewer = true;
                }
            }
            else
            {
                System.out.println("Please specify output file");
                System.out.println(HELP_STRING);
                System.exit(-1);
            }
        }

        // Notify incorrect usage of -v
        if (arguments.contains("-v"))
        {
            arguments.remove("-v");
            System.out.println("[-v] cannot be used unless [-o output_file] is also used");
        }

        // Screen number flag
        if (arguments.contains("-s"))
        {
            if (arguments.indexOf("-s") + 1 < arguments.size())
            {
                String potential_screen_number = arguments.get(arguments.indexOf("-s") + 1);
                arguments.remove(arguments.indexOf("-s") + 1);
                arguments.remove("-s");

                try
                {
                    Integer.parseInt(potential_screen_number);
                }
                catch (NumberFormatException e)
                {
                    System.out.println(potential_screen_number + " is not a valid integer");
                    System.out.println(HELP_STRING);
                    System.exit(-1);
                }

                screen_number = Integer.parseInt(potential_screen_number);

                if (screen_number < 0)
                {
                    System.out.println("Screen number cannot be negative");
                    System.out.println(HELP_STRING);
                    System.exit(-1);
                }
            }
            else
            {
                System.out.println("Please specify screen number");
                System.out.println(HELP_STRING);
                System.exit(-1);
            }
        }

        // GUI window flag
        if (arguments.contains("-w"))
        {
            arguments.remove("-w");
            doGUIWindow = true;
        }

        if (arguments.size() != 0)
        {
            System.out.println("Unknown arguments:");
            for (String arg: arguments)
            {
                System.out.println(arg);
            }
            System.out.println(HELP_STRING);
            System.exit(-1);
        }

        if (!doOutputFile && !doImageViewer && !doGUIWindow)
        {
            System.out.println(HELP_STRING);
        }
        else
        {
            ///////// Take the screenshot /////////
            System.out.println("Peekaboo!");

            Robot robot = null;
            GraphicsDevice screenDevice = null;
            if (screen_number == -1)
            {
                // If the program has gotten this far, this should not throw an exception
                screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                try { robot = new Robot(); } catch (AWTException e) { e.printStackTrace(); System.exit(1);}
            }
            else if (screen_number >= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length)
            {
                System.out.println("Screen number " + screen_number + " does not exist");
                System.exit(1);
            }
            else
            {
                try
                {
                    screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen_number];
                    robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen_number]);
                }
                catch (IllegalArgumentException e)
                {
                    System.out.println("Screen number " + screen_number + " exists but is not a valid screen");
                    System.exit(1);
                }
                // If the program has gotten this far, this exception should not occur
                catch (AWTException e)
                {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            // Take a screenshot
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(
                    screenDevice.getDisplayMode().getWidth(),
                    screenDevice.getDisplayMode().getHeight()
            ));

            if (doOutputFile)
            {
                String outputFileSuffix = outputFile.getName().split("\\.")[outputFile.getName().split("\\.").length - 1];
                try
                {
                    if (Arrays.asList(ImageIO.getWriterFileSuffixes()).contains(outputFileSuffix))
                    {
                        ImageIO.write(screenshot, outputFileSuffix, outputFile);
                        System.out.println("Image file successfully created!");
                        System.out.println(outputFile);
                    }
                    else
                    {
                        System.out.println("No image writer available for format \"" + outputFileSuffix + "\"");
                        System.exit(1);
                    }
                }
                catch (IOException e)
                {
                    System.out.println("Unable to write image file");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}