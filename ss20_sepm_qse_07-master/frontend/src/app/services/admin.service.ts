import { Injectable } from '@angular/core';
import {Globals, Utilities} from '../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthRequest} from '../dtos/auth-request';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private adminBaseURL: string = this.globals.backendUri + '/admin';
  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Persists Admin to the backend
   * @param admin to persist
   */
  createAdmin(admin: AuthRequest): Observable<AuthRequest> {
    DEBUG_LOG('Create admin with username ' + admin.username);
    return this.httpClient.post<AuthRequest>(this.adminBaseURL, admin);
  }
}
