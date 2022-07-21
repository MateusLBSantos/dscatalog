import Navbar from "components/Navbar";
import Home from "pages/Home";
import { BrowserRouter,Route, Switch } from "react-router-dom";



const Routes = () => (
      <BrowserRouter>
      <Navbar/>
      <Switch>
        <Route path="/">
            <Home/>
        </Route>
      </Switch>
      </BrowserRouter>
    );


export default Routes;