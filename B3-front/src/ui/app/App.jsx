import {BrowserRouter, Route, Switch} from "react-router-dom";
import {ContextWrapper} from "../../core/ContextWrapper";
import {Layout} from "../components/HOCs/Layout";
import {Routes} from "../../constants/Routes";
import React from "react";

function App() {
  return (
    <BrowserRouter>
      <Switch>
        <ContextWrapper>
          <Layout>
              {
                  Routes.map((route, idx) => (
                      <Route path={route.path} exact key={idx}>
                          <route.page />
                      </Route>
                  ))
              }
          </Layout>
        </ContextWrapper>
      </Switch>
    </BrowserRouter>
  );
}

export default App;
