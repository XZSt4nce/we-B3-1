import {BrowserRouter, Switch} from "react-router-dom";
import {ContextWrapper} from "../../core/ContextWrapper";
import {Layout} from "../components/HOCs/Layout";
import {AppRoutes} from "./AppRoutes";

function App() {
  return (
    <BrowserRouter>
      <Switch>
        <ContextWrapper>
          <Layout>
              <AppRoutes />
          </Layout>
        </ContextWrapper>
      </Switch>
    </BrowserRouter>
  );
}

export default App;
