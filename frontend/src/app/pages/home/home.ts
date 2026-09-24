import { Component } from '@angular/core';
import { SectionPhoto } from '../../components/section-photo/section-photo';

@Component({
  selector: 'app-home',
  imports: [SectionPhoto],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
