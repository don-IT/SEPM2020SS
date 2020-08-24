import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AnimalService} from '../../services/animal.service';
import {Animal} from '../../dtos/animal';
import {Location} from '@angular/common';
import {AnimalListComponent} from '../animal-list/animal-list.component';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-animal',
  templateUrl: './animal.component.html',
  styleUrls: ['./animal.component.css']
})
export class AnimalComponent implements OnInit {
  error: boolean = false;
  errorMessage: string = '';
  animalCreationForm: FormGroup;
  submittedAnimal = false;
  animals: Animal[];



  constructor(private _location: Location, private animalService: AnimalService, private formBuilder: FormBuilder,
              private authService: AuthService, private alertService: AlertService) {
    this.animalCreationForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      species: ['', [Validators.required]],
      publicInformation: [''],
      description: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.getAnimals();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }


  addAnimal() {
    this.submittedAnimal = true;
    if (this.animalCreationForm.valid) {
      const animal: Animal = new Animal(
        null,
        this.animalCreationForm.controls.name.value,
        this.animalCreationForm.controls.description.value,
        this.animalCreationForm.controls.species.value,
        null,
        this.animalCreationForm.controls.publicInformation.value);
      this.createAnimal(animal);
      this.clearForm();
    } else {
      console.log('Invalid input.');
    }
  }

  createAnimal(animal: Animal) {
    this.animalService.createAnimal(animal).subscribe(
      () => {
        this.getAnimals();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'animal-overview createAnimal(' + JSON.stringify(animal) + ')');
      }
    );
  }

  /**
   * Get All current animals
   */
  getAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.animals = animals;
      },
      error => {
        if (error.status === 404) {
          if (this.animals !== undefined) {
            this.animals.length = 0;
          }
        }
        DEBUG_LOG('Failed to load all animals');
        this.alertService.alertFromError(error, {}, 'animal-overview getAnimals()');
      }
    );
  }

  deleteAnimal(animal: Animal) {
    this.animalService.deleteAnimal(animal).subscribe(
      () => {
        this.getAnimals();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'animal-overview deleteAnimal(' + JSON.stringify(animal) + ')');
      }
    );
  }

  private clearForm() {
    this.animalCreationForm.reset();
    this.submittedAnimal = false;
  }

   defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  backClicked() {
    this._location.back();
  }
}
