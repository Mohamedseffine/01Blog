import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ErrorAlertComponent } from '@core/components/error-alert/error-alert.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ErrorAlertComponent],
  template: `
    <app-error-alert></app-error-alert>
    <router-outlet></router-outlet>
  `,
})
export class AppComponent {
  title = 'Moblogging';
}
