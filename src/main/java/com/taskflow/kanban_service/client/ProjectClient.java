package com.taskflow.kanban_service.client;

import com.taskflow.kanban_service.config.FeignConfig;
import com.taskflow.kanban_service.dto.TaskFlowStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "project-service", configuration = FeignConfig.class)
public interface ProjectClient {

    @GetMapping("/api/projects/{projectId}/statuses")
    List<TaskFlowStatusDto> getWorkflow(@PathVariable UUID projectId);
}
