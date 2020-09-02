import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgZorroAntdModule, NZ_I18N, zh_CN} from 'ng-zorro-antd';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import zh from '@angular/common/locales/zh';
import {MainComponent} from './page/main/main.component';
import {DateFmtPipe} from './pipe/date-fmt.pipe';
import {DefValPipe} from './pipe/def-val.pipe';
import {InsidePipe} from './pipe/inside.pipe';
import {JsonIndexPipe} from './pipe/json-index.pipe';
import {StringFormatPipe} from './pipe/string-format.pipe';

registerLocaleData(zh);

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,

    //<editor-fold desc="pipes">
    DateFmtPipe,
    DefValPipe,
    InsidePipe,
    JsonIndexPipe,
    StringFormatPipe,
    //</editor-fold>
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
  ],
  providers: [{provide: NZ_I18N, useValue: zh_CN}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
