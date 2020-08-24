import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeEditViewComponent } from './employee-edit-view.component';

describe('EmployeeEditViewComponent', () => {
  let component: EmployeeEditViewComponent;
  let fixture: ComponentFixture<EmployeeEditViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmployeeEditViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmployeeEditViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
