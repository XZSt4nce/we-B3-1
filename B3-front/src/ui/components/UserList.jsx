import React, {useContext} from 'react';
import {Context} from "../../core/ContextWrapper";
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {EmptyListPlug} from "./EmptyListPlug";
import {User} from "../kit/User";

export const UserList = () => {
    const {users} = useContext(Context);

    return (
        <WhiteBlock title={"Пользователи"}>
            {users.length === 0 ? <EmptyListPlug /> : users.map((user, idx) => (
                <User user={user} key={idx} />
            ))}
        </WhiteBlock>
    );
};