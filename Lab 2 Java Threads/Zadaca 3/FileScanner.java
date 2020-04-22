package mk.ukim.finki.os.lab2.starter_codes;
import java.io.*;
import java.io.File;


public class FileScanner  {

    private String fileToScan;
    //TODO: Initialize the start value of the counter
    private static Long counter;

    public FileScanner (String fileToScan) {
        this.fileToScan=fileToScan;
        //TODO: Increment the counter on every creation of FileScanner object
    }

    public static void printInfo(File file)  {

        /*
        * TODO: Print the info for the @argument File file, according to the requirement of the task
        * */

    }

    public static Long getCounter () {
        return counter;
    }


    public void run() {

        //TODO Create object File with the absolute path fileToScan.
        File file;

        //TODO Create a list of all the files that are in the directory file.
        File [] files = null;


        for (File f : files) {

            /*
            * TODO If the File f is not a directory, print its info using the function printInfo(f)
            * */

            /*
            * TODO If the File f is a directory, create a thread from type FileScanner and start it.
            * */

            //TODO: wait for all the FileScanner-s to finish
        }

    }

    public static void main (String [] args) {
        String FILE_TO_SCAN = "C:\\Users\\189075\\Desktop\\lab";

        //TODO Construct a FileScanner object with the fileToScan = FILE_TO_SCAN
        FileScanner fileScanner;

        //TODO Start the thread from type FileScanner

        //TODO wait for the fileScanner to finish

        //TODO print a message that displays the number of thread that were created


    }
}

