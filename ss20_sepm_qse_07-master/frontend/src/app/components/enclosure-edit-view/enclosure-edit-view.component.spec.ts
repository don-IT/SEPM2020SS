import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnclosureEditViewComponent } from './enclosure-edit-view.component';

describe('EnclosureEditViewComponent', () => {
  let component: EnclosureEditViewComponent;
  let fixture: ComponentFixture<EnclosureEditViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EnclosureEditViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnclosureEditViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
