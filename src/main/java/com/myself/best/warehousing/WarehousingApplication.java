package com.myself.best.warehousing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan //扫描filter
@EnableTransactionManagement //开启事务支持
public class WarehousingApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehousingApplication.class, args);
        log.info("项目启动成功！");
    }
}
