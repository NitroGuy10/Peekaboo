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
    // java peekaboo [-o output_file [-v]] [-w] [-nc]
    public static final String HELP_STRING = "java peekaboo [-o output_file [-v]] [-w] [-nc]\n" +
            "-o output_file   .... Output an image file to the specified path (WILL ALWAYS OVERWRITE)\n" +
            "-v               .... Open the resultant image file in the default image viewer\n" +
            "-w               .... Open a temporary GUI that displays the image\n" +
            "-nc              .... Do not attempt to crop the screenshot (see README for more details)";

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
        boolean doCrop = true;
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

        // GUI window flag
        if (arguments.contains("-w"))
        {
            arguments.remove("-w");
            doGUIWindow = true;
        }

        // "No Crop" flag
        if (arguments.contains("-nc"))
        {
            arguments.remove("-nc");
            doCrop = false;
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

            // If the program has gotten this far, this should not throw an exception
            try { robot = new Robot(); } catch (AWTException e) { e.printStackTrace(); System.exit(1);}

            // Make a wild guess about how big the screenshot should be
            int totalWidth = 0;
            int totalHeight = 0;
            for (GraphicsDevice screen : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
            {
                totalWidth += screen.getDisplayMode().getWidth();
                totalHeight += screen.getDisplayMode().getHeight();
            }

            // Take a screenshot
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(-totalWidth, -totalHeight, totalWidth * 2, totalHeight * 2));
            System.out.println("Screenshot taken!");

            // Adjust the size of the screenshot to remove as much of the black void as possible
            if (doCrop)
            {
                System.out.println("Cropping...");
                int leftBound = -1;
                int rightBound = -1;
                int topBound = -1;
                int bottomBound = -1;

                // TODO I bet you could do a binary search here instead
                // Find left bound
                for (int x = 0; x < screenshot.getWidth(); x++)
                {
                    for (int y = 0; y < screenshot.getHeight(); y++)
                    {
                        if (screenshot.getRGB(x, y) != -16777216)  // -16777216 is the RGB of the color black
                        {
                            leftBound = x;
                            break;
                        }
                    }
                    if (leftBound != -1)
                    {
                        break;
                    }
                }
                // Find right bound
                for (int x = screenshot.getWidth() - 1; x >= 0; x--)
                {
                    for (int y = 0; y < screenshot.getHeight(); y++)
                    {
                        if (screenshot.getRGB(x, y) != -16777216)
                        {
                            rightBound = x;
                            break;
                        }
                    }
                    if (rightBound != -1)
                    {
                        break;
                    }
                }
                // Find top bound
                for (int y = 0; y < screenshot.getHeight(); y++)
                {
                    for (int x = leftBound; x <= rightBound; x++)
                    {
                        if (screenshot.getRGB(x, y) != -16777216)
                        {
                            topBound = y;
                            break;
                        }
                    }
                    if (topBound != -1)
                    {
                        break;
                    }
                }
                // Find bottom bound
                for (int y = screenshot.getHeight() - 1; y >= 0; y--)
                {
                    for (int x = leftBound; x <= rightBound; x++)
                    {
                        if (screenshot.getRGB(x, y) != -16777216)
                        {
                            bottomBound = y;
                            break;
                        }
                    }
                    if (bottomBound != -1)
                    {
                        break;
                    }
                }

                if (leftBound == -1 && rightBound == -1 && topBound == -1 && bottomBound == -1)
                {
                    System.out.println("Just thought I should let you know that your screenshot is entirely black :/");
                }
                else
                {
                    screenshot = screenshot.getSubimage(leftBound, topBound, rightBound - leftBound, bottomBound - topBound);
                }
            }

            ///////// Output a file /////////
            if (doOutputFile)
            {
                String outputFileSuffix = outputFile.getName().split("\\.")[outputFile.getName().split("\\.").length - 1];
                try
                {
                    if (Arrays.asList(ImageIO.getWriterFileSuffixes()).contains(outputFileSuffix))
                    {
                        System.out.println("Writing image file...");
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