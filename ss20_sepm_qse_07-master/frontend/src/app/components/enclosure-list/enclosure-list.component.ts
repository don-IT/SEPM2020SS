import {Component, Input, OnInit} from '@angular/core';
import {Enclosure} from '../../dtos/enclosure';
import {Router} from '@angular/router';

@Component({
  selector: 'app-enclosure-list',
  templateUrl: './enclosure-list.component.html',
  styleUrls: ['./enclosure-list.component.css']
})
export class EnclosureListComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('enclosures') enclosures: Enclosure[];

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  showInfo(enclosure: Enclosure) {
    this.router.navigate(['/enclosure-view/' + enclosure.id ]);
  }

}
