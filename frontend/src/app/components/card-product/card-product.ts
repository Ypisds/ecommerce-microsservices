import { Component } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-card-product',
  imports: [CurrencyPipe],
  templateUrl: './card-product.html',
  styleUrl: './card-product.css',
})
export class CardProduct {}
