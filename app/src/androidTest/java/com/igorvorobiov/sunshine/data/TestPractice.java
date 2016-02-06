package com.igorvorobiov.sunshine.data;

import android.test.AndroidTestCase;

import junit.framework.TestCase;


/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class TestPractice extends TestCase {
    public void testSomething(){

        int a = 10;
        int b = 10;

        int c = a + b;

        assertEquals("C is supposed to be sum of A and B", c, 20);
    }
}
