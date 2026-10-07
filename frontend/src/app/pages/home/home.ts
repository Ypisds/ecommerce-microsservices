import { Component } from '@angular/core';
import { SectionPhoto } from '../../components/section-photo/section-photo';
import { HomeFilter } from '../../components/home-filter/home-filter';
import { GridCardProduct } from '../../components/grid-card-product/grid-card-product';

@Component({
  selector: 'app-home',
  imports: [SectionPhoto, HomeFilter, GridCardProduct],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
