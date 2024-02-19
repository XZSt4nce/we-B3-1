import React, {useContext, useEffect} from 'react';
import {useParams} from "react-router-dom";
import {Context} from "../../core/ContextWrapper";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {User} from "../kit/User";

export const UserPage = () => {
    const {id} = useParams();
    const {user, users} = useContext(Context);
    const userStruct = users[id];
    const nav = useHistory();

    useEffect(() => {
        if (!user.login) {
            nav.push("/");
        }
        if (user.login === id) {
            nav.push("/main");
        }
    }, [user, id]);

    return !userStruct ? (
        <p>404. Страница не найдена</p>
    ) : (
        <User userStruct={userStruct} />
    );
};