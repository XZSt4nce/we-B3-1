import React, {useContext} from 'react';
import {Context} from "../../core/ContextWrapper";
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {EmptyListPlug} from "./EmptyListPlug";
import {User} from "../kit/User";

export const UserList = () => {
    const {users} = useContext(Context);
    const filteredUsers = Object.values(users).filter((usr) => usr.role !== "OPERATOR");

    return (
        <WhiteBlock title={"Пользователи"} className={"d-flex flex-column gap-3"}>
            {filteredUsers.length === 0 ? <EmptyListPlug /> : filteredUsers.map((userStruct, idx) => (
                <User userStruct={userStruct} key={idx} />
            ))}
        </WhiteBlock>
    );
};