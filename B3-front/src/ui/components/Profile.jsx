import React, {useContext} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {Context} from "../../core/ContextWrapper";

export const Profile = () => {
    const {user} = useContext(Context);

    return (
        <WhiteBlock title={"Профиль"}>
            <p>Логин: {user.login}</p>
            <p>Баланс: {user.balance}</p>
            <p>ФИО: {user.fullName}</p>
            <p>Почта: {user.email}</p>
            <p>Регион(-ы): {user.regions.join(", ")}</p>
            <p>Роль: {user.role}</p>
            <p>{user.activated ? "Активирован" : "Не активирован"}</p>
            {user.blocked && (<p>Заблокирован</p>)}
        </WhiteBlock>
    );
};