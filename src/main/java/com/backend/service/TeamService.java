package com.backend.service;

import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Team;
import com.backend.repository.TeamRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeamService {
    
    TeamRepository teamRepository;


    EntityManager entityManager; 
    public Team createTeam(Team team) {
        // Kiểm tra đầu vào
        if (team.getName() == null || team.getName().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }
        // Lưu đội vào cơ sở dữ liệu
        return teamRepository.save(team);
    }

    public PagedResponse<Team> getTeams(int page, int limit, String sort, String... search) {
        // Khởi tạo danh sách tiêu chí tìm kiếm
        List<SearchType> criteriaList = new ArrayList<>();
        
        // Tạo dịch vụ tìm kiếm tùy chỉnh
        CustomSearchRepository<Team> customSearchService = new CustomSearchRepository<>(entityManager);
        
        // Xây dựng truy vấn tìm kiếm cho bảng Team
        CriteriaQuery<Team> query = customSearchService.buildSearchQuery(Team.class, search, sort);
        
        // Thực hiện truy vấn và lấy danh sách đội
        List<Team> teams = entityManager.createQuery(query)
                .setFirstResult((page - 1) * limit)
                .setMaxResults(limit)
                .getResultList();
        
        // Xây dựng truy vấn đếm số lượng đội
        CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Team.class, search);
        
        // Lấy tổng số đội
        long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        
        // Tính toán tổng số trang
        int totalPages = (int) Math.ceil((double) totalElements / limit);
        
        // Trả về kết quả dưới dạng PagedResponse
        return new PagedResponse<>(teams, page, totalPages, totalElements, limit);
    }


    public Team getTeamById(int id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Team not found with id " + id));
    }

    public Team updateTeam(int id, Team team) {
        Team existingTeam = teamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Team not found with id " + id));
        
        // Cập nhật thông tin của đội
        existingTeam.setName(team.getName());
        existingTeam.setDescription(team.getDescription());
        existingTeam.setLinkJoin(team.getLinkJoin());
        
        return teamRepository.save(existingTeam);
    }

    public void deleteTeam(int id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException("Team not found with id " + id);
        }
        teamRepository.deleteById(id);
    }
}
