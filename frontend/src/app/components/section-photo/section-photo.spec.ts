import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectionPhoto } from './section-photo';

describe('SectionPhoto', () => {
  let component: SectionPhoto;
  let fixture: ComponentFixture<SectionPhoto>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectionPhoto],
    }).compileComponents();

    fixture = TestBed.createComponent(SectionPhoto);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
