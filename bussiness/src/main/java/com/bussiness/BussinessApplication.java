package com.bussiness;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //标识的是注解所标注的类的包，这个类为启动类，启动SpringBoot方法
@MapperScan("com.bussiness.dao")  //给dao包下的接口生成一个实现类，并且将其放在ioc 容器中
public class BussinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(BussinessApplication.class, args);
    }


}
