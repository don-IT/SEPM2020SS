package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;

import java.util.List;

public interface TaskCommentService {

    /**
     * get comment by its id
     *
     * @param taskCommentId of the desired comment
     * @return comment with the given id
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if no Comment with the Id exists
     */
    TaskComment findById(Long taskCommentId);

    /**
     * get all comments of a task
     *
     * @param taskId of the task which comments will be retrieved
     * @return all comments of the task
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if no Task with the Id exists
     */
    List<TaskComment> findAllByTaskId(Long taskId);

    /**
     * saves a new comment in the database
     *
     * @param taskComment that will be saved
     * @return the created comment
     */
    TaskComment createComment(TaskComment taskComment);

    /**
     * delete a comment from the database
     *
     * @param taskCommentId of the comment to be deleted
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if no Comment with the Id exists
     */
    void delete(Long taskCommentId);
}
