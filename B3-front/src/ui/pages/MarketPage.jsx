import React, {useContext, useEffect} from 'react';
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {Profile} from "../components/Profile";

const MarketPage = () => {
    const {user} = useContext(Context);
    const nav = useHistory();

    useEffect(() => {
        if (!user.login) {
            nav.push("/");
        }
    }, [user]);

    return (
        <>
           <Profile />
        </>
    );
};

export default MarketPage;