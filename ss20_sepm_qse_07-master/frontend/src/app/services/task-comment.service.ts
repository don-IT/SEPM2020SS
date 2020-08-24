import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Comment} from '../dtos/comment';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {Globals, Utilities} from '../global/globals';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TaskCommentService {

  private commentBaseUri: string = this.globals.backendUri + '/comments';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getCommentsOfTask(taskId): Observable<Comment[]> {
    DEBUG_LOG('Get comments of task ' + taskId);
    return this.httpClient.get<Comment[]>(this.commentBaseUri + '/' + taskId);
  }

  createComment(comment: Comment) {
    DEBUG_LOG('Create comment: ' + JSON.stringify(comment));
    return this.httpClient.post(this.commentBaseUri, comment);
  }

  deleteComment(commentId) {
    DEBUG_LOG('Delete comment ' + commentId);
    return this.httpClient.delete(this.commentBaseUri + '/' + commentId);
  }
}
