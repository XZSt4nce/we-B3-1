import React, {useContext, useEffect} from 'react';
import {WhiteBlock} from "./HOCs/WhiteBlock";
import {useParams} from "react-router-dom";
import {Context} from "../../core/ContextWrapper";
import {UserRole} from "../../constants/UserRole";
import {ProductsList} from "./ProductsList";
import {useHistory} from "react-router-dom/cjs/react-router-dom";

export const User = () => {
    const {id} = useParams();
    const {user, users, organizations, products} = useContext(Context);
    const userStruct = users[id];
    const nav = useHistory();

    useEffect(() => {
        console.log(user);
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
        <WhiteBlock title={"Пользователь: " + id}>
            <p>Название организации: {organizations[userStruct.organizationKey].title}</p>
            <p>Описание организации: {organizations[userStruct.organizationKey].description}</p>
            <p>Регионы распространения: {userStruct.regions}</p>
            <p>ФИО: {userStruct.fullName}</p>
            <p>E-mail: {userStruct.email}</p>
            <p>Роль: {UserRole[userStruct.role]}</p>
            <ProductsList products={users[id].productsProvided.map(product => products[product])} />
        </WhiteBlock>
    );
};