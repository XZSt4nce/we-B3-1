import React, {useContext, useEffect} from 'react';
import {SignIn} from "../components/SignIn";
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {SignUp} from "../components/SignUp";

const MainPage = () => {
    const {user} = useContext(Context);
    const nav = useHistory();

    useEffect(() => {
        if (user.login) {
            nav.push("/market");
        }
    }, [user]);

    return (
        <>
            <SignIn />
            <SignUp />
        </>
    );
};

export default MainPage;