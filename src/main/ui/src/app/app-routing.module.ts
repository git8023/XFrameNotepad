import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppComponent} from "./app.component";
import {MainComponent} from "./page/main/main.component";

const routes: Routes = [

  {
    path: 'main', component: MainComponent, children: [

    ]
  },

  // 从AppComponent导航可以获取URL拼接的参数
  {path: '', component: AppComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
