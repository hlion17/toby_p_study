package learningtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbook.factorybean.Message;
import springbook.factorybean.MessageFactoryBean;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// 설정파일 이름을 지정하지 않으면 클래스 이름 + "-context.xml"이 디폴트로 사용된다.
@ContextConfiguration(locations = "classpath:test_applicationContext.xml")
public class FactoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        // 팩토리 빈으로 생성한 빈 오브젝트의 타입 테스트
        Object message = context.getBean("message");
        assertEquals(Message.class, message.getClass());
        assertEquals("Factory Bean", ((Message) message).getText());

        // 팩토리 빈 자체의 타입을 빈으로 등록 테스트
        // &가 붙고 안 붙고에 따라 getBean() 메소드가 돌려주는 오브젝트가 달라진다.
        Object factory = context.getBean("&message");
        assertEquals(MessageFactoryBean.class, factory.getClass());
    }

}
