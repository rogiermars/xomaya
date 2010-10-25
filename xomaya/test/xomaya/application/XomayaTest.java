/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.application;

import javax.swing.JFrame;
import xomaya.controllers.Controller;
import javax.media.ControllerEvent;
import javax.media.DataSink;
import javax.media.MediaLocator;
import javax.media.Processor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sean
 */
public class XomayaTest {

    public XomayaTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of open method, of class Xomaya.
     */
    @Test
    public void testOpen() {
        System.out.println("open");
        MediaLocator ml = null;
        Application app = new Application();
        JFrame f = new JFrame();
        Controller controller = new Controller(f);
        Xomaya instance = new Xomaya(controller);
        
        boolean expResult = true;
        //boolean result = instance.open(ml);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of addNotify method, of class Xomaya.
     */
    @Test
    public void testAddNotify() {
        System.out.println("addNotify");
        Xomaya instance = null;
        //instance.addNotify();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of waitForState method, of class Xomaya.
     */
    @Test
    public void testWaitForState() {
        System.out.println("waitForState");
        int state = 0;
        Xomaya instance = null;
        boolean expResult = false;
        //boolean result = instance.waitForState(state);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of createDataSink method, of class Xomaya.
     */
    @Test
    public void testCreateDataSink() {
        System.out.println("createDataSink");
        Processor p = null;
        MediaLocator outML = null;
        Xomaya instance = null;
        DataSink expResult = null;
        //DataSink result = instance.createDataSink(p, outML);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of controllerUpdate method, of class Xomaya.
     */
    @Test
    public void testControllerUpdate() {
        System.out.println("controllerUpdate");
        //ControllerEvent evt = null;
        //Xomaya instance = null;
        //instance.controllerUpdate(evt);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class Xomaya.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        MediaLocator ml = null;
        Application app = new Application();
        JFrame f = new JFrame();
        Controller controller = new Controller(f);
        Xomaya instance = new Xomaya(controller);
        instance.open(ml);
        instance.start();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of prUsage method, of class Xomaya.
     */
    @Test
    public void testPrUsage() {
        System.out.println("prUsage");
        //Xomaya.prUsage();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of stop method, of class Xomaya.
     */
    @Test
    public void testStop() {
        System.out.println("stop");
        Xomaya instance = null;
        //instance.stop();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class Xomaya.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        Xomaya instance = null;
        //instance.run();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

}