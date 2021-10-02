import java.util.ArrayList;
import java.io.File;

public class Peekaboo
{
    public static void main (String[] args)
    {
        boolean doOutputFile = false;
        boolean doImageViewer = false;
        boolean doGUIWindow = false;

        ArrayList<String> arguments = new ArrayList<>();
        for (String arg : args)
        {
            arguments.add(arg);
        }

        // Output file flag
        if (arguments.contains("-o"))
        {
            if (arguments.indexOf("-o") + 1 < arguments.size())
            {
                File outputFile = new File(arguments.get(arguments.indexOf("-o") + 1));

                arguments.remove(arguments.indexOf("-o") + 1);
                arguments.remove("-o");

                doOutputFile = true;
            }
            else
            {
                System.out.println("Please specify output file");
                System.exit(-1);
            }
        }

        // Image viewer flag
        if (arguments.contains("-v"))
        {
            arguments.remove("-v");
            doImageViewer = true;
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
            printHelp();
            System.exit(-1);
        }

        if (!doOutputFile && !doImageViewer && !doGUIWindow)
        {
            printHelp();
        }
        else
        {
            // DO STUFF!!!
        }
    }

    public static void printHelp ()
    {
        // Print help
        // peekaboo [-o output_file] [-v] [-w]
    }
}
