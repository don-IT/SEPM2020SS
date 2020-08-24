import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ZooInfoService} from '../../services/zooInfo.service';
import {Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {ZooInfo} from '../../dtos/zooInfo';
import {Location} from '@angular/common';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Enclosure} from '../../dtos/enclosure';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;


@Component({
  selector: 'app-home-edit',
  templateUrl: './home-edit.component.html',
  styleUrls: ['./home-edit.component.css']
})
export class HomeEditComponent implements OnInit {

  zooInformation: ZooInfo;
  zooInformationEditInfo: FormGroup;
  editing: boolean = false;
  uploadedPicture: string;
  submittedZooInfo: boolean;
  private fileType: string;

  constructor(public authService: AuthService, public zooInfoService: ZooInfoService,
              private router: Router, private alertService: AlertService, private _location: Location,
              private formBuilder: FormBuilder) { }



  ngOnInit(): void {
    this.getZooInformation();
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  getZooInformation() {

    this.zooInfoService.displayZooInfo().subscribe(
      zooInfo => {
       // DEBUG_LOG('zooInfo: ' + JSON.stringify(zooInfo));
        this.zooInformation = zooInfo;
        this.editingOn();
      },
      error => {
        DEBUG_LOG('Failed to load zoo information');
        this.alertService.alertFromError(error, {}, 'getZooInformation');
      }
    );
  }

  editingOn() {
    DEBUG_LOG('Form Group to be created, NAME: ' + this.zooInformation.name);
    this.zooInformationEditInfo = this.formBuilder.group({
      name: [this.zooInformation.name],
      address: [this.zooInformation.address],
      publicInformation: [this.zooInformation.publicInfo],
      workTimeStart: [this.zooInformation.workTimeStart],
      workTimeEnd: [this.zooInformation.workTimeEnd],
      picture: ['']
    });
    this.uploadedPicture = this.zooInformation.picture;
    this.editing = true;
    DEBUG_LOG('Form Group created');
  }


  saveChanges() {
    DEBUG_LOG('ZooInfo changes to be saved');
    const zooInfoEdited: ZooInfo = new ZooInfo(
      this.zooInformation.id,
      this.zooInformationEditInfo.controls.name.value,
      this.zooInformationEditInfo.controls.address.value,
      this.zooInformationEditInfo.controls.publicInformation.value,
      this.zooInformationEditInfo.controls.workTimeStart.value,
      this.zooInformationEditInfo.controls.workTimeEnd.value,
      this.uploadedPicture
    );

    /* DEBUG_LOG('ZooInfo changes: id: ' + zooInfoEdited.id + ' name: ' + zooInfoEdited.name + ' address: ' + zooInfoEdited.address
    + ' publicInformation: ' + zooInfoEdited.publicInfo + ' workTimeStart: ' + zooInfoEdited.workTimeStart + 'workTimeEnd: '
      + zooInfoEdited.workTimeEnd + 'uploadedPicture: ' + zooInfoEdited.picture); */

    this.zooInfoService.editZooInfo(zooInfoEdited).subscribe(
      () => {
        // DEBUG_LOG('edited ZooInfo' + this.zooInformation);
        this.editing = false;
        this.clearForm();
        this.getZooInformation();
        this.backClicked();
      },
      error => {
        DEBUG_LOG('Failed to edit Zoo Information');
        this.backClicked();
        this.alertService.alertFromError(error, {}, 'home-edit component: editEnclosure()');
      }
    );
  }

  backClicked() {
    this._location.back();
  }

  cancelChanges() {
    this.editing = false;
    this.clearForm();
    this.backClicked();
  }

  private clearForm() {
    this.zooInformationEditInfo.reset();
    this.submittedZooInfo = false;
    this.uploadedPicture = null;
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

  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    this.uploadedPicture = this.fileType + btoa(binaryString);

  }

}
