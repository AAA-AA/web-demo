package com.megvii.sng.dzh.webdemo;

import com.megvii.sng.dzh.webdemo.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication.run(Application.class, args);
		String port = SpringUtil.getProperty("server.port");
		String hostAddress = InetAddress.getLocalHost().getHostAddress();
		log.info("接口页面访问地址: http://{}:{}/doc.html",hostAddress,port);
	}

}
