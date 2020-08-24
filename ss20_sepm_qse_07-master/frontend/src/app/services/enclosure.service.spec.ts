import { TestBed } from '@angular/core/testing';

import { EnclosureService } from './enclosure.service';

describe('EnclosureService', () => {
  let service: EnclosureService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnclosureService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
