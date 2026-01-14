package com.taskflow.kanban_service.dto;

import java.util.UUID;

public record TaskFlowStatusDto(
        UUID id,
        String statusName,
        Integer position
) {}
