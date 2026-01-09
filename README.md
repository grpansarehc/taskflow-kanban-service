# Kanban Service

## Overview
The **Kanban Service** provides Kanban board functionality, organizing tasks by workflow status and enabling drag-and-drop task management for the TaskFlow application.

## Technology Stack
- **Spring Boot** 3.4.1
- **Spring Data JPA**
- **Spring Cloud OpenFeign** (for inter-service communication)
- **PostgreSQL** Database
- **Java** 17

## Port
- **Default Port**: `8084`

## Features
- ✅ Kanban board view by project
- ✅ Tasks organized by workflow status (columns)
- ✅ Drag-and-drop task movement between columns
- ✅ Real-time task position updates
- ✅ Integration with Task Service and Project Service
- ✅ Workflow status management

## Database Configuration

### Database Name
```
kanban_db
```

### Connection Properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kanban_db
spring.datasource.username=project_user
spring.datasource.password=project_pass
spring.jpa.hibernate.ddl-auto=update
```

## API Endpoints

### Kanban Board Endpoints (`/api/kanban`)

#### Get Kanban Board for Project
```http
GET /api/kanban/projects/{projectId}/board
Authorization: Bearer <token>

Response:
{
  "projectId": "uuid",
  "columns": [
    {
      "statusId": "uuid",
      "name": "To Do",
      "tasks": [
        {
          "taskId": "uuid",
          "title": "Task title",
          "statusId": "uuid",
          "position": 0
        }
      ]
    },
    {
      "statusId": "uuid",
      "name": "In Progress",
      "tasks": [...]
    },
    {
      "statusId": "uuid",
      "name": "Done",
      "tasks": [...]
    }
  ]
}
```

#### Move Task Between Columns
```http
PUT /api/kanban/tasks/{taskId}/move
Authorization: Bearer <token>
Content-Type: application/json

{
  "newStatusId": "status-uuid",
  "newPosition": 2
}
```

## Data Models

### Kanban Board Response
```java
{
  "projectId": "UUID",
  "columns": "List<KanbanColumnResponse>"
}
```

### Kanban Column Response
```java
{
  "statusId": "UUID",
  "name": "String",
  "tasks": "List<TaskDto>"
}
```

### Task DTO
```java
{
  "taskId": "UUID",
  "title": "String",
  "statusId": "UUID",
  "position": "Integer"
}
```

### Move Task Request
```java
{
  "newStatusId": "UUID",
  "newPosition": "Integer"
}
```

## Inter-Service Communication

### Feign Client - Project Service
```java
@FeignClient(name = "PROJECT-SERVICE")
public interface ProjectClient {
    @GetMapping("/api/projects/{projectId}/statuses")
    List<TaskFlowStatusDto> getProjectStatuses(@PathVariable UUID projectId);
}
```

### Feign Client - Task Service
```java
@FeignClient(name = "TASK-SERVICE")
public interface TaskClient {
    @GetMapping("/api/tasks/all")
    List<TaskDto> getAllTasks();
    
    @PutMapping("/api/tasks/{taskId}/move")
    void moveTask(@PathVariable UUID taskId, @RequestBody MoveTaskRequest request);
}
```

## How It Works

### Loading Kanban Board
1. Client requests board for a project
2. Service fetches workflow statuses from Project Service
3. Service fetches all tasks from Task Service
4. Tasks are filtered by project and grouped by status
5. Tasks are sorted by position within each column
6. Response is structured as columns with tasks

### Moving Tasks
1. Client drags task to new column/position
2. Frontend sends move request with `newStatusId` and `newPosition`
3. Kanban Service forwards request to Task Service
4. Task Service updates task's `statusId` and `position`
5. Board is refreshed to show new state

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Eureka Service Registry running
- Project Service running
- Task Service running

### Database Setup
```sql
CREATE DATABASE kanban_db;
CREATE USER project_user WITH PASSWORD 'project_pass';
GRANT ALL PRIVILEGES ON DATABASE kanban_db TO project_user;
```

### Using Maven
```bash
cd kanban-service
mvn clean install
mvn spring-boot:run
```

## Service Registration
Registers with Eureka as: **KANBAN-SERVICE**

## Security Configuration
All endpoints permit requests (authentication handled by API Gateway).

```java
.requestMatchers("/kanban/**").permitAll()
```

## Health Check
```bash
curl http://localhost:8084/actuator/health
```

## Testing

### Get Kanban Board
```bash
curl http://localhost:8080/api/kanban/projects/{projectId}/board \
  -H "Authorization: Bearer <token>"
```

### Move Task
```bash
curl -X PUT http://localhost:8080/api/kanban/tasks/{taskId}/move \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "newStatusId": "done-status-uuid",
    "newPosition": 1
  }'
```

## Frontend Integration

### Kanban Board Component
The frontend Kanban board (`KanbanBoard.tsx`) integrates with this service:

```typescript
// Fetch board data
const board = await kanbanService.getKanbanBoard(projectId);

// Move task on drag-and-drop
await kanbanService.moveTask(taskId, {
  newStatusId: targetColumnId,
  newPosition: targetPosition
});
```

### Features
- Drag-and-drop task cards between columns
- Real-time position updates
- Optimistic UI updates
- Error handling with rollback

## Workflow Statuses

### Default Statuses
Projects typically have these workflow statuses:
- **To Do**: Tasks not yet started
- **In Progress**: Active tasks
- **In Review**: Tasks awaiting review
- **Done**: Completed tasks

### Custom Statuses
Projects can define custom workflow statuses via Project Service.

## Troubleshooting

### Empty Board
- Verify project has workflow statuses defined
- Check if tasks exist for the project
- Review Task Service connectivity

### Tasks Not Moving
- Verify Task Service is running
- Check Feign client configuration
- Review Task Service logs for errors

### Incorrect Task Order
- Positions are 0-indexed
- Tasks with same position are sorted by creation date

## Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## Future Enhancements
- [ ] Swimlanes (group by assignee, priority, etc.)
- [ ] WIP (Work In Progress) limits per column
- [ ] Board filters and search
- [ ] Bulk task operations
- [ ] Board templates
- [ ] Real-time updates via WebSocket

---

**Status**: ✅ Production Ready  
**Maintained by**: TaskFlow Team
