import { Component, OnInit } from '@angular/core';
import {Enclosure} from '../../dtos/enclosure';
import {Task} from '../../dtos/task';
import {Employee} from '../../dtos/employee';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EnclosureService} from '../../services/enclosure.service';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {EmployeeService} from '../../services/employee.service';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-enclosure-edit-view',
  templateUrl: './enclosure-edit-view.component.html',
  styleUrls: ['./enclosure-edit-view.component.css']
})
export class EnclosureEditViewComponent implements OnInit {

  enclosureToView: Enclosure;
  tasks: Task[];
  janitors: Employee[];
  editing: boolean;
  enclosureEditInfo: FormGroup;
  submittedEnclosure: boolean;
  uploadedPicture: string;
  private fileType: string;
  constructor(private enclosureService: EnclosureService, private authService: AuthService,
              private route: ActivatedRoute, private router: Router, private _location: Location,
              private animalService: AnimalService, private taskService: TaskService,
              private employeeService: EmployeeService, private alertService: AlertService, private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    const enclsureToViewId = Number(this.route.snapshot.paramMap.get('enclosureId'));
    this.loadEnclosureToView(enclsureToViewId);
  }
  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  editingOn() {
    this.editing = true;
    this.enclosureEditInfo = this.formBuilder.group({
      name: [this.enclosureToView.name, [Validators.required]],
      description: [this.enclosureToView.description],
      publicInformation: [this.enclosureToView.publicInfo],
      picture: ['']
    });
    this.uploadedPicture = this.enclosureToView.picture;
  }

  saveChanges() {
    if (!this.enclosureEditInfo.valid) {
      this.alertService.info('Name cannot be empty');
    } else {
      const enclosureEdited: Enclosure = new Enclosure(
        this.enclosureToView.id,
        this.enclosureEditInfo.controls.name.value,
        this.enclosureEditInfo.controls.description.value,
        this.enclosureEditInfo.controls.publicInformation.value,
        this.uploadedPicture);

      this.enclosureService.editEnclosure(enclosureEdited).subscribe(
        () => {
          DEBUG_LOG('edited enclosure' + this.enclosureToView);
          this.editing = false;
          this.clearForm();
          this.loadEnclosureToView(this.enclosureToView.id);
          this.backClicked();
        },
        error => {
          DEBUG_LOG('Failed to edit enclosure');
          this.backClicked();
          this.alertService.alertFromError(error, {}, 'EnclosureView component: editEnclosure()');
        }
      );
    }
  }
  backClicked() {
    this._location.back();
  }
  private clearForm() {
    this.enclosureEditInfo.reset();
    this.submittedEnclosure = false;
    this.uploadedPicture = null;
  }

  cancelChanges() {
    this.editing = false;
    this.clearForm();
    this.backClicked();
  }

  OnImageFileSelected(event) {
    const files = event.target.files;
    const file = files[0];
    const maxSize = 259000000;
    const acceptedImageTypes = ['image/jpeg', 'image/png'];

    if (files && file) {
      if (file.size > maxSize) {
        this.alertService.warn('File is to large. Max size is: ' + maxSize / 1000 + ' MB.',
          {}, 'Enclosure component: OnImageFileSelected');
      } else {
        if (!acceptedImageTypes.includes(file.type)) {
          this.alertService.warn('File has to either be jpeg or png.' + maxSize / 1000 + ' MB.',
            {}, 'Enclosure component: OnImageFileSelected');
        } else {
          const reader = new FileReader();

          reader.onload = this._handleReaderLoaded.bind(this);
          this.fileType = 'data:' + file.type.toString() + ';base64,';
          reader.readAsBinaryString(file);
        }
      }
    }
  }
  // From: https://stackoverflow.com/questions/42482951/converting-an-image-to-base64-in-angular-2
  // Converts the resulting binary String of the reader to base 64
  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    this.uploadedPicture = this.fileType + btoa(binaryString);
  }

  loadEnclosureToView(enclosureId: number) {
    this.enclosureService.getById(enclosureId).subscribe(
      (enclosure: Enclosure) => {
        this.enclosureToView = enclosure;
        DEBUG_LOG('Loaded enclosure id: ' + enclosure.id);
        if (this.enclosureToView == null) {
          this.alertService.error('Enclosure with such id does not exist.', {}, 'loadEnclosureToView()');
        }
        this.editingOn();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'EnclosureView component: loadEnclosureToView()');
      }
    );
  }
}
