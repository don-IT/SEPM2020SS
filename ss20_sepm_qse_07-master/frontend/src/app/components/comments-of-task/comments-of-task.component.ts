import {Component, Input, OnInit} from '@angular/core';
import {Comment} from '../../dtos/comment';
import {TaskCommentService} from '../../services/task-comment.service';
import {AlertService} from '../../services/alert.service';
import {Task} from '../../dtos/task';
import {Employee} from '../../dtos/employee';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-comments-of-task',
  templateUrl: './comments-of-task.component.html',
  styleUrls: ['./comments-of-task.component.css']
})
export class CommentsOfTaskComponent implements OnInit {
  componentId = 'comments-of-task';
  @Input() currentTask: Task;

  commentsVisible = false;

  newCommentText: string;
  commentsOfTask: Comment[];

  constructor(private taskCommentService: TaskCommentService, private alertService: AlertService, private authService: AuthService) {
  }

  ngOnInit(): void {
  }

  addComment() {
    if (this.newCommentText) {
      const commentToAdd = new Comment(null, this.currentTask.id, null, null, this.newCommentText);
      this.taskCommentService.createComment(commentToAdd).subscribe(
        (res: any) => {
          this.loadCommentsOfTask();
          this.newCommentText = '';
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'CommentsOfTask: addComment()');

        }
      );
    } else {
      this.alertService.warn('Comment cannot be empty!', {componentId: this.componentId}, 'CommentsOfTask: addComment()');
    }
  }

  loadCommentsOfTask() {
    this.taskCommentService.getCommentsOfTask(this.currentTask.id).subscribe(
      (comments) => {
        this.commentsOfTask = comments;
     /*   this.commentsOfTask = [
          new Comment(1, 545, 'admin', '2020-06-18 00:42', 'This is the content of the message.')
        ];*/
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'CommentsOfTask: loadCommentsOfTask()');
      }
    );
  }

  deleteComment(commentId) {
    this.taskCommentService.deleteComment(commentId).subscribe(
      (res: any) => {
        this.loadCommentsOfTask();
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'CommentsOfTask: deleteComment()');
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
