package com.adiops.cpq.api.taskmgmt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.adiops.cpq.domain.Task;
import com.adiops.cpq.repository.TaskRepository;

@Repository
public interface PublicTaskRepository extends TaskRepository {

    @Query(value = "select distinct task from Task task left join fetch task.user left join fetch task.project where task.user.login = ?#{principal.username}", countQuery = "select count(distinct task) from Task task where task.user.login = ?#{principal.username}")
    Page<Task> findAllByUserIsCurrentUser(Pageable pageable);

}
