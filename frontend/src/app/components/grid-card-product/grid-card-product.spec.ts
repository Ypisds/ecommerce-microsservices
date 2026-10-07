import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GridCardProduct } from './grid-card-product';

describe('GridCardProduct', () => {
  let component: GridCardProduct;
  let fixture: ComponentFixture<GridCardProduct>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GridCardProduct],
    }).compileComponents();

    fixture = TestBed.createComponent(GridCardProduct);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
