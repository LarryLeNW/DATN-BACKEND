package com.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.entity.Team;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeamService {

	private final TeamRepository teamRepository;

    // Tạo mới một team
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    // Lấy tất cả các team
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    // Lấy team theo ID
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    // Cập nhật team
    public Team updateTeam(Long id, Team teamDetails) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + id));

        // Cập nhật các thuộc tính của team từ teamDetails
        team.setName(teamDetails.getName());
        team.setDescription(teamDetails.getDescription());
        // thêm các trường khác nếu có

        return teamRepository.save(team);
    }

    // Xóa team
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + id));

        teamRepository.delete(team);
    }
}

