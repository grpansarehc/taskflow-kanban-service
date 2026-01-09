package com.taskflow.kanban_service.dto;

import java.util.UUID;

public record MoveTaskRequest(
        UUID statusId,
        Integer position
) {}
