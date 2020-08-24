import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Animal} from '../dtos/animal';
import {HttpClient} from '@angular/common/http';
import {Employee} from '../dtos/employee';
import {Enclosure} from '../dtos/enclosure';
import {Globals, Utilities} from '../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Injectable({
  providedIn: 'root'
})
export class AnimalService {

  private animalBaseUri: string = this.globals.backendUri + '/animals';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createAnimal(animal: Animal): Observable<Animal> {
    DEBUG_LOG('Create animal:' + JSON.stringify(animal));
    return this.httpClient.post<Animal>(this.animalBaseUri, animal);
  }

  /**
   * Get List of all current animals
   */
  getAnimals(): Observable<Animal[]> {
    DEBUG_LOG('Getting all animals');
    return this.httpClient.get<Animal[]>(this.animalBaseUri);
  }

  deleteAnimal(animal: Animal) {
    DEBUG_LOG('Delete animal: ' + JSON.stringify(animal));
    return this.httpClient.delete(this.animalBaseUri + '/' + animal.id);
  }

  getAnimalById(id): Observable<Animal> {
    DEBUG_LOG('Get animal by id: ' + id);
    return this.httpClient.get<Animal>(this.animalBaseUri + '/' + id);
  }

  updateAnimal(animal: Animal) {
    DEBUG_LOG('Update animal: ' + JSON.stringify(animal));
    return this.httpClient.put(this.animalBaseUri + '/edit', animal);
  }

  searchAnimals(animal: Animal, employeeUsername: String, visitedEnclosureId: String): Observable<Animal[]> {

    DEBUG_LOG('Getting filtered list of animals with name: ' + animal.name + ' description: ' + animal.description
      + ' specie: ' + animal.species + ' enclosure: ' + animal.enclosure);
    let query = '/search?';
    if (animal.name != null && animal.name !== '') {
      query = query + 'name=' + animal.name + '&';
    }
    if (animal.description != null && animal.description !== '') {
      query = query + 'description=' + animal.description + '&';
    }
    if (animal.species != null && animal.species !== '') {
      query = query + 'species=' + animal.species + '&';
    }
    if (visitedEnclosureId != null) {
      query = query + 'enclosureId=' + visitedEnclosureId + '&';
    } else if (animal.enclosure != null) {
      query = query + 'enclosureId=' + animal.enclosure + '&';
    }
    if (employeeUsername != null && employeeUsername !== 'animal' ) {
      query = query + 'employeeUsername=' + employeeUsername + '&';
    }
    query = query.substring(0, query.length - 1);
    return this.httpClient.get<Animal[]>(this.animalBaseUri + query);
  }

}
