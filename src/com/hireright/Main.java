package com.hireright;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.*;


public class Main {

    public static void main(String[] args) {

        String sel = " ";

        List<XML> xmlList = new ArrayList<>(); //List contains classes of XML type, to store and analyse multiple XML at once.

        while (!sel.equals("exit"))   //Main menu, will cycle through options until exit flag
        {
            Scanner input = new Scanner(System.in);
            clearScreen();
            System.out.print("\t MAIN MENU \n Type 1 to add new XML and XSD/DTD file path \n Type 2 to validate XML file \n" +
                    " Type 3 to print number of elements in XML file \n Type exit to quit the application\n ---> ");
            sel = input.nextLine();

            switch (sel.toLowerCase())
            {
                case "exit":
                    System.out.println("Goodbye! ");
                    break;
                case "1":
                    Scanner Path = new Scanner(System.in);
                    clearScreen();
                    System.out.println("Please type the location of the XML file: ");
                    String path = Path.nextLine();
                    boolean xmlExists = findFile(path);
                    if (!xmlExists)
                       break;
                    clearScreen();

                    System.out.println("Please type the location of the DTD/XSD file: ");
                    Path = new Scanner(System.in);
                    String dtdPath = Path.nextLine();
                    System.out.println(dtdPath);
                    boolean dtdExists = findFile(dtdPath);
                    if (!dtdExists)
                        break;


                    if(determineType(dtdPath) != -1)    //Checks to see if file exists to be added
                    {
                        xmlList.add(new XML(path,dtdPath,determineType(dtdPath))); //if file was found, instantiate class and append to xmlList
                    }
                    else {
                        System.out.println("File supplied is not a DTD nor XSD");
                        waitInput();
                    }
                    break;


                case "2":
                    int choice = chooseFile(xmlList);
                    if(choice == -1)
                        break;

                    clearScreen();
                    if (xmlList.get(choice).validator() == 1)
                        System.out.println("This file is valid");
                    else
                        System.out.println("This file is invalid!");
                    waitInput();
                    break;


                case "3":
                    clearScreen();
                    int choice2 = chooseFile(xmlList);
                    if(choice2 == -1)
                        break;
                    clearScreen();
                    System.out.println("Please type the name of the element to be counted: ");
                    Scanner element = new Scanner(System.in);
                    int count = xmlList.get(choice2).countElements(element.nextLine());
                    if (count == -1)
                        System.out.println("Could not find the element specified");
                    else
                        System.out.println("There are " + count + " elements");
                    waitInput();
                    break;


                default:
                    System.out.println("Invalid option, try again!\n\n");
                    waitInput();

            }
        }
    }

    // Will select file in list of saved xml's
    public static int chooseFile(List<XML> xmlList)
    {
        System.out.println("Please select which XML file: ");
        int i = 0;
        //Cycles throgh the xmlList containing all XML classes instantiated
        for(XML xml : xmlList)
        {
            System.out.println(i+1 + " - " + xml.path + " with file " + xml.dtdpath);
            i++;
        }
        System.out.println(" --> ");
        //chooses one
        Scanner sel = new Scanner(System.in);
        int choice = sel.nextInt();
        if (choice > xmlList.size() || choice < 1)
        {
            System.out.println("Invalid option, try again");
            waitInput();
            return -1;
        }
        return choice-1;
    }

    public static int determineType(String Path)
    {
        if (Path.endsWith(".xsd"))
            return 1;
        else if (Path.endsWith(".dtd"))
            return 0;
        else
            return -1;
    }

    public static boolean findFile(String Path)
    {
        String pathDivided[] = Path.split("\\\\");
        String name = pathDivided[pathDivided.length-1];
        pathDivided = Arrays.copyOf(pathDivided,pathDivided.length-1);

        String dirPath = concatenate(pathDivided);

        File file = new File(dirPath);
        File[] list = file.listFiles();
        if(list!=null)
            for (File fil : list)
            {
                if (fil.isDirectory())
                {
                    String recurseString = new String(dirPath + "/" + name);
                    findFile(recurseString);
                }
                else if (name.equalsIgnoreCase(fil.getName()))
                {
                    System.out.println("File found and added successfully!");  //+fil.getParentFile()
                    waitInput();
                    return true;
                }
            }
            System.out.println("Could not find the file, please try again");
            return false;
    }

    private static String concatenate(String[] pathdivided)
    {
        StringBuilder strBuilder = new StringBuilder();
        for (String a : pathdivided) {
            strBuilder.append(a + "\\");
        }
        return strBuilder.toString();
    }


    private static void clearScreen()
    {
        for(int i = 0; i < 50; i++)
            System.out.println();
    }

    private static void waitInput()
    {
        System.out.println("Press any key and Enter to continue...");
        Scanner wait = new Scanner(System.in); //Wait for user to press any key to continue
        wait.next();
    }
}


