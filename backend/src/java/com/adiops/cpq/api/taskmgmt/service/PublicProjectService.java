package com.adiops.cpq.api.taskmgmt.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adiops.cpq.api.taskmgmt.repository.PublicProjectRepository;
import com.adiops.cpq.domain.Project;
import com.adiops.cpq.domain.User;
import com.adiops.cpq.security.SecurityUtils;
import com.adiops.cpq.service.UserService;
import com.adiops.cpq.service.dto.ProjectDTO;
import com.adiops.cpq.service.mapper.ProjectMapper;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
@Transactional
public class PublicProjectService {

    private final Logger log = LoggerFactory.getLogger(PublicProjectService.class);

    private final PublicProjectRepository projectRepository;

    private final UserService userService;

    private final ProjectMapper projectMapper;

    public PublicProjectService(PublicProjectRepository projectRepository, UserService userService,
            ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectMapper = projectMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Projects of current user");
        return projectRepository.findAllByUserIsCurrentUser(pageable).map(projectMapper::toDto);
    }

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        String userLogin = SecurityUtils
                .getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Current user login not found"));
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(userLogin);
        if (!user.isPresent()) {
            throw new RuntimeException("User could not be found");
        }
        Project project = projectMapper.toEntity(projectDTO);
        project.setUser(user.get());
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

}
