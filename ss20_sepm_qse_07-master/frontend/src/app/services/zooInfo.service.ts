import {Injectable} from '@angular/core';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {Globals, Utilities} from '../global/globals';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Enclosure} from '../dtos/enclosure';
import {ZooInfo} from '../dtos/zooInfo';
import {Employee} from '../dtos/employee';
@Injectable({
  providedIn: 'root'
})
export class ZooInfoService {
  private zooInfoBaseUri: string = this.globals.backendUri + '/zoo';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }


  displayZooInfo(): Observable<ZooInfo> {
    DEBUG_LOG('Load Zoo Information');
    return this.httpClient.get<ZooInfo>(this.zooInfoBaseUri);
  }

  editZooInfo(zooInfo: ZooInfo): Observable<ZooInfo> {
    return this.httpClient.put<ZooInfo>(this.zooInfoBaseUri + '/edit', zooInfo);
  }

}
