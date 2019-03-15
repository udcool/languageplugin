package com.hyjoy.gradle.plugin.language;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;


/**
 * Created by hyjoy on 2019/3/15.
 */
public class HJLLanguageTaskTest {

    @Before
    public void before() {

    }

    @Test
    public void excuteTest() throws IOException {
//        HJLLanguageTask.execute("../app/laugnage-map.json");


        String data = "Se non hai ricevuto l\\'SMS, non esitare a contattare l'a nostra Assistenza online";
        System.out.println(data);
        String data1 = data.replaceAll("([^\\\\]')/", "\\\\'");
        System.out.println(data1);
    }

    @After
    public void after() {

    }
}
