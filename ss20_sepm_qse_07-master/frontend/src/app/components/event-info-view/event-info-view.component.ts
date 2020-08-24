import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Task} from '../../dtos/task';
import {Animal} from '../../dtos/animal';
import {Enclosure} from '../../dtos/enclosure';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {FormBuilder} from '@angular/forms';
import {EnclosureService} from '../../services/enclosure.service';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-event-info-view',
  templateUrl: './event-info-view.component.html',
  styleUrls: ['./event-info-view.component.css']
})
export class EventInfoViewComponent implements OnInit {

  @Input() stringId: String = 'default';
  @Input() task: Task;
  @Input() index;

  animal: Animal;
  enclosure: Enclosure;

  componentId = 'TaskInfoUpdate';

  @Output() reloadTasks = new EventEmitter();
  @Output() toggleClickPropagationEvent = new EventEmitter();

  @ViewChild('modalToggleBtn')
  modalToggle: ElementRef<HTMLElement>;

  private modalIsOpen: boolean = false;

  constructor(private animalService: AnimalService, private taskService: TaskService,
              private enclosureService: EnclosureService, private alertService: AlertService) { }

  ngOnInit(): void {
    this.getSubject();
  }

  getSubject() {
    if (this.task.animalTask === true) {
      this.animalService.getAnimalById(this.task.subjectId).subscribe(
        animal => {
          this.animal = animal;
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskInfoUpdate: getSubject()');
        }
      );
    } else if (this.task.animalTask === false) {
      this.enclosureService.getById(this.task.subjectId).subscribe(
        enclosure => {
          this.enclosure = enclosure;
        },
        error => {
          this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskInfoUpdate: getSubject()');
        }
      );
    }
  }

  toggleModal() {
    //   this.stopClickPropagationEvent.emit();
    DEBUG_LOG('Toggle Modal');
    const modalToggleElement: HTMLElement = this.modalToggle.nativeElement;
    if (this.modalIsOpen) {
      this.closeModal(modalToggleElement);
    } else if (!this.modalIsOpen) {
      this.openModal(modalToggleElement);
    }

    // modalToggleElement.click();
    // this.enableClickPropagationEvent.emit();
  }

  closeModal(modalToggleElement: HTMLElement) {
    DEBUG_LOG('close modal, modalIsOpen = ' + this.modalIsOpen);
    if (this.modalIsOpen) {
      modalToggleElement.click();
      this.modalIsOpen = false;
      this.toggleClickPropagationEvent.emit();
    }

  }

  openModal(modalToggleElement: HTMLElement) {
    DEBUG_LOG('open modal, modalIsOpen = ' + this.modalIsOpen);
    if (!this.modalIsOpen) {
      this.toggleClickPropagationEvent.emit();
      this.modalIsOpen = true;
      modalToggleElement.click();
    }
  }

}
