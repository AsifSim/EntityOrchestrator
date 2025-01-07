package com.entity.orchestrator;

import com.entity.orchestrator.EntityOrchestrator.EntityLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = {"com.entity.orchestrator", "flink.generic.db","com.example.demo","com.spriced.workflow"})
@ComponentScan(basePackages = {"com.entity.orchestrator", "flink.generic.db","com.example.demo","com.spriced.workflow"})
public class OrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestratorApplication.class, args);
		EntityLoader entityLoader=new EntityLoader();
		entityLoader.entityLoader("C:\\Users\\asif.azmi_simadvisor\\Downloads\\orchestrator (1)\\orchestrator\\src\\main\\resources");
	}

}
