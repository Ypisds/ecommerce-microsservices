import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LucideCircleUserRound, LucideShoppingCart } from '@lucide/angular';
import { LucideSearch } from '@lucide/angular';
import { LucideHeart } from '@lucide/angular';


@Component({
  selector: 'app-header',
  imports: [RouterLink, LucideCircleUserRound, LucideSearch, LucideHeart, LucideShoppingCart],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
 
}
