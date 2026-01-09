package com.taskflow.kanban_service.dto;

import java.util.List;
import java.util.UUID;

public record KanbanColumnResponse(
        UUID statusId,
        String name,
        List<TaskDto> tasks
) {}
