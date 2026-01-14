package com.taskflow.kanban_service.dto;

import java.util.UUID;

public record TaskDto(
        UUID taskId,
        String title,
        UUID statusId,
        Integer position,
        String taskType,
        String description,
        String priority,
        String dueDate,
        UUID assigneeId
) {}
