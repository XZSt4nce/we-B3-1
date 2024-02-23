import React, {useContext} from 'react';
import {Context} from "../../core/ContextWrapper";
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {EmptyListPlug} from "./EmptyListPlug";
import {User} from "../kit/User";

export const UserList = () => {
    const {users} = useContext(Context);

    return (
        <WhiteBlock title={"Пользователи"}>
            {Object.keys(users).length === 0 ? <EmptyListPlug /> : Object.keys(users).map((userKey, idx) => (
                <User userStruct={users[userKey]} key={idx} />
            ))}
        </WhiteBlock>
    );
};