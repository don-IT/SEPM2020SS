package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.RepeatableTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepeatableTaskRepository extends JpaRepository<RepeatableTask, Long> {

    Optional<RepeatableTask> findByFollowTask(Task task);
}
