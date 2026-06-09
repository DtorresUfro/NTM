package com.Ntm.service;

import com.Ntm.entity.Task;
import com.Ntm.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task guardarTarea(String title, String description, Date dueDate,
                             String createdBy, String roomMasterKey) {
        Task tarea = new Task(title, description, dueDate, createdBy, roomMasterKey);
        return taskRepository.save(tarea);
    }
}