import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {Storages, urlParam} from './util/utils';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  constructor(private router: Router) {
    let params = urlParam(location.search);
    Storages.SESSION.user(+params.user);
    this.router.navigateByUrl('/main').finally();
  }
}
