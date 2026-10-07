import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeFilter } from './home-filter';

describe('HomeFilter', () => {
  let component: HomeFilter;
  let fixture: ComponentFixture<HomeFilter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeFilter],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeFilter);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
