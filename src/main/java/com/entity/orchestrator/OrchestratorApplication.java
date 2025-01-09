package com.entity.orchestrator;

import com.entity.orchestrator.EntityOrchestrator.EntityLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.javapoet.ClassName;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = {"com.entity.orchestrator", "flink.generic.db","com.example.demo","com.spriced.workflow"})
@ComponentScan(basePackages = {"com.entity.orchestrator", "flink.generic.db","com.example.demo","com.spriced.workflow"})
public class OrchestratorApplication {
	private static final Logger logger = LoggerFactory.getLogger(OrchestratorApplication.class);
	public static void main(String[] args) {
		logger.info("=============Inside Class OrchestratorApplication, Function main");
		ApplicationContext context = SpringApplication.run(OrchestratorApplication.class, args);
		EntityLoader entityLoader = context.getBean(EntityLoader.class);
		entityLoader.entityLoader();
		logger.info("================done===================");
	}
}
