import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskInfoUpdateComponent } from './task-info-update.component';

describe('TaskInfoUpdateComponent', () => {
  let component: TaskInfoUpdateComponent;
  let fixture: ComponentFixture<TaskInfoUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskInfoUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskInfoUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
