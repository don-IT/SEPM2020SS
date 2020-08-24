import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnclosureViewComponent } from './enclosure-view.component';

describe('EnclosureViewComponent', () => {
  let component: EnclosureViewComponent;
  let fixture: ComponentFixture<EnclosureViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EnclosureViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnclosureViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
