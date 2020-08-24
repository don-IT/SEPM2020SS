import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnimalUpdateViewComponent } from './animal-update-view.component';

describe('AnimalUpdateViewComponent', () => {
  let component: AnimalUpdateViewComponent;
  let fixture: ComponentFixture<AnimalUpdateViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnimalUpdateViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnimalUpdateViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
