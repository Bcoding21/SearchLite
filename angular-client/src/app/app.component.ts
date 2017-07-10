import { Component } from '@angular/core';
import { SearchService } from './search.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  term: string;
  result: string;
  listItems: string[] = ['howard', 'west'];

  constructor(private searchService: SearchService) {
  console.log(this.listItems);
  }

  search() {
    this.searchService.search(this.term)
      .subscribe(data => {
        this.result = JSON.stringify(data);
      });
  }
}
