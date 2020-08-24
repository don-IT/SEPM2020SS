import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {AnimalService} from '../../services/animal.service';
import {AlertService} from '../../services/alert.service';
import {Animal} from '../../dtos/animal';


@Component({
  selector: 'app-animal-update-view',
  templateUrl: './animal-update-view.component.html',
  styleUrls: ['./animal-update-view.component.css']
})
export class AnimalUpdateViewComponent implements OnInit {
  @Input() animalCurr: Animal;
  animal: Animal;
  componentId = 'animal-update-view';
  validationErrors = {
    name: false,
    species: false,
    description: false,
    public: false
  };
  @Output() reloadAnimal = new EventEmitter();

  constructor(private animalService: AnimalService, private alertService: AlertService) {
  }

  ngOnInit(): void {
  }

  loadInfoToForm() {
   this.animal = this.animalCurr;
  }


  updateAnimal() {
    if (this.validate()) {
      this.animalService.updateAnimal(this.animal).subscribe(
        (res: any) => {
          this.alertService.success('Animal updated successfully!', {componentId: this.componentId}, 'AnimalUpdateView: updateAnimal()');
          this.clearErrors();
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'AnimalUpdateView: updateAnimal()');
        }
      );
    }
  }

  validate() {
    let valid = true;
    if (this.animal.name === null || this.animal.name === '') {
      this.validationErrors.name = true;
      valid = false;
    }
    if (this.animal.description === null || this.animal.description === '') {
      this.validationErrors.description = true;
      valid = false;
    }
    if (this.animal.species === null || this.animal.species === '') {
      this.validationErrors.species = true;
      valid = false;
    }
    return valid;
  }

  clearErrors() {
    this.validationErrors = {
      name: false,
      species: false,
      description: false,
      public: false
    };
  }

}
