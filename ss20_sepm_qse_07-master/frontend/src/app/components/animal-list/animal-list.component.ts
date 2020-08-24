import {Component, OnInit, Input, Output, EventEmitter, QueryList, ViewChildren} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {Router} from '@angular/router';
import {DeleteWarningComponent} from '../delete-warning/delete-warning.component';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AlertService} from '../../services/alert.service';
import {Employee} from '../../dtos/employee';
import {AnimalComponent} from '../animal/animal.component';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureComponent} from '../enclosure/enclosure.component';
import {EnclosureService} from '../../services/enclosure.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-animal-list',
  templateUrl: './animal-list.component.html',
  styleUrls: ['./animal-list.component.css']
})
export class AnimalListComponent implements OnInit {

  // tslint:disable-next-line:no-input-rename
  @Input('animals') animals: Animal[];
  @Input() animalPage;
  @Output() deleteAnimal = new EventEmitter<Animal>();
  @Input() enclosurePage;
  @Input() employeePage;
  @Output() unassignAnimal = new EventEmitter<Animal>();
  @ViewChildren(DeleteWarningComponent)

  deleteWarningComponents: QueryList<DeleteWarningComponent>;
  stopClickPropagation: boolean = false;
  enableDelete: boolean = false;

  constructor(private authService: AuthService, private animalService: AnimalService, private route: Router,
              private alertService: AlertService, private enclosureService: EnclosureService) { }
 // currentEnclosures = this.enclosureComponent.enclosures;
  searchAnimal = new Animal(null, null, null, null, null, null);
  allAnimals: Animal[];
  allEnclosures: Enclosure[];
  error: boolean = false;
  errorMessage: string = '';
  visitedEmployeeUsername: String;
  visitedEnclosureId: String;
  currentRoute: String;
  isError: boolean;
  alertMessage: String;

  ngOnInit(): void {
    this.getAnimals();
    this.getAllEnclosures();
  }

  getAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.allAnimals = animals;
      },
      error => {
        if (error.status === 404) {
          this.animals.length = 0;
        }
        DEBUG_LOG('Failed to load all animals');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getAllEnclosures() {
    this.enclosureService.getAllEnclosures().subscribe(
      enclosures => {
        this.allEnclosures = enclosures;
      },
      error => {
        if (error.status === 404) {
          this.allEnclosures.length = 0;
        }
        DEBUG_LOG('Failed to load all enclosures');
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  private defaultServiceErrorHandling(error: any) {
    DEBUG_LOG(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  isEnclosureView(): boolean {
    return this.route.url.includes('enclosure-view');
  }

  changeDeleteState() {
    this.enableDelete = !this.enableDelete;
  }

  showInfo(a: Animal) {
    if (!this.stopClickPropagation) {
      DEBUG_LOG('got here');
      this.route.navigate(['/animal-view/' + a.id]);
    }
  }

  getFilteredAnimals() {
    this.isError = false;
    this.currentRoute = this.route.url;
    this.visitedEmployeeUsername = this.currentRoute.substring(this.currentRoute.lastIndexOf('/') + 1);
    if (this.visitedEmployeeUsername === 'animal') {
      this.visitedEmployeeUsername = null;
    }
    if (this.currentRoute.includes('enclosure-view')) {
      this.visitedEnclosureId = this.currentRoute.substring(this.currentRoute.lastIndexOf('/') + 1);
    }
    if ( this.visitedEnclosureId == null) {
    this.animalService.searchAnimals(this.searchAnimal, this.visitedEmployeeUsername, null).subscribe(
      animals => {
        this.animals = animals;
      },
      error => {
        DEBUG_LOG('Failed to load all animals');
        this.isError = true;
        this.alertMessage = error.error;
      }
    );
  } else {
      this.animalService.searchAnimals(this.searchAnimal, null, this.visitedEnclosureId).subscribe(
        animals => {
          this.animals = animals;
        },
        error => {
          DEBUG_LOG('Failed to load all animals');
          this.isError = true;
          this.alertMessage = error.error;
        }
      );
    }
  }


  deleteAnimalFn(animal: any) {
    this.deleteAnimal.emit(animal);
    this.toggleClickPropagation();
  }

  delAnimal(animal: any) {
    this.animalService.deleteAnimal(animal).subscribe(
      () => {
        this.loadAllAnimals();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'animal-list deleteAnimal(' + JSON.stringify(animal) + ')');
      }
    );
  }

  loadAllAnimals() {
    this.animalService.getAnimals().subscribe(
      (animals) => {
        if (animals.length > 0) {
          this.animals = animals;
        } else {
          this.animals.length = 0;
        }

      },
    error => {
        if (error.status !== undefined && error.status === 404) {
          this.animals.length = 0;
        }
      this.alertService.alertFromError(error, {}, 'animal-list loadAnimals()');
    }
    );
  }

  toggleClickPropagation () {
    DEBUG_LOG('Before Toggled CLICK propagation: ' + this.stopClickPropagation);
    this.stopClickPropagation = !this.stopClickPropagation;
    DEBUG_LOG('Toggled CLICK click propagation: ' + this.stopClickPropagation);
  }
}

