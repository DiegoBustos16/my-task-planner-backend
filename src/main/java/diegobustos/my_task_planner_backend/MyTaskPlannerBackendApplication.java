package diegobustos.my_task_planner_backend;

import diegobustos.my_task_planner_backend.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyTaskPlannerBackendApplication {

	public static void main(String[] args) {
		new DotenvInitializer();
		SpringApplication.run(MyTaskPlannerBackendApplication.class, args);
	}

}
