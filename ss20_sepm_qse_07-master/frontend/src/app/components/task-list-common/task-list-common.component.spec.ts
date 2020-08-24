import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskListCommonComponent } from './task-list-common.component';

describe('TaskListCommonComponent', () => {
  let component: TaskListCommonComponent;
  let fixture: ComponentFixture<TaskListCommonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskListCommonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListCommonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
