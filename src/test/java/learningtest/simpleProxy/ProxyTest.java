package learningtest.simpleProxy;

import org.junit.jupiter.api.Test;
import springbook.proxyclass.Hello;
import springbook.proxyclass.HelloTarget;
import springbook.proxyclass.HelloUppercase;

import static org.junit.jupiter.api.Assertions.*;

public class ProxyTest {
    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        assertEquals("Hello toby", hello.sayHello("toby"));
        assertEquals("Hi toby", hello.sayHi("toby"));
        assertEquals("Thank You toby", hello.sayThankYou("toby"));

        Hello proxyHello = new HelloUppercase(new HelloTarget());
        assertEquals("HELLO TOBY", proxyHello.sayHello("toby"));
        assertEquals("HI TOBY", proxyHello.sayHi("toby"));
        assertEquals("THANK YOU TOBY", proxyHello.sayThankYou("toby"));
    }
}
