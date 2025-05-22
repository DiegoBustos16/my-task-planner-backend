package diegobustos.my_task_planner_backend.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvInitializer {
    static {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
