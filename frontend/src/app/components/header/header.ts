import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LucideCircleUserRound, LucideShoppingCart, LucideMenu } from '@lucide/angular';
import { LucideSearch } from '@lucide/angular';
import { LucideHeart } from '@lucide/angular';


@Component({
  selector: 'app-header',
  imports: [RouterLink, LucideCircleUserRound, LucideSearch, LucideHeart, LucideShoppingCart, LucideMenu],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
    isOpenAside = signal<boolean>(false);

    openAside()
    {
      this.isOpenAside.update(v => !v);
      console.log(this.isOpenAside());
    }
}
