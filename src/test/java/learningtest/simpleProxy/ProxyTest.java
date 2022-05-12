package learningtest.simpleProxy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import springbook.proxyclass.Hello;
import springbook.proxyclass.HelloTarget;
import springbook.proxyclass.HelloUppercase;
import springbook.proxyclass.UpperCaseHandler;

import java.lang.reflect.Proxy;

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

    @Test
    @DisplayName("다이나믹 프록시 생성 테스트")
    void GenerateDynamicProxy() {
        // 생성된 다이내믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로 Hello 타입으로 캐스팅 해도 안전하다.
        Hello proxyHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),  // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{Hello.class},  // 구현할 인터페이스
                new UpperCaseHandler(new HelloTarget())  //부가기능의 위임 코드를 담은 InvocationHandler
        );

        assertEquals("HELLO TOBY", proxyHello.sayHello("toby"));
        assertEquals("HI TOBY", proxyHello.sayHi("toby"));
        assertEquals("THANK YOU TOBY", proxyHello.sayThankYou("toby"));
    }
}
