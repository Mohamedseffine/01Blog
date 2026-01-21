package com.zone01oujda.moblogging.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long totalReports;
    private long bannedUsers;
    private long pendingReports;
}
