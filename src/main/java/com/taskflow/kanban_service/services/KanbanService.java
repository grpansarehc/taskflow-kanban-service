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

       
        List<TaskFlowStatusDto> statuses;
        try {
            statuses = projectClient.getWorkflow(projectId);
        } catch (Exception e) {
            System.err.println("KanbanService: Error calling ProjectClient for workflow: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

       
        List<TaskDto> tasks;
        try {
            tasks = taskClient.getTasksByProject(projectId);
        } catch (Exception e) {
            System.err.println("KanbanService: Error calling TaskClient for tasks: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
       
        if (tasks != null) {
            System.out.println("KanbanService: Fetched " + tasks.size() + " tasks.");
            tasks.forEach(t -> System.out.println("Task: " + t.title() + ", Type: " + t.taskType()));
        } else {
            System.out.println("KanbanService: Fetched tasks is null");
        }

     
        Map<UUID, List<TaskDto>> tasksByStatus =
                tasks.stream()
                        .collect(Collectors.groupingBy(TaskDto::statusId));

        List<KanbanColumnResponse> columns = statuses.stream()
                .sorted(Comparator.comparing(TaskFlowStatusDto::position, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(status -> new KanbanColumnResponse(
                        status.id(),
                        status.statusName(),
                        tasksByStatus
                                .getOrDefault(status.id(), new ArrayList<>())
                                .stream()
                                .sorted(Comparator.comparing(TaskDto::position, Comparator.nullsLast(Comparator.naturalOrder())))
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
