package com.adiops.cpq.api.taskmgmt.service;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adiops.cpq.api.taskmgmt.repository.PublicTaskRepository;
import com.adiops.cpq.domain.Project;
import com.adiops.cpq.domain.Task;
import com.adiops.cpq.domain.User;
import com.adiops.cpq.repository.TaskRepository;
import com.adiops.cpq.security.SecurityUtils;
import com.adiops.cpq.service.TaskService;
import com.adiops.cpq.service.UserService;
import com.adiops.cpq.service.dto.TaskDTO;
import com.adiops.cpq.service.mapper.TaskMapper;
import com.adiops.cpq.web.rest.errors.BadRequestAlertException;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
@Transactional
public class PublicTaskService {

    private final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final PublicTaskRepository taskRepository;

    private final UserService userService;

    private final TaskMapper taskMapper;

    public PublicTaskService(PublicTaskRepository taskRepository, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    /**
     * Get all the tasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return taskRepository.findAllByUserIsCurrentUser(pageable).map(taskMapper::toDto);
    }

    /**
     * Save a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskDTO save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        String userLogin = SecurityUtils
                .getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Current user login not found"));
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(userLogin);
        if (!user.isPresent()) {
            throw new RuntimeException("User could not be found");
        }
        Task task = taskMapper.toEntity(taskDTO);
        task.setUser(user.get());
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * Update a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskDTO update(TaskDTO taskDTO) {
        log.debug("Request to update Task : {}", taskDTO);

        if (!taskRepository.existsById(taskDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", TaskDTO.class.getSimpleName(), "idnotfound");
        }

        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * complete a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskDTO complete(Long id, Boolean complete) {
        log.debug("Request to update Task : {}", id);
        Optional<Task> task = taskRepository.findById(id);
        if (!task.isPresent()) {
            throw new BadRequestAlertException("Entity not found", TaskDTO.class.getSimpleName(), "idnotfound");
        }
        Task taskEntity = task.get();
        taskEntity.setComplete(complete ? ZonedDateTime.now() : null);
        taskEntity = taskRepository.save(taskEntity);
        return taskMapper.toDto(taskEntity);
    }

}
