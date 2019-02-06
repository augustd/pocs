package com.codemagi.pocs;

import com.sun.management.UnixOperatingSystemMXBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Test if FileInputStream is closed when it is reassigned. Loads several files,
 * re-using the same FileInputStream, and prints out the number of open file
 * descriptors on the OS.
 *
 * Outcome: Proves the contention that reassigning a FileInputStream does not
 * close the file handles unless close() is explicitly called. 
 *
 * @author augustd
 */
public class FileInputStreamReassign {

    //some files to load. These should be available on any unix-y system.
    private static final String[] FILES = {"/etc/passwd", "/etc/hosts", "/etc/group", "/etc/resolv.conf"};

    public static void main(String[] args) throws FileNotFoundException, IOException {
        printFileDescriptorCount();

        FileInputStream fis = null;
        for (String filename : FILES) {
            System.out.println("Loading file: " + filename);
            fis = new FileInputStream(new File(filename));

            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                //System.out.println(new String(buf));
            }
            printFileDescriptorCount();
        }

        System.out.println("calling fis.close()");
        fis.close();
        printFileDescriptorCount();

    }

    public static void printFileDescriptorCount() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof UnixOperatingSystemMXBean) {
            System.out.println("Number of open fd: " + ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount());
        }
    }

}
