package com.settler.domain.expenses.repo;

import com.settler.domain.expenses.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    /**
     * Fetch all expenses for a given group
     */
    List<Expense> findByGroupId(UUID groupId);

    /**
     * Fetch all expenses paid by a specific user
     */
    List<Expense> findByPaidBy(UUID userId);

    /**
     * Custom projection to get lightweight summaries if needed later
     */
    @Query("SELECT e FROM Expense e WHERE e.groupId = :groupId ORDER BY e.createdAt DESC")
    List<Expense> getRecentGroupExpenses(UUID groupId);
}
