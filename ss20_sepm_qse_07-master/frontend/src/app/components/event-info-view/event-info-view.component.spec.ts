import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventInfoViewComponent } from './event-info-view.component';

describe('EventInfoViewComponent', () => {
  let component: EventInfoViewComponent;
  let fixture: ComponentFixture<EventInfoViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventInfoViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventInfoViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
