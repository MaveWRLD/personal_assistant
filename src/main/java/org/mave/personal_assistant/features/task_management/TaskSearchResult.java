package org.mave.personal_assistant.features.task_management;

import lombok.Builder;
import lombok.Data;
import org.mave.personal_assistant.features.task_management.dto.TaskResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class TaskSearchResult {
    private List<TaskResponse> tasks;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private boolean hasMore;

    public static TaskSearchResult from(Page<TaskResponse> page) {
        return TaskSearchResult.builder()
                .tasks(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .hasMore(!page.isLast())
                .build();
    }
}

