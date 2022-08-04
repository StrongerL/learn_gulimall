package com.atguigu.gulimall.thirdparty;

import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallThirdPartyApplicationTests {

    @Resource
    private SmsComponent smsComponent;

    @Test
    public void sendSmsCode() {
        smsComponent.sendCode("17307485051", "134531");
    }

}
