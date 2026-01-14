package com.taskflow.kanban_service.controllers;


import com.taskflow.kanban_service.dto.KanbanBoardResponse;
import com.taskflow.kanban_service.dto.MoveTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.taskflow.kanban_service.services.KanbanService;

import java.util.UUID;

@RestController
@RequestMapping("/api/kanban")
@RequiredArgsConstructor
public class KanbanController {

    private final KanbanService kanbanService;
    @GetMapping("/projects/{projectId}/board")
    public KanbanBoardResponse getBoard(@PathVariable UUID projectId) {
        return kanbanService.getBoard(projectId);
    }

    // Move task between columns
    @PutMapping("/tasks/{taskId}/move")
    public void moveTask(
            @PathVariable UUID taskId,
            @RequestBody MoveTaskRequest request
    ) {
        kanbanService.moveTask(taskId, request);
    }
}
