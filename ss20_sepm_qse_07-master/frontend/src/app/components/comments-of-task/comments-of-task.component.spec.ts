import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentsOfTaskComponent } from './comments-of-task.component';

describe('CommentsOfTaskComponent', () => {
  let component: CommentsOfTaskComponent;
  let fixture: ComponentFixture<CommentsOfTaskComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommentsOfTaskComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentsOfTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
