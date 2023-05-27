package com.adiops.cpq.api.taskmgmt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({ "com.adiops.cpq.api.taskmgmt.repository" })
public class TaskMgmtConfig {

}
