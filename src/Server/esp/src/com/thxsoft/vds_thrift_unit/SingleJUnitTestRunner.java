package com.thxsoft.vds_thrift_unit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;


public class SingleJUnitTestRunner {
    public static void main(String... args) throws ClassNotFoundException {
        String[] classAndMethod = args[0].split("#");
        Request request = Request.method(Class.forName(classAndMethod[0]),
                classAndMethod[1]);

        Result result = new JUnitCore().run(request);
        System.out.println(result.toString());
        if (result.wasSuccessful())
        {
        	System.out.println("Success");
        }
        else
        {
        	System.out.println("Failure");
        }
    }
}