package com.backend.controller;

import com.backend.entity.Team;
import com.backend.service.TeamService;
import jakarta.validation.Valid;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/teams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TeamController {

    TeamService teamService;

    // Lấy danh sách tất cả đội
    @GetMapping // Phương thức GET để lấy danh sách đội
    public ResponseEntity<PagedResponse<Team>> getAllTeams(
            @RequestParam(defaultValue = "1") int page, 
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String[] search) {
        
        PagedResponse<Team> response = teamService.getTeams(page, limit, sort, search);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Tạo đội mới
    @PostMapping
    public ResponseEntity<ApiResponse<Team>> createTeam(@Valid @RequestBody Team team) {
        Team createdTeam = teamService.createTeam(team);
        return new ResponseEntity<>(ApiResponse.<Team>builder().result(createdTeam).build(), HttpStatus.CREATED);
    }

    // Lấy đội theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Team>> getTeamById(@PathVariable int id) {
        Team team = teamService.getTeamById(id);
        return ResponseEntity.ok(ApiResponse.<Team>builder().result(team).build());
    }

    // Xóa đội theo ID (nếu cần thiết)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable int id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }

    // Cập nhật đội theo ID (nếu cần thiết)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Team>> updateTeam(@PathVariable int id, @Valid @RequestBody Team team) {
        Team updatedTeam = teamService.updateTeam(id, team);
        return ResponseEntity.ok(ApiResponse.<Team>builder().result(updatedTeam).build());
    }
}
