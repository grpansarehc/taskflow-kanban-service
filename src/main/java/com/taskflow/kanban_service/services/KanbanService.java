package com.taskflow.kanban_service.services;


import com.taskflow.kanban_service.client.ProjectClient;
import com.taskflow.kanban_service.client.TaskClient;
import com.taskflow.kanban_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KanbanService {

    private final ProjectClient projectClient;
    private final TaskClient taskClient;

    public KanbanBoardResponse getBoard(UUID projectId) {

        // 1️⃣ Get workflow (columns)
        List<TaskFlowStatusDto> statuses =
                projectClient.getWorkflow(projectId);

        // 2️⃣ Get tasks
        List<TaskDto> tasks =
                taskClient.getTasksByProject(projectId);

        // 3️⃣ Group tasks by status
        Map<UUID, List<TaskDto>> tasksByStatus =
                tasks.stream()
                        .collect(Collectors.groupingBy(TaskDto::statusId));

        // 4️⃣ Build board
        List<KanbanColumnResponse> columns = statuses.stream()
                .sorted(Comparator.comparing(TaskFlowStatusDto::position))
                .map(status -> new KanbanColumnResponse(
                        status.id(),
                        status.name(),
                        tasksByStatus
                                .getOrDefault(status.id(), new ArrayList<>())
                                .stream()
                                .sorted(Comparator.comparing(TaskDto::position))
                                .toList()
                ))
                .toList();

        return new KanbanBoardResponse(projectId, columns);
    }

    public void moveTask(UUID taskId, MoveTaskRequest request) {

        // Kanban does NOT store anything
        // It delegates update to Task Service

        taskClient.moveTask(taskId, request);

        // Kafka event will be published by Task Service
    }
}
