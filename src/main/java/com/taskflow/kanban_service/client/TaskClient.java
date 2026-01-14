package com.taskflow.kanban_service.client;

import com.taskflow.kanban_service.config.FeignConfig;
import com.taskflow.kanban_service.dto.MoveTaskRequest;
import com.taskflow.kanban_service.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tms-service", configuration = FeignConfig.class)
public interface TaskClient {

    @GetMapping("/api/tasks")
    List<TaskDto> getTasksByProject(@RequestParam UUID projectId);

    @PutMapping("/api/tasks/{taskId}/move")
    void moveTask(
            @PathVariable UUID taskId,
            @RequestBody MoveTaskRequest request
    );
}
