package com.eport.rule;

import com.eport.common.feign.annotation.EnablePigxFeignClients;
import com.eport.common.security.annotation.EnableResourceServer;
import com.eport.common.swagger.annotation.EnableOpenApi;
import com.eport.uni.annotation.EnableAuthTemplateServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Alaia
 */
@EnableOpenApi("rule")
@EnablePigxFeignClients
@EnableResourceServer
@EnableAuthTemplateServer
@EnableDiscoveryClient
@SpringBootApplication
public class EportDaemonRuleEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(EportDaemonRuleEngineApplication.class, args);
	}

}
