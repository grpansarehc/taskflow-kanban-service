package com.taskflow.kanban_service.dto;

import java.util.List;
import java.util.UUID;

public record KanbanBoardResponse(
        UUID projectId,
        List<KanbanColumnResponse> columns
) {}
