import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home/index';
import { SignInComponent } from './sign-in/sign-in.component';
import { RegisterComponent } from './register/index';
import { AuthGuard } from './guards/index';
const appRoutes: Routes = [
    { path: '', component: HomeComponent, canActivate: [AuthGuard] },
    { path: 'sign-in', component: SignInComponent },
    { path: 'register', component: RegisterComponent },

    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);