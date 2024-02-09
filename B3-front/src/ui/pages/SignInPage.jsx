import React, {useContext, useEffect} from 'react';
import {SignIn} from "../components/SignIn";
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";

const SignInPage = () => {
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
        </>
    );
};

export default SignInPage;