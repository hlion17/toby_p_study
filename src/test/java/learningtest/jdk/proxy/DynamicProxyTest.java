package learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import springbook.proxyclass.Hello;
import springbook.proxyclass.HelloTarget;
import springbook.proxyclass.UpperCaseHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTest {
    @Test
    void simpleProxy() {
        // JDK 다이내믹 프록시 생성
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader()
                , new Class[]{Hello.class}
                , new UpperCaseHandler(new HelloTarget())
        );

        Assertions.assertEquals("HELLO TOBY", proxiedHello.sayHello("toby"));
        Assertions.assertEquals("HI TOBY", proxiedHello.sayHi("toby"));
        Assertions.assertEquals("THANK YOU TOBY", proxiedHello.sayThankYou("toby"));
    }

    @Test
    void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        // 타깃 설정
        pfBean.setTarget(new HelloTarget());
        // 부가기능을 담은 어드바이스를 추가한다. (여러개를 추가할 수 있다.)
        pfBean.addAdvice(new UppercaseAdvice());

        // FactoryBean 이므로 getObject() 로 생성된 프록시를 가져온다.
        Hello proxiedHello = (Hello) pfBean.getObject();

        Assertions.assertEquals("HELLO TOBY", proxiedHello.sayHello("toby"));
        Assertions.assertEquals("HI TOBY", proxiedHello.sayHi("toby"));
        Assertions.assertEquals("THANK YOU TOBY", proxiedHello.sayThankYou("toby"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            // 리플랙션의 Method 와 달리 메소드 실행시
            // 타깃 오브젝트를 전달할 필요가 없다.
            // MethodInvocation 은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 떄문이다.
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();  // 부가기능 적용
        }
    }

}
