import React, {useContext} from 'react';
import MainPage from "../pages/MainPage";
import {Routes} from "../../constants/Routes";
import {Route} from "react-router-dom";
import {RoutesParams} from "../../constants/RoutesParams";
import {Context} from "../../core/ContextWrapper";

export const AppRoutes = () => {
    const {user} = useContext(Context);

    return (
        <>
            {!user.login ? (
                <MainPage />
            ) : (
                <>
                    {
                        Routes.map((route, idx) => (
                            <Route path={route.path} exact key={idx}>
                                <route.page />
                            </Route>
                        ))
                    }
                    {
                        RoutesParams.map((route, idx) => (
                            <Route path={route.path} exact key={idx}>
                                <Route index element={route.indexPage} />
                                <Route path={route.params} element={route.paramsPage} />
                            </Route>
                        ))
                    }
                </>
            )}
        </>
    );
};