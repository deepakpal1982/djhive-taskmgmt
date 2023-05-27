package com.adiops.cpq.api.taskmgmt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.adiops.cpq.domain.Project;
import com.adiops.cpq.repository.ProjectRepository;

@Repository
public interface PublicProjectRepository extends ProjectRepository {

    @Query(value = "select distinct project from Project project left join fetch project.user where project.user.login = ?#{principal.username}", countQuery = "select count(distinct project) from Project project where project.user.login = ?#{principal.username}")
    Page<Project> findAllByUserIsCurrentUser(Pageable pageable);

}
