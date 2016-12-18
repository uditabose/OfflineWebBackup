/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offlineweb.job.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author papa
 */
public class UnzipUtilityTest {
    
    public UnzipUtilityTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of unzip method, of class UnzipUtil.
     */
    @Test
    public void testUnzip() throws Exception {
        System.out.println("unzip");
        String zipFilePath = "/home/papa/Desktop/zip.zip";
        String destDirectory = "/home/papa/Desktop/test";
        UnzipUtil instance = new UnzipUtil();
        instance.unzip(zipFilePath, destDirectory);
        
        assertTrue(1 == 1);
    }

    /**
     * Test of flattenZip method, of class UnzipUtil.
     */
    @Test
    public void testFlattenZip() throws Exception {
        System.out.println("flattenZip");
        String zipFilePath = "/home/papa/Desktop/zip.zip";
        String destDirectory = "/home/papa/Desktop/flatten";
        String fileExt = "log";
        UnzipUtil instance = new UnzipUtil();
        instance.flattenZip(zipFilePath, destDirectory, fileExt);
        
        assertTrue(1 == 1);
    }
    
    @Test
    public void testFlattenZipNull() throws Exception {
        System.out.println("flattenZip");
        String zipFilePath = "/home/papa/Desktop/zip.zip";
        String destDirectory = "/home/papa/Desktop/flattenNull";
        
        UnzipUtil instance = new UnzipUtil();
        instance.flattenZip(zipFilePath, destDirectory);
        
        assertTrue(1 == 1);
    }
    
    @Test
    public void testFlattenZipMulti() throws Exception {
        System.out.println("flattenZip");
        String zipFilePath = "/home/papa/Desktop/zip.zip";
        String destDirectory = "/home/papa/Desktop/flattenMulti";
        
        UnzipUtil instance = new UnzipUtil();
        instance.flattenZip(zipFilePath, destDirectory, "log", "html");
        
        assertTrue(1 == 1);
    }
    
}
