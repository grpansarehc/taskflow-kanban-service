package com.taskflow.kanban_service.client;

import com.taskflow.kanban_service.dto.TaskFlowStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "project-service")
public interface ProjectClient {

    @GetMapping("/projects/{projectId}/statuses")
    List<TaskFlowStatusDto> getWorkflow(@PathVariable UUID projectId);
}
