package com.example.filipedgb.cmovproj1;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation Teste, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Teste
    public void useAppContext() throws Exception {
        // Context of the app under Teste.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.filipedgb.cmovproj1", appContext.getPackageName());
    }
}
