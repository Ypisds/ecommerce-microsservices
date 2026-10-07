import { Component } from '@angular/core';
import { CardProduct } from '../card-product/card-product';

@Component({
  selector: 'app-grid-card-product',
  imports: [CardProduct],
  templateUrl: './grid-card-product.html',
  styleUrl: './grid-card-product.css',
})
export class GridCardProduct {}
